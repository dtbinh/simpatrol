package tools.graph_generator;

import java.util.LinkedList;
import java.util.Random;

import util.graph.Graph;
import util.graph.GraphBuilder;


public class RandomGraphGenerator {
	private Random random;

	
	public RandomGraphGenerator() {
		random = new Random();
	}

	/**
	 * Generates a random graph with undirected edges and with the given number of 
	 * nodes/vertices. Each node of the graph has a number of neighbors randomly chosen 
	 * from the interval [minDegree; maxDegree]. The neighbors are randomly chosen
	 * from the set of all nodes. All edges have unitary weights. 
	 */
	public Graph generateUndirected(int numVertices, int minDegree, int maxDegree) {
		return generateUndirected(numVertices, minDegree, maxDegree, 1, 1);
	}

	/**
	 * Generates a random graph with undirected edges and with the given number of 
	 * nodes/vertices. Each node of the graph has a number of neighbors uniformly chosen 
	 * from the interval [minDegree; maxDegree]. The neighbors are randomly chosen
	 * from the set of all nodes. The edges are weighted with values (costs) randomly 
	 * chosen from the interval [minWeight; maxWeight].
	 */
	public Graph generateUndirected(int numVertices, int minDegree, int maxDegree, int minWeight, int maxWeight) {
		GraphBuilder g = new GraphBuilder(numVertices, false);
		
		int[] degree = new int[numVertices];
		
		int randomDegree;
		int neighbor, weight;
		boolean added;
				
		for (int v = 0; v < numVertices; v++) {
			randomDegree = intRandom(minDegree, maxDegree);
			//System.out.printf("RandomDeg[%s] = %s\n", v, randomDegree);
			
			while (degree[v] < randomDegree) {
				neighbor = intRandom(v+1, numVertices-1);
				weight = intRandom(minWeight, maxWeight);
		
				added = false;
				
				for (int x = neighbor; x < numVertices; x++) {
					if (degree[x] < maxDegree && !g.hasEdge(v, x)) {
						g.addEdge(v, x, weight);
						degree[v] ++;
						degree[x] ++;
						added = true;
						break;
					}
				}
				
				if (!added) {
					randomDegree --;
				}
			}
		}
		
		return g.toGraph("random-undirected");
	}
	
	
	public Graph generateUndirected_noGap(int numVertices, int minDegree, int maxDegree) {
		return generateUndirected_noGap(numVertices, minDegree, maxDegree, 1, 1);
	}
	
	public Graph generateUndirected_noGap(int numVertices, int minDegree, int maxDegree, int minWeight, int maxWeight) {
		GraphBuilder g = new GraphBuilder(numVertices, false);
		
		int[] degree = new int[numVertices];
		
		int randomDegree;
		int neighbor, weight;
		boolean added, connect;
		
		LinkedList<Integer> connected = new LinkedList<Integer>();
		
				
		for (int v = 0; v < numVertices; v++) {
			randomDegree = intRandom(minDegree, maxDegree);
			//System.out.printf("RandomDeg[%s] = %s\n", v, randomDegree);
			
			if(connected.size() == 0)
				connected.add(v);
			
			connect = (connected.contains(v) ? true : false);
			LinkedList<Integer> neighbourlist = new LinkedList<Integer>();
			
			
			while (degree[v] < randomDegree) {
				neighbor = intRandom(v+1, numVertices-1);
				weight = intRandom(minWeight, maxWeight);
		
				added = false;
				
				if((degree[v] == randomDegree - 1) && !connect){
					neighbor = intRandom(0, connected.size() - 1);
					for (int x = neighbor; x < connected.size(); x++) {
						if (degree[connected.get(x)] < maxDegree && !g.hasEdge(v, connected.get(x))) {	
							g.addEdge(v, connected.get(x), weight);
							degree[v] ++;
							degree[connected.get(x)] ++;
							added = true;
							connect = true;					
							break;
						}
					}
				}
				else {
					for (int x = neighbor; x < numVertices; x++) {
						if (degree[x] < maxDegree && !g.hasEdge(v, x)) {	
							g.addEdge(v, x, weight);
							degree[v] ++;
							degree[x] ++;
							added = true;
							
							neighbourlist.add(x);
							if(connected.contains(x))
								connect = true;
							break;
						}
					}
				}
				
				if (!added) {
					randomDegree --;
				}
			}
			if(connect)
				for(Integer neigh : neighbourlist)
					if(!connected.contains(neigh))
						connected.add(neigh);
		}
		
		return g.toGraph("random-undirected");
	}
	
