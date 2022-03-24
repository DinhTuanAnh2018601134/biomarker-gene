package kcore.plugin.parallel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import javax.swing.JOptionPane;

//import org.apache.poi.hssf.usermodel.HSSFCell;
//import org.apache.poi.hssf.usermodel.HSSFRow;
//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.commons.math3.stat.inference.TestUtils;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kcore.plugin.alg.param.KcoreParameters;
import kcore.plugin.alg.param.NetFilteringMethod;
import kcore.plugin.rcore.sequence.Edge;
import kcore.plugin.service.ServicesUtil;

import com.aparapi.Range;

public class KcoreParallel extends AbstractTask {
	private static final Logger logger = LoggerFactory.getLogger(KcoreParallel.class);
	static {
		System.setProperty("com.aparapi.executionMode", "GPU");
		System.setProperty("com.aparapi.dumpProfilesOnExit", "true");
		System.setProperty("com.aparapi.enableExecutionModeReporting", "false");
		System.setProperty("com.aparapi.enableShowGeneratedOpenCL", "false");
	}

	// path to input/output file
	// private static final String INPUT = "wikivote.txt";
	private String OUTPUT;// =
							// "D:/Project/docx/cytoscape_data/kcore_parallel.txt";

	// list to store edges
	private List<Edge> edgeList;
	// map to store k-core
	private int[] kCore;
	// map to store adjacency list
	private Map<Integer, ArrayList<Integer>> adjList;
	// map to store degree
	private int[] degrees;
	// vertex queue
	private PriorityQueue<Vertex> vertexQueue;

	// sets vertex
	private Set<String> setV;

	// convert vertex string to intId
	private Map<String, Integer> vStringToInt;

	// convert vertex intId to string
	private String[] vStringArray;

	private int numberOfVertexs;
	private int numberOfEdges;

	private CyNetwork net;
	private CyTable cyTable;
	private List<CyEdge> listEdge;
	private CyTable cyTableNode;
	private List<CyNode> listNode;
	private KcoreParameters params;
	private boolean cancelled = false;

	public KcoreParallel(KcoreParameters params, String path) {
		this.params = params;
		this.OUTPUT = path;
	}

