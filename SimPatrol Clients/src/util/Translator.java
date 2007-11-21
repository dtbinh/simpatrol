/* Translator.java */

/* The package of this class. */
package util;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import util.graph.Edge;
import util.graph.Graph;
import util.graph.Vertex;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;

/** Implements translators that obtain Java objects from XML files. */
public abstract class Translator {
	/* Methods. */
	/**
	 * Parses a given XML string.
	 * 
	 * @param xml_string
	 *            The string of the XML source containing the objects to be
	 *            translated.
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static Element parseString(String xml_string)
			throws ParserConfigurationException, SAXException, IOException {
		InputSource is = new InputSource(new StringReader(xml_string));

		DOMParser parser = new DOMParser();
		parser.parse(is);

		Document doc = parser.getDocument();
		return doc.getDocumentElement();
	}

	/**
	 * Obtains the graphs from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the graphs.
	 * @return The graphs from the XML source.
	 */
	public static Graph[] getGraphs(Element xml_element) {
		// obtains the nodes with the "graph" tag
		NodeList graph_node = xml_element.getElementsByTagName("graph");

		// the answer to the method
		Graph[] answer = new Graph[graph_node.getLength()];

		// for each graph_node
		for (int i = 0; i < answer.length; i++) {
			// obtains the current graph element
			Element graph_element = (Element) graph_node.item(i);

			// obtains the data
			String label = graph_element.getAttribute("label");

			// obtains the vertexes
			Vertex[] vertexes = getVertexes(graph_element);

			// obtains the edges
			getEdges(graph_element, vertexes);

			// obtains the new graph
			Graph graph = new Graph(label, vertexes);

			// adds the new graph to the answer
			answer[i] = graph;
		}

		// returns the answer
		return answer;
	}

	/**
	 * Obtains the vertexes from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the vertexes.
	 * @return The vertexes from the XML source.
	 */
	private static Vertex[] getVertexes(Element xml_element) {
		// obtains the nodes with the "vertex" tag
		NodeList vertex_nodes = xml_element.getElementsByTagName("vertex");

		// the answer to the method
		Vertex[] answer = new Vertex[vertex_nodes.getLength()];

		// for each ocurrence
		for (int i = 0; i < answer.length; i++) {
			// obtains the current vertex element
			Element vertex_element = (Element) vertex_nodes.item(i);

			// obtains its data
			String id = vertex_element.getAttribute("id");
			String label = vertex_element.getAttribute("label");
			String str_priority = vertex_element.getAttribute("priority");
			String str_idleness = vertex_element.getAttribute("idleness");
			String str_fuel = vertex_element.getAttribute("fuel");

			// instatiates the new vertex
			Vertex current_vertex = new Vertex(label);

			// configures the new vertex...
			// id configuration
			current_vertex.setObjectId(id);

			// priority configuration
			int priority = 0;
			if (str_priority.length() > 0)
				priority = Integer.parseInt(str_priority);
			current_vertex.setPriority(priority);

			// idleness configuration
			int idleness = 0;
			if (str_idleness.length() > 0)
				idleness = Integer.parseInt(str_idleness);
			current_vertex.setIdleness(idleness);

			// fuel configuration
			boolean fuel = false;
			if (str_fuel.length() > 0)
				fuel = Boolean.parseBoolean(str_fuel);
			current_vertex.setFuel(fuel);

			// adds the new vertex to the answer
			answer[i] = current_vertex;
		}

		// returns the answer
		return answer;
	}

	/**
	 * Obtains the edges from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the edges.
	 * @param vertexes
	 *            The set of vertexes read from the XML source.
	 * @return The edges from the XML source.
	 */
	private static Edge[] getEdges(Element xml_element, Vertex[] vertexes) {
		// obtains the nodes with the "edge" tag
		NodeList edge_nodes = xml_element.getElementsByTagName("edge");

		// the answer to the method
		Edge[] answer = new Edge[edge_nodes.getLength()];

		// for each ocurrence
		for (int i = 0; i < answer.length; i++) {
			// obtains the current edge element
			Element edge_element = (Element) edge_nodes.item(i);

			// obtains its data
			String id = edge_element.getAttribute("id");
			String emitter_id = edge_element.getAttribute("emitter_id");
			String collector_id = edge_element.getAttribute("collector_id");
			String str_oriented = edge_element.getAttribute("oriented");
			double length = Double.parseDouble(edge_element
					.getAttribute("length"));

			// finds the correspondent emitter and collector vertexes
			Vertex emitter = null;
			Vertex collector = null;

			for (int j = 0; j < vertexes.length; j++) {
				Vertex current_vertex = vertexes[j];

				if (current_vertex.getObjectId().equals(emitter_id)) {
					emitter = current_vertex;
					if (collector != null)
						break;
				}

				if (current_vertex.getObjectId().equals(collector_id)) {
					collector = current_vertex;
					if (emitter != null)
						break;
				}
			}

			// decides if the edge is oriented
			boolean oriented = false;
			if (str_oriented.length() > 0)
				oriented = Boolean.parseBoolean(str_oriented);

			// instantiates the new edge (normal or dynamic)
			Edge current_edge = new Edge(emitter, collector, oriented, length);

			// configures the new edge...
			// id configuration
			current_edge.setObjectId(id);

			// adds the new edge to the answer
			answer[i] = current_edge;
		}

		// returns the answer
		return answer;
	}
}