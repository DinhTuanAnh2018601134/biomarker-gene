package kcore.plugin.rcore.parallel;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.aparapi.Kernel;

public class RCoreKernel extends Kernel {
	//R-core
	private int l;
	// number of vertex
	private int i;
	// map to store adjacency list
	private Map<String, Vector<String>> adjList;
	// map to store reachability
	private Map<String, Integer> reachability;
	//array list to store adjbuff
	private ArrayList<String> vertexBuff;
	private ArrayList<String> vertexBuff1;
	private Set<String> vertexList;
	
	public RCoreKernel() {
		super();
	}

	@Override
	public void run() {
		int index = getGlobalId();
		if(index < vertexBuff.size()) {
			String vertex = vertexBuff.get(index);
			Vector<String> adjListV = adjList.get(vertex);
			for (String vert : adjListV) {
				int adjRea = reachability.get(vert);
				if(adjRea > l) {
					adjRea--;
					reachability.replace(vert, adjRea);
					if(adjRea == l) {
						vertexBuff.add(vert);
//						vertexBuff1.add(vert);
					}
					if(adjRea < l) {
						reachability.replace(vert, adjRea++);
					}
				}
			}
		}
	}
	
	public ArrayList<String> getVertexBuff1() {
		return vertexBuff1;
	}

	public void setVertexBuff1(ArrayList<String> vertexBuff1) {
		this.vertexBuff1 = vertexBuff1;
	}

	public Set<String> getVertexList() {
		return vertexList;
	}

	public void setVertexList(Set<String> vertexList) {
		this.vertexList = vertexList;
	}

	public int getVisitedVertex() {
		return vertexBuff.size();
	}

	public int getL() {
		return l;
	}

	public void setL(int l) {
		this.l = l;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public Map<String, Vector<String>> getAdjList() {
		return adjList;
	}

	public void setAdjList(Map<String, Vector<String>> adjList) {
		this.adjList = adjList;
	}

	public Map<String, Integer> getReachability() {
		return reachability;
	}

	public void setReachability(Map<String, Integer> reachability) {
		this.reachability = reachability;
	}

	public ArrayList<String> getVertexBuff() {
		return vertexBuff;
	}

	public void setVertexBuff(ArrayList<String> vertexBuff) {
		this.vertexBuff = vertexBuff;
	}

	
}
