package kcore.plugin.rcore.parallel;

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

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aparapi.Range;

import kcore.plugin.alg.param.KcoreParameters;
import kcore.plugin.hc.DirectionType;
import kcore.plugin.rcore.sequence.Edge;

public class RcoreParallel extends AbstractTask {
	// private static final Logger logger =
	// LoggerFactory.getLogger(RcoreParallel.class);

	static {
		System.setProperty("com.aparapi.executionMode", "GPU");
		System.setProperty("com.aparapi.dumpProfilesOnExit", "true");
		System.setProperty("com.aparapi.enableExecutionModeReporting", "false");
		System.setProperty("com.aparapi.enableShowGeneratedOpenCL", "false");
	}

	// path to input/output file
	// private static final String INPUT = "FigS1.txt";
	private String OUTPUT;// = "output.txt";

	// list to store edges
	private List<Edge> edgeList;
	// map to store r-core
	private int[] rCore;
	// map to store adjacency list
	private Map<Integer, ArrayList<Integer>> adjList;
	// map to store reach level
	private int[] reachLevel;
	// vertex queue
	private PriorityQueue<Vertex> vertexQueue;

	//
	private int[] reachableSource;
	private int[][] reachableList;
	//

	// sets vertex
	private Set<String> setV;

	// convert vertex string to intId
	private Map<String, Integer> vStringToInt;

	// convert vertex intId to string
	private String[] vStringArray;

	private int numberOfVertexs;
	private int numberOfEdges;

	// temp
	private Set<Integer> visited;
	private int reachListIndex = 0;
	private int[] reachListColumnIndex;

	private CyNetwork net;
	private CyTable cyTable;
	private List<CyEdge> listEdge;
	private CyTable cyTableNode;
	private List<CyNode> listNode;
	private KcoreParameters params;
	private boolean cancelled = false;

	public RcoreParallel(KcoreParameters params, String path) {
		this.params = params;
		this.OUTPUT = path;
	}

	// public static void main(String[] args) throws Exception {
	// RcoreParallel main = new RcoreParallel();
	// main.init();
	// main.readFile();
	// main.loadData();
	// long start = System.currentTimeMillis();
	// main.compute();
	// long end = System.currentTimeMillis();
	// System.out.println(end - start);
	// main.writeTextFile();
	// }
	
	public DirectionType getType(List<String> type) {
		DirectionType direction = DirectionType.UNDIRECTED;
		for (String temp : type) {
			if (temp.contains("activation") || temp.contains("expression") || temp.contains("inhibition")
					|| temp.contains("indirect_effect") || temp.contains("via_compound")
					|| temp.contains("missing_interaction") || temp.contains("phosphorylation")) {
				direction = DirectionType.DIRECTED;
			} else if (temp.contains("dissociation")) {
				direction = DirectionType.UNDIRECTED;
			}
		}

		return direction;
	}