	/**
	 * Generates a random graph with directed edges (arcs) and with the given number of 
	 * nodes/vertices. Each node of the graph has a number of successors (target nodes) 
	 * randomly chosen from the interval [minOutDegree; maxOutDegree]. These successors
	 * are randomly chosen from the set of all nodes. All edges have unitary weights. 
	 */
	public Graph generateDirected(int numVertices, int minOutDegree, int maxOutDegree) {
		return generateDirected(numVertices, minOutDegree, maxOutDegree, 1, 1);
	}

	/**
	 * Generates a random graph with directed edges (arcs) and with the given number of 
	 * nodes/vertices. Each node of the graph has a number of successors (target nodes) 
	 * randomly chosen from the interval [minOutDegree; maxOutDegree]. These successors
	 * are randomly chosen from the set of all nodes. The edges are weighted with values
	 * (costs) randomly chosen from the interval [minWeight; maxWeight].
	 */
	public Graph generateDirected(int numVertices, int minOutDegree, int maxOutDegree, int minWeight, int maxWeight) {
		GraphBuilder g = new GraphBuilder(numVertices, true);
		
		int[] outdegree = new int[numVertices];
		
		int randomDegree;
		int neighbor, neighborInc;
		int weight;
		boolean added;
				
		for (int v = 0; v < numVertices; v++) {
			randomDegree = intRandom(minOutDegree, maxOutDegree);
			//System.out.printf("RandomDeg[%s] = %s\n", v, randomDegree);
			
			while (outdegree[v] < randomDegree) {
				neighbor = intRandom(0, numVertices-1);
				weight = intRandom(minWeight, maxWeight);
		
				added = false;
				
				for (int inc = 0; inc < numVertices; inc++) {
					neighborInc = (neighbor + inc) % numVertices;
					
					if (v != neighborInc && !g.hasEdge(v,neighborInc)) {
						g.addEdge(v, neighborInc, weight);
						outdegree[v] ++;
						added = true;
						break;
					}
				}
				
				if (!added) {
					randomDegree --;
				}
			}
		}
		
		return g.toGraph("random-directed");
	}

	/**
	 * Generates a random complete graph (i.e. an undirected graph with all possible edges) 
	 * with the given number of nodes/vertices. All edges have unitary weights. 
	 */
	public Graph generateComplete(int numVertices) {
		return generateComplete(numVertices, 1, 1);
	}

	/**
	 * Generates a random complete graph (i.e. an undirected graph with all possible edges) 
	 * with the given number of nodes/vertices. The edges are weighted with values (costs) 
	 * randomly chosen from the interval [minWeight; maxWeight].
	 */
	public Graph generateComplete(int numVertices, int minWeight, int maxWeight) {
		GraphBuilder graph = new GraphBuilder(numVertices, false);
		int capacity;
		
		for (int v = 0; v < numVertices; v++) {
			for (int x = v+1; x < numVertices; x++) {
				capacity = intRandom(minWeight, maxWeight);
				
				graph.addEdge(v, x, capacity);
			}
		}
		
		return graph.toGraph("random-complete");
	}

	// auxiliary function
	private int intRandom(int from, int to) {
		if (from > to) {
			return from;
		} else {
			return from + random.nextInt(to - from + 1);
		}
	}

	
	public static void main(String[] args) {
		RandomGraphGenerator generator = new RandomGraphGenerator();
		
		Graph graph = generator.generateUndirected_noGap(1000, 1, 7, 2, 22);
		
//		for (Node n : graph.getNodees()) {
//			System.out.printf("Degree[%s] = %s\n", n.getLabel(), n.getDegree());
//		}

		System.out.print (graph.fullToXML(0));
	}
	
}
