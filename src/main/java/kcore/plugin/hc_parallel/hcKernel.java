package kcore.plugin.hc_parallel;

import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.aparapi.Kernel;
import com.aparapi.Range;

public class hcKernel  extends Kernel {
	private Map<String, Vector<String>> adjList;
	private Set<String> vertextList;
	private Set<String> visited;
	private int reachability[];
	private Range range;
	
	public hcKernel(Range range) {
		this.range = range;
		reachability = new int[this.range.getGlobalSize(0)];
	}
	
	public void run() {
		int index = getGlobalId();
		String vertex[] = new String[vertextList.size()] ;
		vertextList.toArray(vertex);
		reachability[index] = countChildNode(vertex[index],vertex[index]);
	}
	public int countChildNode(String node, String source) {
		int count = 0;
		visited.add(node);
		if (adjList.get(node) != null) {
			for (String vertex : adjList.get(node)) {
				if (!visited.contains(vertex)) {
					if (adjList.get(vertex) != null && adjList.get(vertex).size() > 0) {
						count = count + countChildNode(vertex, source);
						// System.out.print(count+" ");
					}
					count = count + 1;
					visited.add(vertex);
//					pushMapS(reachableList, source, vertex);
				}
			}
		}
	    System.out.println("count: "+ count );
		return count;
	}
	
	
	public Map<String, Vector<String>> getAdjList() {
		return adjList;
	}

	public void setAdjList(Map<String, Vector<String>> adjList) {
		this.adjList = adjList;
	}

	public Set<String> getVisited() {
		return visited;
	}

	public void setVisited(Set<String> visited) {
		this.visited = visited;
	}

	public Set<String> getVertextList() {
		return vertextList;
	}
	public void setVertextList(Set<String> vertextList) {
		this.vertextList = vertextList;
	}
	public int[] getReachability() {
		return reachability;
	}
	public void setReachability(int[] reachability) {
		this.reachability = reachability;
	}
	
}
