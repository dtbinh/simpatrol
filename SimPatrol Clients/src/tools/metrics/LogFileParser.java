package tools.metrics;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import util.DoubleList;

import com.twicom.qdparser.DocHandler;
import com.twicom.qdparser.QDParser;
import com.twicom.qdparser.XMLParseException;


/**
 * Parser for the file generated by the logger client of the SimPatrol. 
 * 
 * It parses the XML file and returns the visits information as an object
 * of the class VisitsList, which can be used to calculate any metrics.
 * 
 * @author Pablo A. Sampaio
 */
public class LogFileParser implements DocHandler {
	private HashMap<String,Integer> agentsNumbers;
	private HashMap<String,Integer> nodesNumbers;
	private HashMap<String,Integer> agentsNodes; 

	private int numAgents;
	private int numNodes;
	private VisitsList visits;
	private DoubleList nodePriorities;
	
	
	public LogFileParser() {
		reset();
	}
	
	private void reset() {
		agentsNumbers = new HashMap<String, Integer>();
		nodesNumbers = new HashMap<String,Integer>();
		agentsNodes  = new HashMap<String,Integer>();
		
		this.nodePriorities = new DoubleList();
		
		numAgents = 0;
		numNodes = 0;
		visits = new VisitsList();
	}
	

	@Override
	public void startDocument() throws XMLParseException {
	}

	@Override
	public void endDocument() throws XMLParseException {
	}

	@Override
	public void startElement(String nameSpace, String tag,
			Map<String, String> attributes, int line, int col)
			throws XMLParseException {
		
		if (tag.equals("node")) {
			String node = attributes.get("id");
			
			// associates the node to an unique number
			nodesNumbers.put(node, numNodes);
			numNodes ++;
			
			// Stores each node's priority value
			this.nodePriorities.add(Double.parseDouble(attributes.get("priority")));
			
			System.out.printf("Node %s is number %d.\n", node, numNodes-1);

		} else if (tag.equals("agent")) {
			String agentId = attributes.get("id");
			
			// associates the agent to an unique number (among agents)
			agentsNumbers.put(agentId, numAgents);
			numAgents ++;
			
			System.out.printf("Agent %s is number %d.\n", agentId, numAgents-1);
			
			// TODO: REMOVE THIS by referencing the node directly in event 6
			
			String nodeId  = attributes.get("node_id");
			Integer nodeNumber = nodesNumbers.get(nodeId);
			
			agentsNodes.put(agentId, nodeNumber);
			System.out.printf("Agent %d (%s) starts in node %d (%s).\n", numAgents-1, agentId, nodeNumber, nodeId);

		} else if (tag.equals("event") ) {
			
			if (attributes.get("type").equals("6")) {
				String agentId = attributes.get("agent_id");
				
				int agent = agentsNumbers.get(agentId); 
				int node  = agentsNodes.get(agentId);
				int time  = (int)Double.parseDouble( attributes.get("time") );
				
				visits.addVisit(new Visit(time, agent, node));
				
				System.out.printf(">> Agent %d visited node %d in time %d!\n", agent, node, time);
			
			} else if (attributes.get("type").equals("5")) {
				// REMOVE THIS by including the node directly in event 6

				String agent = attributes.get("agent_id");
				String node  = attributes.get("node_id");
				Integer nodeNumber = nodesNumbers.get(node);
					
				agentsNodes.put(agent, nodeNumber);
				System.out.printf("Agent %s is in node %d (%s).\n", agent, nodeNumber, node);

			} 
			
		}
		
	}

	@Override
	public void endElement(String nameSpace, String tag)
			throws XMLParseException {
	}

	@Override
	public void text(String str, int line, int col) throws XMLParseException {
	}

	@Override
	public void text(String str, boolean cdata, int line, int col)
			throws XMLParseException {
	}
	
	public void parseFile(String fileName) throws FileNotFoundException, XMLParseException, IOException {
		reset();
		
		QDParser.parse(this, new FileReader(fileName));
	}
	
	public VisitsList getVisitsList() {
		return visits;
	}
	
	public int getNumAgents() {
		return numAgents;
	}
	
	public int getNumNodes() {
		return numNodes;
	}

	public DoubleList getNodePriorities() {
		return nodePriorities;
	}
}
