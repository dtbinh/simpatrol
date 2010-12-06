/* CycledAgent.java */

/* The package of this class. */
package cycled_OLD;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.xml.sax.SAXException;

import util.Keyboard;
import util.graph.Graph;
import util.graph.GraphTranslator;
import util.net_OLD.TCPClientConnection_OLD;
import util.net_OLD.UDPClientConnection_OLD;
import common_OLD.Agent_OLD;

/**
 * Implements cycled agents, as it is described in the work of Chevaleyre
 * [2005].
 */
public final class CycledAgent_OLD extends Agent_OLD {
	/* Attributes. */
	/** The id of this agent. */
	private String id;

	/** The plan of walking through the graph. */
	private final LinkedList<String> PLAN;

	/**
	 * Registers how many agents this one must let pass before start walking on
	 * the graph.
	 */
	private int let_pass;
	private int nb_agents = 0;

	/**
	 * Registers how much time this agent must wait before start walking on the
	 * graph, after the last agent passed it.
	 */
	private double wait_time;
	
	/*
	 * Set if the agent must walk
	 */
	private boolean walk = false; 

	/**
	 * Registers the time the agent started counting up until the moment to
	 * start walking on the graph.
	 */
	private double start_time;

	/** Registers the agents this one have already perceived. */
	private HashSet<String> PERCEIVED_AGENTS;

	/** Register the current position of the agent. */
	private String current_node_id;

	/** Registers the current step of the path planned by the agent. */
	private int current_plan_step;

	/**
	 * Lets the agent count up how many cycles it has experienced (if it is a
	 * cycled simulation).
	 */
	private int cycles_count;
	
	// registers if this agent received orientations from the coordinator
	boolean received_orientation = false;
	boolean received_position = false;

	// holds the currently perceived graph
	Graph current_graph = null;

	
	/* Methods. */
	/** Constructor. */
	public CycledAgent_OLD(String id) {
		this.id = id;
		this.PLAN = new LinkedList<String>();
		this.let_pass = 0;
		this.wait_time = 0;
		this.start_time = -1;
		this.current_node_id = null;
		this.current_plan_step = -1;
		this.cycles_count = 0;
	}

	/**
	 * Lets the agent perceive the orientation sent by the coordinator.
	 * 
	 * @param perception
	 *            The current perception of the agent.
	 * @return TRUE if the agent perceived the orientation, FALSE if not.
	 */
	private boolean perceiveOrientation(String perception) {
		if (perception.indexOf("<perception type=\"3\"") > -1) {
			// obtains the message broadcasted
			int message_index = perception.indexOf("message=\"");
			perception = perception.substring(message_index + 9);
			String message = perception.substring(0, perception.indexOf("\""));
			
			// if this agent is concerned by the message
			if (message.contains(this.id)){
				
				// get plan
				String str_plan = message.substring(0, message.indexOf("###"));
				StringTokenizer tokenizer = new StringTokenizer(str_plan, ",");

				System.err.print(this.id + ": Plan:");
				while (tokenizer.hasMoreTokens()) {
					String next_step = tokenizer.nextToken();
					this.PLAN.add(next_step);
					System.err.print(" " + next_step);
				}
				System.err.println();
				
				// updates the current plan step, if possible and necessary
				if (this.current_node_id != null
						&& this.current_plan_step == -1) {
					this.current_plan_step = this.PLAN
							.indexOf(this.current_node_id);
					System.err.println("Started on step "
							+ this.current_plan_step + ", node "
							+ this.current_node_id);
				}
				
				// obtains the number of agents that this one must let pass
				message = message.substring(message.indexOf(this.id) + this.id.length() + 1);
				this.let_pass = Integer.parseInt(message.substring(0, message
						.indexOf(";")));
				System.err.println(this.id + ": Let pass " + this.let_pass + " agents.");
				
				// obtains the time this agent must wait, after the last agent
				// passed it, to start walking
				message = message.substring(message.indexOf(";") + 1);
				if(message.indexOf("###") < 0)
					this.wait_time = Double.parseDouble(message);
				else
					this.wait_time = Double.parseDouble(message.substring(0, message.indexOf("###")));
				System.err.println(this.id + ": Must wait " + this.wait_time
						+ " cycles/seconds.");
				
				// returns the success of the perception
				return true;
				
			}
			
			return false;
		}
		// default answer
		return false;
	}

