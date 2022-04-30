package kcore.plugin.parallel;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import com.aparapi.Kernel;

public class KCoreKernel extends Kernel {
	//k-core
	private int l;
	// number of vertex
	private int i;
	// map to store adjacency list
	private Map<String, Vector<String>> adjList;
	// map to store degree
	private Map<String, Integer> degrees;
	//array list to store adjbuff
	private ArrayList<String> vertexBuff;
	
	public KCoreKernel() {
		super();
	}

	@Override
	public void run() {
		int index = getGlobalId();
		if(index < vertexBuff.size()) {
			String vertex = vertexBuff.get(index);
			Vector<String> adjListV = adjList.get(vertex);
			for (String adjName : adjListV) {
				int adjDeg = degrees.get(adjName);
				if(adjDeg > l) {
					adjDeg = adjDeg - 1;
					degrees.replace(adjName, adjDeg);
					if(adjDeg == l) {
						vertexBuff.add(adjName);
					}
					if(adjDeg < l) {
						adjDeg = adjDeg + 1;
						degrees.replace(adjName, adjDeg);
					}
				}
			}
		}
	}

	public int getVisitedVertex() {
		return vertexBuff.size();
	}
	
	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getL() {
		return l;
	}

	public void setL(int l) {
		this.l = l;
	}

	public Map<String, Vector<String>> getAdjList() {
		return adjList;
	}

	public void setAdjList(Map<String, Vector<String>> adjList) {
		this.adjList = adjList;
	}

	public Map<String, Integer> getDegrees() {
		return degrees;
	}

	public void setDegrees(Map<String, Integer> degrees) {
		this.degrees = degrees;
	}

	public ArrayList<String> getVertexBuff() {
		return vertexBuff;
	}

	public void setVertexBuff(ArrayList<String> vertexBuff) {
		this.vertexBuff = vertexBuff;
	}
}
