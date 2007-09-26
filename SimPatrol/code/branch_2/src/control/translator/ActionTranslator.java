/* ActionTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import model.action.Action;
import model.action.ActionTypes;
import model.action.AtomicRechargeAction;
import model.action.BroadcastAction;
import model.action.GoToAction;
import model.action.RechargeAction;
import model.action.StigmatizeAction;
import model.action.TeleportAction;
import model.action.VisitAction;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;

/** Implements a translator that obtains Action objects
 *  from XML source elements.
 *  
 *  @see Action
 *  @developer New Action subclasses must change this class. */
public abstract class ActionTranslator extends Translator {
	/* Methods. */
	/** Obtains the action from the given XML string,
	 *  except for an eventual TeleportAction action and
	 *  for an eventual GoToAction action.
	 *  
	 *  To obtain actions of teleporting,
	 *  use getTeleportAction(String xml_string, Graph graph).
	 *  @see TeleportAction
	 *  
	 *  To obtain actions of moving through the graph,
	 *  use getGoToAction(String xml_string, Graph graph).
	 *  @see GoToAction
	 * 
	 *  @param xml_string The XML source containing the actions.
	 *  @return The action from the XML source.
	 *  @throws IOException 
	 *  @throws SAXException 
	 *  @throws ParserConfigurationException 
	 *  @developer New Action subclasses must change this method. */
	public static Action getAction(String xml_string) throws ParserConfigurationException, SAXException, IOException {
		// parses the xml string into a xml element
		Element action_element = parseString(xml_string);
		
		// obtains the type of the current action
		int action_type = Integer.parseInt(action_element.getAttribute("type"));
		
		// switches the action type
		switch(action_type) {
			// if it is a visit action, returns it
			case ActionTypes.VISIT_ACTION: {
				return new VisitAction();
			}
			 
			// if it is a broadcast action
			case ActionTypes.BROADCAST_ACTION: {
				// obtains the message and its depth
				String message = action_element.getAttribute("message");
				
				int message_depth = -1;
				String str_message_depth =  action_element.getAttribute("message_depth");
				if(str_message_depth.length() > 0)
					message_depth = Integer.parseInt(str_message_depth);
				
				// returns the action
				return new BroadcastAction(message, message_depth);
			}
			
			// if it is a stigmatize action, returns it
			case ActionTypes.STIGMATIZE_ACTION: {
				return new StigmatizeAction();
			}
			
			// if it is an atomic recharge action
			case ActionTypes.ATOMIC_RECHARGE_ACTION: {
				// obtains the stamina value to be added to the agent
				double stamina = Double.parseDouble(action_element.getAttribute("stamina"));
				
				// return the action
				return new AtomicRechargeAction(stamina);
			}
			
			// if it is a recharge action
			case ActionTypes.RECHARGE_ACTION: {
				// obtains the stamina value to be added to the agent
				double stamina = Double.parseDouble(action_element.getAttribute("stamina"));
				
				// return the action
				return new RechargeAction(stamina);
			}
			
			// developer: new actions must add code here
		}
		
		// default answer
		return null;
	}
	
	/** Obtains the action of teleport
	 *  from the given XML string.
	 *  
	 *  @param xml_string The XML source containing the action.
	 *  @param graph The graph of the simulation.
	 *  @return The action of teleport from the XML source. 
	 *  @throws IOException 
	 *  @throws SAXException 
	 *  @throws ParserConfigurationException */
	public static TeleportAction getTeleportAction(String xml_string, Graph graph) throws ParserConfigurationException, SAXException, IOException {
		// parses the string in order to obtain the "action" element
		Element action_element = parseString(xml_string);
		
		// verifies the type of the action
		int type = Integer.parseInt(action_element.getAttribute("type"));
		if(type != ActionTypes.TELEPORT_ACTION)
			return null;
		
		// obtains the data from the action
		String vertex_id = action_element.getAttribute("vertex_id");
		String edge_id = action_element.getAttribute("edge_id");
		String str_elapsed_length = action_element.getAttribute("elapsed_length");
		
		// tries to find the correspondent vertex from the graph,
		// given its id
		Vertex vertex = null;
		Vertex[] vertexes = graph.getVertexes();
		for(int i = 0; i < vertexes.length; i++)
			if(vertexes[i].getObjectId().equals(vertex_id)) {
				vertex = vertexes[i];
				break;
			}
		
		// tries to find the correspondent edge from the graph,
		// given its id
		Edge edge = null;
		if(edge_id.length() > 0) {
			Edge[] edges = graph.getEdges();
			for(int i = 0; i < edges.length; i++)
				if(edges[i].getObjectId().equals(edge_id)) {
					edge = edges[i];
					break;
				}
		}
		
		// tries to obtain the elapsed length of the teleport
		double elapsed_length = 0;
		if(str_elapsed_length.length() > 0)
			elapsed_length = Double.parseDouble(str_elapsed_length);
		
		// return the obtained teleport action
		return new TeleportAction(vertex, edge, elapsed_length);
	}
	
	/** Obtains the action of moving on the graph
	 *  from the given XML string.
	 *  
	 *  @param xml_string The XML source containing the action.
	 *  @param graph The graph of the simulation.
	 *  @return The action of moving on the graph from the XML source. 
	 *  @throws IOException 
	 *  @throws SAXException 
	 *  @throws ParserConfigurationException */
	public static GoToAction getGoToAction(String xml_string, Graph graph) throws ParserConfigurationException, SAXException, IOException {
		// parses the string in order to obtain the "action" element
		Element action_element = parseString(xml_string);
		
		// verifies the type of the action
		int type = Integer.parseInt(action_element.getAttribute("type"));
		if(type != ActionTypes.GOTO_ACTION)
			return null;
		
		// obtains the data from the action
		String str_initial_speed = action_element.getAttribute("initial_speed");
		String str_acceleration = action_element.getAttribute("acceleration");
		String goal_vertex_id = action_element.getAttribute("vertex_id");
		
		// tries to obtain the initial speed of the movement
		// (the default value is -1)
		double initial_speed = -1;
		if(str_initial_speed.length() > 0)
			initial_speed = Double.parseDouble(str_initial_speed);
		
		// tries to obtain the acceleration of the movement
		// (the default value is -1)
		double acceleration = -1;
		if(str_acceleration.length() > 0)
			acceleration = Double.parseDouble(str_acceleration);
		
		// tries to find the correspondent vertex from the graph,
		// given its id
		Vertex goal_vertex = null;
		Vertex[] vertexes = graph.getVertexes();
		for(int i = 0; i < vertexes.length; i++)
			if(vertexes[i].getObjectId().equals(goal_vertex_id)) {
				goal_vertex = vertexes[i];
				break;
			}
		
		// return the obtained goto action
		return new GoToAction(initial_speed, acceleration, goal_vertex);
	}
}