	/**
	 * Lets the agent perceive and count the passing agents.
	 * 
	 * @param perception
	 *            The current perception of the agent.
	 * @return TRUE if the agent perceived the passing agents, FALSE if not.
	 */
	private boolean perceivePassingAgents(String perception) {
		// lets the agent perceive some agent
		if (perception.indexOf("<perception type=\"1\"") > -1) {
			// holds the perceived agents
			String current_perceived_agents = "";
			String current_agent_position = "";

			// at the beginning, if the other agents are on the same node, they are considered as passing by,
			// but we wait more time before starting
			if(this.PERCEIVED_AGENTS == null){
				this.PERCEIVED_AGENTS = new HashSet<String>();
				int agent_index = perception.indexOf("<agent id=\"");
				while (agent_index > -1) {
					perception = perception.substring(agent_index + 11);
					current_perceived_agents = perception.substring(0, perception.indexOf("\""));
					agent_index = perception.indexOf("node_id=\"");
					perception = perception.substring(agent_index + 9);
					current_agent_position = perception.substring(0, perception.indexOf("\""));
					
					nb_agents++;
					
					if (!current_perceived_agents.equals("coordinator")
							&& !this.PERCEIVED_AGENTS.contains(current_perceived_agents)
							&& this.current_node_id.equals(current_agent_position)) {
						// adds it to the already perceived agents
						this.PERCEIVED_AGENTS.add(current_perceived_agents);

						System.err.println(this.id + ": Agent " + current_perceived_agents + " is in the same spot as me.");
					}		
					agent_index = perception.indexOf("<agent id=\"");
				}
				
				if(nb_agents==2 && this.PERCEIVED_AGENTS.size() == 1)
					this.let_pass--;
				
			}
			
			else
			{
			// perceives the agents
				int agent_index = perception.indexOf("<agent id=\"");
				while (agent_index > -1) {
					perception = perception.substring(agent_index + 11);
					current_perceived_agents = perception.substring(0, perception.indexOf("\""));
					agent_index = perception.indexOf("node_id=\"");
					perception = perception.substring(agent_index + 9);
					current_agent_position = perception.substring(0, perception.indexOf("\""));
					
					if (!current_perceived_agents.equals("coordinator")
							&& !this.PERCEIVED_AGENTS.contains(current_perceived_agents)
							&& this.current_node_id.equals(current_agent_position)) {
						// adds it to the already perceived agents
						this.PERCEIVED_AGENTS.add(current_perceived_agents);
	
						// decreases the number of agents to let pass
						this.let_pass--;
	
						System.err.println(this.id + ": Agent " + current_perceived_agents + " passed me.");
					}		
					agent_index = perception.indexOf("<agent id=\"");
					
				}
			}


			// if the number of agents to let pass is smaller than one, sets the
			// "start time" attribute, if necessary
			if (this.let_pass < 1 && this.start_time < 0) {
				if (this.connection instanceof TCPClientConnection_OLD)
					this.start_time = this.cycles_count;
				else
					this.start_time = System.nanoTime();

				System.err.println(this.id + ": Started counting down.");
			}

			// returns the success of the perception
			return true;
		}

		// default answer
		return false;
	}

	/**
	 * Lets the agent perceive its current position, given the string of a
	 * perception.
	 * 
	 * @param perception
	 *            A string representing the current perception of the agent.
	 * @return TRUE if the agent perceived a new position, FALSE if not.
	 */
	private boolean perceivePosition(String perception) {
		if (perception.indexOf("<perception type=\"4\"") > -1) {
			// obtains the id of the agent, if it's still null
			if (this.id == null) {
				int agent_id_index = perception.indexOf("<agent id=\"");
				perception = perception.substring(agent_id_index + 11);
				this.id = perception.substring(0, perception.indexOf("\""));
			}

			// obtains the id of the current node
			int node_id_index = perception.indexOf("node_id=\"");
			perception = perception.substring(node_id_index + 9);
			String node_id = perception
					.substring(0, perception.indexOf("\""));

			// if such id is different from the previous id
			if (!node_id.equals(this.current_node_id)) {
				// updates it
				this.current_node_id = node_id;

				// updates the current plan step if necessary and possible
				if (!this.PLAN.isEmpty() && this.current_plan_step == -1) {
					this.current_plan_step = this.PLAN
							.indexOf(this.current_node_id);
					System.err.println(this.id + ": Started on step "
							+ this.current_plan_step + ", node "
							+ this.current_node_id);
				}

				// signalizes that the current position was updated
				received_position = true;
				return true;
			}
		}

		return false;
	}

