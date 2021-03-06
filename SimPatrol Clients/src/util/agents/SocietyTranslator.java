package util.agents;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

public class SocietyTranslator {
	/* Methods. */
	/**
	 * Parses a given XML string.
	 * 
	 * @param xml_string
	 *            The string of the XML source containing the objects to be
	 *            translated.
	 * @throws IOException
	 * @throws SAXException
	 */
	public static Element parseString(String xml_string) throws SAXException,
			IOException {
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
	public static SocietyImage getSocieties(Element xml_element) {
		// obtains the nodes with the "graph" tag
//		NodeList soc_node = xml_element.getElementsByTagName("society"); //this should came from SimPatrol, but this is not happening

		AgentImage[] agents = getAgentsSimple(xml_element);
			
		SocietyImage soc = new SocietyImage("", "", false, agents);

		return soc;
	}

	public static AgentImage[] getAgentsSimple(Element xml_element) {
		// obtains the nodes with the "node" tag
		NodeList node_nodes = xml_element.getElementsByTagName("agent");

		// the answer to the method
		AgentImage[] answer = new AgentImage[node_nodes.getLength()];

		// for each occurrence
		for (int i = 0; i < answer.length; i++) {
			// obtains the current node element
			Element node_element = (Element) node_nodes.item(i);

			// obtains its data
			String id = node_element.getAttribute("id");
			String label = node_element.getAttribute("label");
			String node_id = node_element.getAttribute("node_id");
			
			// instantiates the new node
			AgentImage current_agent = new AgentImage(id, label, -1, node_id, "", -1.d, 
					-1.d, -1.d, null, null, -1, -1, "");

			// adds the new node to the answer
			answer[i] = current_agent;
		}

		// returns the answer
		return answer;
	}

	/**
	 * Obtains the nodes from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the nodes.
	 * @return The nodes from the XML source.
	 */
	public static AgentImage[] getAgents(Element xml_element) {
		// obtains the nodes with the "node" tag
		NodeList node_nodes = xml_element.getElementsByTagName("agent");

		// the answer to the method
		AgentImage[] answer = new AgentImage[node_nodes.getLength()];

		// for each occurrence
		for (int i = 0; i < answer.length; i++) {
			// obtains the current node element
			Element node_element = (Element) node_nodes.item(i);

			// obtains its data
			String id = node_element.getAttribute("id");
			String label = node_element.getAttribute("label");
			int state = Integer.valueOf(node_element.getAttribute("state"));
			String node_id = node_element.getAttribute("node_id");
			String edge_id = node_element.getAttribute("edge_id");
			String elapsed_length_str = node_element.getAttribute("elapsed_length");
			double stamina = Double.valueOf(node_element.getAttribute("stamina"));
			double max_stamina = Double.valueOf(node_element.getAttribute("max_stamina"));
			String enter_str = node_element.getAttribute("enter_time");
			String quit_str = node_element.getAttribute("quit_time");
			String socToJoin = node_element.getAttribute("society_to_join");

			double elapsed_length = 0.0d;
			if (!elapsed_length_str.equals("")) {
				elapsed_length = Double.parseDouble(elapsed_length_str);
			}

			int enter_time = -1, quit_time = -1;
			if (!enter_str.equals("")) {
				enter_time = Integer.valueOf(enter_str);
			}
			if (!quit_str.equals("")) {
				quit_time = Integer.valueOf(quit_str);
			}
			
			
			NodeList perceptions = node_element.getElementsByTagName("allowed_perception");
			int[] perception_list = new int[perceptions.getLength()];
			for (int j = 0; j < perception_list.length; j++)
				perception_list[j] = Integer.valueOf(((Element) perceptions.item(j)).getAttribute("type"));
			
			NodeList actions = node_element.getElementsByTagName("allowed_action");
			int[] action_list = new int[actions.getLength()];
			for (int j = 0; j < action_list.length; j++)
				action_list[j] = Integer.valueOf(((Element) actions.item(j)).getAttribute("type"));

			// instantiates the new node
			AgentImage current_agent = new AgentImage(id, label, state, node_id, edge_id, elapsed_length, 
					stamina, max_stamina, perception_list, action_list, enter_time, quit_time, socToJoin);

			// adds the new node to the answer
			answer[i] = current_agent;
		}

		// returns the answer
		return answer;
	}

	
}