	// initialize
	private void init() {
		edgeList = new ArrayList<>();
		adjList = new HashMap<>();
		vertexQueue = new PriorityQueue<>();
		setV = new HashSet<>();
		visited = new HashSet<>();
		vStringToInt = new HashMap<>();

		net = params.getCyNetwork();
		cyTable = net.getDefaultEdgeTable();
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

				for (int j = 0; j < subNameNode.size(); j++) {
					if (j == subNameNode.size() - 1) {

					} else {
						for (int k = j + 1; k < subNameNode.size(); k++) {
							Edge edge = new Edge(subNameNode.get(j), subNameNode.get(k), 0, 1);
							edgeList.add(edge);
						}
					}
				}
			}

		}

		for (int i = 0; i < listEdge.size(); i++) {
			// get first edge of edge table
			List<String> type = cyTable.getRow(listEdge.get(i).getSUID()).get("KEGG_EDGE_SUBTYPES", List.class);
			if (type.contains("compound")) {

			} else {
				DirectionType direction = getType(type);
				String name = cyTable.getRow(listEdge.get(i).getSUID()).get("name", String.class).trim();
				String[] subName = name.split(" ");

				ArrayList<String> firstEdge = new ArrayList<>();
				ArrayList<String> secondEdge = new ArrayList<>();
				// set first array edge
				setAr(firstEdge, secondEdge, subName[0], 1);
				// set second array edge
				setAr(firstEdge, secondEdge, subName[subName.length - 1], 2);
				// Add edge
				swap(firstEdge, secondEdge, direction);

			}
		}
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
	public void swap(ArrayList<String> ar1, ArrayList<String> ar2, DirectionType direction) {
		for (int i = 0; i < ar1.size(); i++) {
			for (int j = 0; j < ar2.size(); j++) {
				Edge edge = new Edge(ar1.get(i), ar2.get(j), direction == DirectionType.DIRECTED ? 1:0, 1);
				edgeList.add(edge);
			}
		}
	}

	// load data
	private void loadData() {
		for (Edge edge : edgeList) {
			setV.add(edge.getStartNode());
			setV.add(edge.getEndNode());
		}

		numberOfEdges = edgeList.size();
		numberOfVertexs = setV.size();

		rCore = new int[numberOfVertexs];
		Arrays.fill(rCore, -1);

		reachListColumnIndex = new int[numberOfVertexs];

		reachLevel = new int[numberOfVertexs];

		reachableSource = new int[numberOfVertexs];
		Arrays.fill(reachableSource, -1);
		reachableList = new int[numberOfVertexs][numberOfVertexs];

		encryptVertex();

		for (Edge edge : edgeList) {
			pushMapV(adjList, edge.getStartNode(), edge.getEndNode(), edge.getDirection());
		}

		for (String vertex : setV) {
			visited.clear();
			int n = countChildNode(vStringToInt.get(vertex), vStringToInt.get(vertex));
			reachLevel[vStringToInt.get(vertex)] = n;

			vertexQueue.add(new Vertex(vertex, n));
		}
	}

	// write result to output.txt
	public void writeTextFile() throws Exception {

		Path path = Paths.get(OUTPUT);
		List<String> lines = new ArrayList<>();

		Map<String, Integer> rCoreResult = new HashMap<>();
		for (int i = 0; i < rCore.length; i++) {
			rCoreResult.put(vStringArray[i], rCore[i] + 1);
		}

		// sort map by value
		Map<String, Integer> sortedMap = MapComparator.sortByValue(rCoreResult);
		lines.add("Node\tRCore");
		for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
			lines.add(String.format("%s\t%d", entry.getKey(), entry.getValue()));
		}

		// extend