	// initialize
	public void init() {
		edgeList = new ArrayList<Edge>();
		// kCore = new HashMap<String, Integer>();
		adjList = new HashMap<>();
		// degrees = new HashMap<String, Integer>();
		vertexQueue = new PriorityQueue<Vertex>();

		setV = new HashSet<>();
		vStringToInt = new HashMap<>();

		this.net = params.getCyNetwork();
		cyTable = this.net.getDefaultEdgeTable();
		listEdge = net.getEdgeList();
		cyTableNode = this.net.getDefaultNodeTable();
		listNode = net.getNodeList();

		// neu trong suid hien tai co nhieu hon 1 nut
		for (int i = 0; i < listNode.size(); i++) {
			String subName = cyTableNode.getRow(listNode.get(i).getSUID()).get("name", String.class).trim();
			if (subName.contains("container")) {

			} else {
				List<String> subNameNode = new ArrayList<String>();
				try {
					subNameNode = cyTableNode.getRow(listNode.get(i).getSUID()).get("KEGG_ID", List.class);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Lỗi convert!", "Error", JOptionPane.ERROR_MESSAGE);
				}
				if(subNameNode != null) {
					for (int j = 0; j < subNameNode.size(); j++) {
						if (j == subNameNode.size() - 1) {

						} else {
							for (int k = j + 1; k < subNameNode.size(); k++) {
								Edge edge = new Edge(subNameNode.get(j), subNameNode.get(k), 1, 1);
								edgeList.add(edge);
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < listEdge.size(); i++) {
			// get first edge of edge table
			List<String> type = cyTable.getRow(listEdge.get(i).getSUID()).get("KEGG_EDGE_SUBTYPES", List.class);
			if(type == null) {
				String name = cyTable.getRow(listEdge.get(i).getSUID()).get("name", String.class).trim();
				String[] subName = name.split(" ");
				Edge edge = new Edge(subName[0],subName[subName.length - 1], 1, 1);
				edgeList.add(edge);
			
			}else if (type.contains("compound")) {

			} else {
				String name = cyTable.getRow(listEdge.get(i).getSUID()).get("name", String.class).trim();
				String[] subName = name.split(" ");

				ArrayList<String> firstEdge = new ArrayList<>();
				ArrayList<String> secondEdge = new ArrayList<>();
				// set first array edge
				setAr(firstEdge, secondEdge, subName[0], 1);
				// set second array edge
				setAr(firstEdge, secondEdge, subName[subName.length - 1], 2);
				// Add edge
				swap(firstEdge, secondEdge);

			}
		}
		logger.info("Init OK!");
	}

	// set Source array node of edge
	@SuppressWarnings("unchecked")
	public void setAr(ArrayList<String> ar1, ArrayList<String> ar2, String key, int type) {
		for (int k = 0; k < listNode.size(); k++) {
			String subName = cyTableNode.getRow(listNode.get(k).getSUID()).get("name", String.class).trim();
			if (subName.contains("container")) {

			} else {
				String[] subNameArray = subName.split(":");
				// lay ra entryId
				String subItem = subNameArray[subNameArray.length - 1];
				if (subItem.equals(key)) {
					// Neu entryId = edge key thi lay ra danh sach kegg id
					List<String> subNameMain = cyTableNode.getRow(listNode.get(k).getSUID()).get("KEGG_ID", List.class);
					// String nameMain = convertString(temp);
					// get node list of first edge
					// String[] subNameMain = nameMain.split(",");
					if (type == 1) {
						for (int l = 0; l < subNameMain.size(); l++) {
							ar1.add(subNameMain.get(l));
						}
					} else if (type == 2) {
						for (int l = 0; l < subNameMain.size(); l++) {
							ar2.add(subNameMain.get(l));
						}
					}

				}
			}

		}
	}

	// Compare and swap
	public void swap(ArrayList<String> ar1, ArrayList<String> ar2) {
		for (int i = 0; i < ar1.size(); i++) {
			for (int j = 0; j < ar2.size(); j++) {
				Edge edge = new Edge(ar1.get(i), ar2.get(j), 1, 1);
				edgeList.add(edge);
			}
		}
	}

	// load data
	public void loadData() {
		for (Edge edge : edgeList) {
			setV.add(edge.getStartNode());
			setV.add(edge.getEndNode());
		}

		numberOfEdges = edgeList.size();
		encryptVertex();

		for (Edge edge : edgeList) {
			pushMap(adjList, edge.getStartNode(), edge.getEndNode());
			pushMap(adjList, edge.getEndNode(), edge.getStartNode());

		}

		degrees = new int[numberOfVertexs];
		for (Map.Entry<Integer, ArrayList<Integer>> entry : adjList.entrySet()) {
			// key nay co bn ket noi
			degrees[entry.getKey()] = entry.getValue().size();
			vertexQueue.add(new Vertex(vStringArray[entry.getKey()], entry.getValue().size()));
		}
	}

	// write result to output.txt
	public void writeFile() throws Exception {

		Path path = Paths.get(OUTPUT);
		List<String> lines = new ArrayList<>();

		Map<String, Integer> kCoreResult = new HashMap<>();

		for (int i = 0; i < kCore.length; i++) {
			kCoreResult.put(vStringArray[i], kCore[i]);
		}

		// sort map by value
		Map<String, Integer> sortedMap = MapComparator.sortByValue(kCoreResult);

		lines.add("Node\tKcore");
		for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
			lines.add(String.format("%s\t%d", entry.getKey(), entry.getValue()));
		}

		// extend
//		lines.add("Start\tEnd\tWeight");
//		for (Edge edge : edgeList) {
//			lines.add(String.format("%s\t%s\t%d", edge.getStartNode(), edge.getEndNode(), edge.getWeight()));
//		}

		Files.write(path, lines);
		// writeXLSFile(sortedMap);
	}

	// push value to map
	public void pushMap(Map<Integer, ArrayList<Integer>> adjList, String start, String end) {
		if (!adjList.containsKey(vStringToInt.get(start))) {
			adjList.put(Integer.valueOf(vStringToInt.get(start)), new ArrayList<Integer>());
		}
		adjList.get(vStringToInt.get(start)).add(vStringToInt.get(end));
	}

	public void encryptVertex() {
		int id = 0;
		int length = setV.size();
		numberOfVertexs = length;
		kCore = new int[length];
		vStringArray = new String[length];
		for (String s : setV) {
			vStringToInt.put(s, id);
			vStringArray[id] = s;
			id++;
		}
		setV.clear();
	}

	// compute
	public void compute() {
		int k = 0;
		// BFS traverse
		while (vertexQueue.size() != 0) {
			Vertex current = vertexQueue.poll();
			String currentVertex = current.getVertex();
			if (degrees[vStringToInt.get(currentVertex)] < current.getDegree()) {
				continue;
			}

			k = Math.max(k, degrees[vStringToInt.get(currentVertex)]);

			kCore[vStringToInt.get(currentVertex)] = k;

			int adjListV[] = convertIntegers(adjList.get(vStringToInt.get(currentVertex)));
			Range range = Range.create(adjListV.length);
			KCoreKernel kCoreKernel = new KCoreKernel(range);
			kCoreKernel.setAdjListV(adjListV);
			kCoreKernel.setkCore(kCore);
			kCoreKernel.setDegrees(degrees);

			kCoreKernel.execute(range);

			degrees = kCoreKernel.getDegrees();
			int result[] = kCoreKernel.getResult();
			kCoreKernel.dispose();

			for (int x : result) {
				vertexQueue.add(new Vertex(vStringArray[x], degrees[x]));
			}

			// Arrays.stream(result).forEach(x -> {
			// vertexQueue.add(new Vertex(vStringArray[x], degrees[x]));
			// });
		}
		System.out.println("K-Core: " + k);
	}

	public int[] convertIntegers(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}

	// public void writeXLSFile(Map<String, Integer> result) throws IOException
	// {
	//
	// // name of excel file
	// String excelFileName = "result.xls";
	//
	// // name of sheet
	// String sheetName = "Sheet1";
	//
	// HSSFWorkbook wb = new HSSFWorkbook();
	// HSSFSheet sheet = wb.createSheet(sheetName);
	// HSSFRow row;
	// HSSFCell cell;
	//
	// // header
	// row = sheet.createRow(0);
	// cell = row.createCell(0);
	// cell.setCellValue("Node");
	// cell = row.createCell(1);
	// cell.setCellValue("Rank");
	//
	// int index = 1;
	// for (Map.Entry<String, Integer> entry : result.entrySet()) {
	// row = sheet.createRow(index++);
	//
	// cell = row.createCell(0);
	// cell.setCellValue(String.format("%s", entry.getKey()));
	//
	// cell = row.createCell(1);
	// cell.setCellValue(String.format("%d", entry.getValue()));
	// }
	//
	// FileOutputStream fileOut = new FileOutputStream(excelFileName);
	//
	// // write this workbook to an Outputstream.
	// wb.write(fileOut);
	// fileOut.flush();
	// fileOut.close();
	// }
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setProgress(0.0);
		taskMonitor.setStatusMessage("Searching modules ....");

		System.gc();

		taskMonitor.setProgress(0.1);
		taskMonitor.setStatusMessage("Initing parameters ....");
		init();

		taskMonitor.setProgress(0.3);
		taskMonitor.setStatusMessage("Load data ....");
		loadData();

		taskMonitor.setProgress(0.4);
		taskMonitor.setStatusMessage("Computing K-core GPU ....");
		compute();

		taskMonitor.setProgress(0.9);
		taskMonitor.setStatusMessage("Write result....");
		writeFile();
		// createColumn();

		taskMonitor.setProgress(1.0);
		taskMonitor.setStatusMessage("Compute success!");

	}

	@Override
	public void cancel() {
		super.cancel();
		cancelled = true;
//		if (cancelled == true) {
//			JOptionPane.showMessageDialog(null, "Compute K-core GPU Success, open text file to see the result!",
//					"Infor", JOptionPane.INFORMATION_MESSAGE);
//		}
	}
}
