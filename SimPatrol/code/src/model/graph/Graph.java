/* Graph.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;
import model.interfaces.Dynamic;
import model.interfaces.XMLable;

/** Implements graphs that represent the territories to be
 *  patrolled. */
public final class Graph implements XMLable {
	/* Attributes. */
	/** The set of vertexes of the graph. */
	private Set<Vertex> vertexes;
	
	/** The set of edges of the graph. */
	private Set<Edge> edges;

	/** The label of the graph. */
	private String label;
	
	/* Methods. */
	/** Constructor.
	 *  @param label The label of the graph.
	 *  @param vertexes The vertexes of the graph. */
	public Graph(String label, Vertex[] vertexes) {
		this.label = label;
		
		this.vertexes = new HashSet<Vertex>();
		for(int i = 0; i < vertexes.length; i++)
			this.vertexes.add(vertexes[i]);
		
		this.edges = new HashSet<Edge>();
		
		// for each vertex, adds its edges to the set of edges
		for(int i = 0; i < vertexes.length; i++) {
			Edge[] current_edges = vertexes[i].getEdges();
			for(int j = 0; j < current_edges.length; j++)
				this.edges.add(current_edges[j]);
		}
		
		if(this.edges.size() == 0)
			this.edges = null;
	}
	
	/** Obtains the vertexes of the graph.
	 *  @return The vertexes of the graph. */
	public Vertex[] getVertexes() {
		Object[] vertexes_array = this.vertexes.toArray();
		Vertex[] answer = new Vertex[vertexes_array.length];
		
		for(int i = 0; i < answer.length; i++)
			answer[i] = (Vertex) vertexes_array[i];
		
		return answer;
	}
	
	/** Obtains the edges of the graph.
	 *  @return The edges of the graph. */
	public Edge[] getEdges() {
		Object[] edges_array = this.edges.toArray();
		Edge[] answer = new Edge[edges_array.length];
		
		for(int i = 0; i < answer.length; i++)
			answer[i] = (Edge) edges_array[i];
		
		return answer;
	}	
	
	/** Obtains the dynamic objects of the graph.
	 *  @return The dynamic vertexes and edges. */
	public Dynamic[] getDynamicObjects() {
		// the set of dynamic objects
		Set<Dynamic> dynamic_objects = new HashSet<Dynamic>();
		
		// searches for dynamic vertexes
		Object[] vertexes_array = this.vertexes.toArray();
		for(int i = 0; i < vertexes_array.length; i++)
			if(vertexes_array[i] instanceof Dynamic)
				dynamic_objects.add((Dynamic) vertexes_array[i]);
		
		// searches for dynamic edges
		if(this.edges != null) {
			Object[] edges_array = this.edges.toArray();
			for(int i = 0; i < edges_array.length; i++)
				if(edges_array[i] instanceof Dynamic)
					dynamic_objects.add((Dynamic) edges_array[i]);
		}
		
		// returns the answer
		Object[] dynamic_objects_array = dynamic_objects.toArray();
		Dynamic[] answer = new Dynamic[dynamic_objects_array.length];
		for(int i = 0; i <answer.length; i++)
			answer[i] = (Dynamic) dynamic_objects_array[i];		
		return answer;
	}
	
	/** Obtains a subgraph from the graph, starting from the given
	 *  vertex and walking in depth-first mode, until the given
	 *  depth is reached.
	 *  @param vertex The starting point to obtain the subgraph.
	 *  @param depth The depth to reach when walking in depth-first mode.
	 *  @return A subgraph starting from the given vertex and with the given depth. */
	public Graph getSubgraph(Vertex vertex, int depth) {
		// the answer for the method
		Vertex[] starting_vertex = {vertex.getCopy()};
		Graph answer = new Graph(this.label, starting_vertex);
		answer.edges = new HashSet<Edge>();
		
		// expands the answer until the given depth is reached
		this.addDepth(answer, vertex, depth, new HashSet<Vertex>());
		
		// if there are no edges in the answer, nullifies its set of edges
		if(answer.edges.size() == 0) answer.edges = null;
		
		// returns the answer
		return answer;
	}
	