	/**
	 * Lets the agent visit the current position and go to the next step (i.e.
	 * node) of its plan.
	 * 
	 * @throws IOException
	 */
	private void visitAndGoToNextStep() throws IOException {
		// lets the agent visit the current position
		this.connection.send("<action type=\"2\"/>");

		// lets the agent go to the next position
		this.current_plan_step = (this.current_plan_step + 1)
				% this.PLAN.size();

		String next_node_id = this.PLAN.get(this.current_plan_step);
		this.connection.send("<action type=\"1\" node_id=\"" + next_node_id
				+ "\"/>");

		System.err.println(this.id +": Gone to step " + this.current_plan_step
				+ ", node " + next_node_id);
	}	
	
	public void run() {
		// starts its connection
		this.connection.start();

		// registers if this agent received orientations from the coordinator
		boolean received_orientation = false;

		// holds the currently perceived graph
		Graph current_graph = null;
		
		LinkedList<String> waiting_messages = new LinkedList<String>();
		
		
		// while the agent is supposed to work
		while (!this.stop_working) {
			// obtains the current perceptions of the agent
			String[] perceptions = this.connection.getBufferAndFlush();

			// tries to perceive the current position of the agent
			for (int i = 0; i < perceptions.length; i++)
				if (this.perceivePosition(perceptions[i]))
					break;
			
			// tries to perceive the coordinator's orientation, if necessary
			if(!received_position)
				for (int i = 0; i < perceptions.length; i++)
					waiting_messages.add(perceptions[i]);
			
			if (!received_orientation && received_position){
				for (int i = 0; i < waiting_messages.size(); i++) {
					received_orientation = this.perceiveOrientation(waiting_messages.get(i));
					if (received_orientation)
						break;
				}
				for (int i = 0; i < perceptions.length; i++) {
					received_orientation = this.perceiveOrientation(perceptions[i]);
					if (received_orientation) break;
				}
				
				if(!received_orientation)
					for (int i = 0; i < perceptions.length; i++)
						waiting_messages.add(perceptions[i]);
				
			}
				
			if(received_orientation){

				// if the agent perceived the coordinator's orientation
				// tries to perceive the other agents
				for (int i = 0; i < waiting_messages.size(); i++)
					this.perceivePassingAgents(waiting_messages.get(i));
				for (int i = 0; i < perceptions.length; i++)
					if (this.perceivePassingAgents(perceptions[i]))
						break;
				
				// tries to perceive the current graph
				Graph[] next_graph = new Graph[0];
				for (int i = waiting_messages.size()-1; i > -1; i--) {
					try {
						next_graph = GraphTranslator.getGraphs(GraphTranslator.parseString(waiting_messages.get(i)));
					} catch (SAXException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
	
					// if obtained a graph, quits the loop
					if (next_graph.length > 0)
						break;
				}
				for (int i = perceptions.length-1; i > -1; i--) {
					try {
						next_graph = GraphTranslator.getGraphs(GraphTranslator
								.parseString(perceptions[i]));
					} catch (SAXException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
	
					// if obtained a graph, quits the loop
					if (next_graph.length > 0)
						break;
				}
				
			
				// verifies if it is time to start walking
				if (this.start_time > -1) {
					if (this.connection instanceof TCPClientConnection_OLD) {
						if (this.cycles_count - this.start_time > this.wait_time) {
							// lets the agent walk and quits this loop
							try {
								this.visitAndGoToNextStep();
							} catch (IOException e) {
								e.printStackTrace();
							}
	
							break;
						}
					} else {
						// obtains the current time of the system
						double current_time = System.nanoTime();
	
						// if enough time has passed
						if (current_time - this.start_time > (this.wait_time * Math.pow(10, 9))) 
						{
							// lets the agent walk and quits this loop
							try {
								this.visitAndGoToNextStep();
							} catch (IOException e) {
								e.printStackTrace();
							}
	
							break;
						}
					}
				}
	
				// if such graph is different from the current one
				if (next_graph.length > 0 && !next_graph[0].equals(current_graph)) {
					// updates the current graph
					current_graph = next_graph[0];
	
					// increments the cycles count
					this.cycles_count++;
					
					while(waiting_messages.size() > 0)
						waiting_messages.remove();
	
					// do nothing
					try {
						this.connection.send("<action type=\"-1\"/>");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// while the agent is supposed to work
		while (!this.stop_working) {
			// obtains the current perceptions
			String[] perceptions = this.connection.getBufferAndFlush();

			// if the agent changed its position
			for (int i = perceptions.length - 1; i > -1; i--)
				if (this.perceivePosition(perceptions[i])) {
					// lets the agent visit the current position and execute the
					// next step of it plan
					try {
						this.visitAndGoToNextStep();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		}

		// stops the connection of the agent
		try {
			this.connection.stopWorking();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Turns this class into an executable one. Useful when running this agent
	 * in an individual machine.
	 * 
	 * @param args
	 *            Arguments: index 0: The IP address of the SimPatrol server.
	 *            index 1: The number of the socket that the server is supposed
	 *            to listen to this client. index 2: "true", if the simulation
	 *            is a real time one, "false" if not.
	 */
	public static void main(String args[]) {
		try {
			String server_address = args[0];
			int server_socket_number = Integer.parseInt(args[1]);
			boolean is_real_time_simulation = Boolean.parseBoolean(args[2]);

			CycledAgent_OLD agent = new CycledAgent_OLD(null);
			if (is_real_time_simulation)
				agent.setConnection(new UDPClientConnection_OLD(server_address,
						server_socket_number));
			else
				agent.setConnection(new TCPClientConnection_OLD(server_address,
						server_socket_number));

			agent.start();

			System.out.println("Press [t] key to terminate this agent.");
			String key = "";
			while (!key.equals("t"))
				key = Keyboard.readLine();

			agent.stopWorking();
		} catch (Exception e) {
			System.out
					.println("Usage \"java heuristic_cognitive_coordinated.HeuristicCognitiveCoordinatedAgent\n"
							+ "<IP address> <Remote socket number> <Is real time simulator? (true | false)> <Agent ID>\"");
		}
	}

	//@Override
	public void update() {
		if(!this.stop_working){
			if( !this.walk ) {
				// obtains the current perceptions of the agent
				String[] perceptions = this.connection.getBufferAndFlush();
	
				// tries to perceive the coordinator's orientation, if necessary
				if (!received_orientation)
					for (int i = 0; i < perceptions.length; i++) {
						received_orientation = this
								.perceiveOrientation(perceptions[i]);
	
						if (received_orientation)
							break;
					}
	
				// tries to perceive the current position of the agent
				for (int i = 0; i < perceptions.length; i++)
					if (this.perceivePosition(perceptions[i]))
						break;
	
				// if the agent perceived the coordinator's orientation
				if (received_orientation)
					// tries to perceive the other agents
					for (int i = 0; i < perceptions.length; i++)
						if (this.perceivePassingAgents(perceptions[i]))
							break;
	
				// verifies if it is time to start walking
				if (this.start_time > -1) {
					if (this.connection instanceof TCPClientConnection_OLD) {
						if (this.cycles_count - this.start_time > this.wait_time) {
							// lets the agent walk and quits this loop
							try {
								this.visitAndGoToNextStep();
							} catch (IOException e) {
								e.printStackTrace();
							}
	
							walk = true;
						}
					} else {
						// obtains the current time of the system
						double current_time = System.nanoTime();
	
						// if enough time has passed
						if (current_time - this.start_time > (this.wait_time * Math
								.pow(10, 9))) {
							// lets the agent walk and quits this loop
							try {
								this.visitAndGoToNextStep();
							} catch (IOException e) {
								e.printStackTrace();
							}
	
							walk = true;
						}
					}
	
				}
	
				// tries to perceive the current graph
				Graph[] next_graph = new Graph[0];
				for (int i = 0; i < perceptions.length; i++) {
					try {
						next_graph = GraphTranslator.getGraphs(GraphTranslator
								.parseString(perceptions[i]));
					} catch (SAXException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
	
					// if obtained a graph, quits the loop
					if (next_graph.length > 0)
						break;
				}
	
				// if such graph is different from the current one
				if (next_graph.length > 0 && !next_graph[0].equals(current_graph)) {
					// updates the current graph
					current_graph = next_graph[0];
	
					// increments the cycles count
					this.cycles_count++;
	
					// do nothing
					try {
						this.connection.send("<action type=\"-1\"/>");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
					//Walk
					// obtains the current perceptions
					String[] perceptions = this.connection.getBufferAndFlush();
		
					// if the agent changed its position
					for (int i = 0; i < perceptions.length; i++)
						if (this.perceivePosition(perceptions[i])) {
							// lets the agent visit the current position and execute the
							// next step of it plan
							try {
								this.visitAndGoToNextStep();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
		}
		
	   } else{
		// stops the connection of the agent
			try {
				this.connection.stopWorking();
			} catch (IOException e) {
				e.printStackTrace();
			}
	   }
	}
}