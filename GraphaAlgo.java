package algorithms;

import java.util.LinkedList;

public class GraphaAlgo {

	int vertex;
	LinkedList list[];
	
	GraphaAlgo(int vertex){
		this.vertex = vertex;
        list = new LinkedList[vertex];
		for(int i=0;i<vertex;i++) {
			list[i] = new LinkedList();
		}
	}
	
	
	private void addEdge(int src, int dest) {
		list[src].add(dest);
		list[dest].add(src);
	}
	
	private void printGraph() {
		for(int i=0;i<vertex;i++) {
			System.out.println("for head:: " + i + " edges are:: ");
			if(list[i].size() > 0)
			for(int j=0;j<list[i].size();j++) {
				System.out.print(list[i].get(j) +" ");
			}
		}
	}
	public static void main(String[] args) {
		GraphaAlgo graph = new GraphaAlgo(5);
	        graph.addEdge(0,1);
	        graph.addEdge(0, 4);
	        graph.addEdge(1, 2);
	        graph.addEdge(1, 3);
	        graph.addEdge(1, 4);
	        graph.addEdge(2, 3);
	        graph.addEdge(3, 4);
	        graph.printGraph();
	}

}