	/** Expands the given subgraph until the given depth is reached, in a depth-first
	 *  recursive manner. The vertex where the expansion is started and a set
	 *  of already expanded vertexes must be informed.
	 *  @param subgraph The subgraph to be expanded.
	 *  @param starting_vertex The vertex where the expansion is started.
	 *  @param depth The depth limit for the expansion.
	 *  @param already_expanded_vertexes The vertexes not to be expanded. */
	private void addDepth(Graph subgraph, Vertex starting_vertex, int depth, Set<Vertex> already_expanded_vertexes) {
		// if the depth is valid
		if(depth > -1) {
			// tries to obtain the copy of the starting vertex from the given subgraph
			Vertex starting_vertex_copy = subgraph.getVertex(starting_vertex.getObjectId());
			
			// if the copy is null, creates it and adds to the subgraph
			if(starting_vertex_copy == null) {
				starting_vertex_copy = starting_vertex.getCopy();
				subgraph.vertexes.add(starting_vertex_copy);
			}
			
			// adds the starting vertex to the vertexes already expanded
			already_expanded_vertexes.add(starting_vertex);
			
			// if there's still depth to expand in the given subgraph
			if(depth > 0) {
				// obtains the neighbourhood of the starting vertex
				Vertex[] neighbourhood = starting_vertex.getNeighbourhood();
				
				// for each vertex of the neighbourhood
				for(int i = 0; i < neighbourhood.length; i++) {
					// if it isn't in the vertexes already expandend
					if(!already_expanded_vertexes.contains(neighbourhood[i])) {
						// tries to obtain a copy of it from the given subgraph
						Vertex neighbour_copy = subgraph.getVertex(neighbourhood[i].getObjectId());
						
						// if the copy is null, creates it and adds to the subgraph
						if(neighbour_copy == null) {
							neighbour_copy = neighbourhood[i].getCopy();
							subgraph.vertexes.add(neighbour_copy);
						}
						
						// obtains the edge between the starting vertex and its
						// current neighbour
						Edge[] edges = starting_vertex.getConnectingEdges(neighbourhood[i]);
						
						// for each edge
						for(int j = 0; j < edges.length; j++) {
							// if there isn't a copy of it in the given
							// subgraph
							if(subgraph.getEdge(edges[j].getObjectId()) == null) {
								// creates the copy and adds to the subgraph
								Edge current_edge_copy = null;
								if(starting_vertex.isEmitterOf(edges[j]))
									current_edge_copy = edges[j].getCopy(starting_vertex_copy, neighbour_copy);
								else
									current_edge_copy = edges[j].getCopy(neighbour_copy, starting_vertex_copy);
								
								subgraph.edges.add(current_edge_copy);
							}
						}
						
						// calls this method recursively, starting from the
						// current neighbour
						this.addDepth(subgraph, neighbourhood[i], depth - 1, already_expanded_vertexes);
					}
				}
			}
		}
	}
	
	/** Returns the vertex of the graph that has the given id.
	 *  @param id The id of the wanted vertex.
	 *  @return The vertex with the given id, or NULL if there's no vertex with such id. */	 
	private Vertex getVertex(String id) {
		Object[] vertexes_array = this.vertexes.toArray();
		for(int i = 0; i < vertexes_array.length; i++) {
			Vertex current_vertex = (Vertex) vertexes_array[i]; 
			if(current_vertex.getObjectId().equals(id))
				return current_vertex;
		}
		
		return null;
	}
	
	/** Returns the edge of the graph that has the given id.
	 *  @param id The id of the wanted edge.
	 *  @return The edge with the given id, or NULL if there's no edge with such id. */
	private Edge getEdge(String id) {
		if(this.edges != null) {
			Object[] edges_array = this.edges.toArray();
			for(int i = 0; i < edges_array.length; i++) {
				Edge current_edge = (Edge) edges_array[i]; 
				if(current_edge.getObjectId().equals(id))
					return current_edge;
			}
		}
		
		return null;
	}
	
	/** Obtains the XML version of this graph at the current moment.
	 *  @param identation The identation to organize the XML. 
	 *  @param current_time The current time, measured in cycles or in seconds.
	 *  @return The XML version of this graph at the current moment. */	
	public String toXML(int identation, int current_time) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// fills the buffer 
		buffer.append("<graph label=\"" + this.label + "\">\n");
		
		// inserts the vertexes
		Object[] vertexes_array = this.vertexes.toArray();
		for(int i = 0; i < vertexes_array.length; i++)
			buffer.append(((Vertex) vertexes_array[i]).toXML(identation + 1, current_time));
		
		// inserts the edges
		if(this.edges != null) {
			Object[] edges_array = this.edges.toArray();
			for (int i = 0; i < edges_array.length; i++)
				buffer.append(((Edge) edges_array[i]).toXML(identation + 1));
		}
		
		// finishes the buffer content
		for(int i = 0; i < identation; i++) buffer.append("\t");
		buffer.append("</graph>\n");		
		
		// returns the buffer content
		return buffer.toString();
	}
	
	/** Give preference to use this.toXML(int identation, int current_time) 
	 * @deprecated */
	public String toXML(int identation) {
		return this.toXML(identation, 0);
	}
	
	public String getObjectId() {
		// a graph doesn't need an id
		return null;
	}
	
	public void setObjectId(String object_id) {
		// a graph doesn't need an id
		// so, do nothing
	}
}