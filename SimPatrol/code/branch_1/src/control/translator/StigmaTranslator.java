/* StigmaTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;
import model.stigma.Stigma;

/** Implements a translator that obtains Stigma objects
 *  from XML source elements.
 *  
 *  @see Stigma
 *  @develper New Stigma classes must change this class. */
public abstract class StigmaTranslator extends Translator {
	/* Methods. */
	/** Obtains the stigmas from the given XML element.
	 * 
	 *  @param xml_element The XML source containing the stigmas.
	 *  @param graph The graph of the simulation performed by SimPatrol.
	 *  @return The stigmas from the XML source.
	 *  @developer New Stigma classes must change this method. */
	public static Stigma[] getStigmas(Element xml_element, Graph graph) {
		// obtains the nodes with the "stigma" tag
		NodeList stigma_node = xml_element.getElementsByTagName("stigma");
		
		// holds the obtained stigmas
		List<Stigma> stigmas = new LinkedList<Stigma>();
		
		// for each stigma_node
		for(int i = 0; i < stigma_node.getLength(); i++) {
			// obtains the current stigma element
			Element stigma_element = (Element) stigma_node.item(i);
			
			// obtains its data
			String vertex_id = stigma_element.getAttribute("vertex_id");
			String edge_id = stigma_element.getAttribute("edge_id");
			
			// if the vertex_id is valid
			if(vertex_id.length() > 0) {
				// finds the correspondent vertex in the graph
				Vertex vertex = null;
				
				Vertex[] vertexes = graph.getVertexes();
				for(int j = 0; j < vertexes.length; j++)
					if(vertexes[j].getObjectId().equals(vertex_id)) {
						vertex = vertexes[j];
						break;
					}
				
				// if a valid vertex was found
				if(vertex != null)
					// adds a new stigma to the set of stigmas
					stigmas.add(new Stigma(vertex));
			}
			// if not
			else {
				// finds the correspondent edge in the graph
				Edge edge = null;
				
				Edge[] edges = graph.getEdges();
				for(int j = 0; j < edges.length; j++)
					if(edges[j].getObjectId().equals(edge_id)) {
						edge = edges[j];
						break;
					}
				
				// if a valid edge was found
				if(edge != null)
					// adds a new stigma to the set of stigmas
					stigmas.add(new Stigma(edge));
			}
		}
		
		// mounts and returns the answer
		Stigma[] answer = new Stigma[stigmas.size()];
		for(int i = 0; i < answer.length; i++)
			answer[i] = stigmas.get(i);
		return answer;
	}
}