//		lines.add("Start\tEnd\tWeight");
//		for (Edge edge : edgeList) {
//			lines.add(String.format("%s\t%s\t%d", edge.getStartNode(), edge.getEndNode(), edge.getWeight()));
//		}

		Files.write(path, lines);
	}

	// push value to map
	private void pushMapV(Map<Integer, ArrayList<Integer>> adjList, String start, String end, int weight) {
		if (!adjList.containsKey(vStringToInt.get(start))) {
			adjList.put(vStringToInt.get(start), new ArrayList<>());
		}
		adjList.get(vStringToInt.get(start)).add(vStringToInt.get(end));
		if (weight == 0) {
			if (!adjList.containsKey(vStringToInt.get(end))) {
				adjList.put(vStringToInt.get(end), new ArrayList<>());
			}
			adjList.get(vStringToInt.get(end)).add(vStringToInt.get(start));
		}
	}

	private void addReachableVertex(int[] reachableSource, int[][] reachableList, int start, int end) {
		int containIndex = isContainSource(reachableSource, start);
		if (containIndex == -1) {
			reachableSource[reachListIndex] = start;

			reachableList[reachableSource[reachListIndex]][reachListColumnIndex[reachListIndex]] = end;
			reachListColumnIndex[reachListIndex]++;
			reachListIndex++;
		} else {
			reachableList[reachableSource[containIndex]][reachListColumnIndex[containIndex]] = end;
			reachListColumnIndex[containIndex]++;
		}
	}

	private int isContainSource(int[] reachableSource, int start) {
		for (int ro = 0; ro < reachableSource.length; ro++) {
			if (reachableSource[ro] == start) {
				return ro;
			}
		}
		return -1;
	}

	private boolean containReachValue(int row, int value) {
		for (int c = 0; c < reachableList[row].length; c++) {
			if (reachableList[row][c] == value) {
				return true;
			}
		}
		return false;
	}

	private int countChildNode(int node, int source) {
		int count = 0;
		visited.add(node);
		if (adjList.get(node) != null) {
			for (Integer vertex : adjList.get(node)) {
				if (!visited.contains(vertex)) {
					if (adjList.get(vertex) != null && adjList.get(vertex).size() > 0) {
						count = count + countChildNode(vertex, source);
						// System.out.print(count+" ");
						// count number of child node of vertex that can
						// controll
					}
					count = count + 1;
					visited.add(vertex);
					addReachableVertex(reachableSource, reachableList, source, vertex);
				}
			}
		}
		return count;
	}

	// compute
	public void compute() {
		int r = 0;
		// BFS traverse
		while (!vertexQueue.isEmpty()) {
			Vertex current = vertexQueue.poll();
			String currentVertex = current.getVertex();
			if (reachLevel[vStringToInt.get(currentVertex)] < current.getDegree()) {
				continue;
			}

			r = Math.max(r, reachLevel[vStringToInt.get(currentVertex)]);

			rCore[vStringToInt.get(currentVertex)] = r;
			// System.out.println(currentVertex + ": " + r);

			// GPU
			final Range range;
			final RCoreKernel rCoreKernel;
			int[] result;
			if (adjList.get(vStringToInt.get(currentVertex)) != null
					&& reachLevel[vStringToInt.get(currentVertex)] > 0) {

				int adjListV[] = convertIntegers(adjList.get(vStringToInt.get(currentVertex)));
				range = Range.create(adjListV.length);
				rCoreKernel = new RCoreKernel(rCore, adjListV, reachLevel, reachableSource, reachableList, 1);

				rCoreKernel.execute(range);
				reachLevel = rCoreKernel.getReachability();

				result = rCoreKernel.getResult();

				rCoreKernel.dispose();

				for (int x : result) {
					vertexQueue.add(new Vertex(vStringArray[x], reachLevel[x]));
				}

				// Arrays.stream(result).forEach(x -> {
				// vertexQueue.add(new Vertex(vStringArray[x], reachLevel[x]));
				// });

			} else if (reachLevel[vStringToInt.get(currentVertex)] == 0) {

				range = Range.create(reachableSource.length);
				rCoreKernel = new RCoreKernel(vStringToInt.get(currentVertex), reachLevel, reachableSource,
						reachableList, 2);

				rCoreKernel.execute(range);
				reachLevel = rCoreKernel.getReachability();

				result = rCoreKernel.getResult();

				rCoreKernel.dispose();

				for (int x : result) {
					vertexQueue.add(new Vertex(vStringArray[x], reachLevel[x]));
				}

				// Arrays.stream(result).forEach(x -> {
				// vertexQueue.add(new Vertex(vStringArray[x], reachLevel[x]));
				// });
			}

		}

		System.out.println("R-Core: " + r);
	}

	private int[] convertIntegers(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}

	private void encryptVertex() {
		int id = 0;
		int length = setV.size();
		vStringArray = new String[length];
		for (String s : setV) {
			vStringToInt.put(s, id);
			vStringArray[id] = s;
			id++;
		}
	}

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
		taskMonitor.setStatusMessage("Computing R-core GPU ....");
		compute();

		taskMonitor.setProgress(0.8);
		taskMonitor.setStatusMessage("Write result....");
		writeTextFile();

		taskMonitor.setProgress(0.9);
		// JOptionPane.showMessageDialog(null,
		// "Compute R-core GPU Success, open text file to see the result!",
		// "Infor", JOptionPane.INFORMATION_MESSAGE);

		taskMonitor.setProgress(1.0);
		taskMonitor.setStatusMessage("Compute success!");

	}

	@Override
	public void cancel() {
		super.cancel();
		cancelled = true;
//		if (cancelled == true) {
//			JOptionPane.showMessageDialog(null, "Compute R-core GPU Success, open text file to see the result!",
//					"Infor", JOptionPane.INFORMATION_MESSAGE);
//		}
	}
}
