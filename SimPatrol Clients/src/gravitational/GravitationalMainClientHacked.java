package gravitational;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import common.IMessageObserver;

import util.file.FileReader;

import log_clients.LogFileClient;
import dummy_client.TcpConnection;


/**
 * Configures the gravitational agents described in SAMPAIO [2010]. To use them, 
 * run the main method with appropriate parameters. 
 * 
 * Some remarks when creating the environment files:
 *  
 * - Perceptions: one agents needs perceptions of SELF and GRAPH, 
 *                while the others only need perception of SELF                
 * - Actions    : all of them need actions VISIT and GOTO 
 *  
 * @author Pablo Sampaio
 */
public class GravitationalMainClientHacked implements IMessageObserver {
	private String serverAddress;
	private int    serverPort;
	private TcpConnection connection;

	private String environmentPath;
	private int totalCycles;

	private String logFilePath;        
	private LogFileClient logClient;
	
	private BlackBoard blackboard;
	private List<GravitationalAgentHacked> agentsList;


	public GravitationalMainClientHacked(String serverIpAddress, int serverPortNumber,
			String environmentFilePath, String logPath, int cyclesOfSimulation,
			MassGrowth growth, double exponent, GravitiesCombinator combinator)
			throws UnknownHostException, IOException {
		
		this.serverAddress = serverIpAddress;
		this.serverPort = serverPortNumber;
		
		this.environmentPath = environmentFilePath;
		this.totalCycles = cyclesOfSimulation;
		
		this.logFilePath = logPath;
		
		this.blackboard  = new BlackBoard(growth, exponent, combinator);
	}

	public void start() {
		
		try {
			
			this.connection = new TcpConnection(serverAddress, serverPort);
			this.connection.start();		

			// 1. sends the environment 

			System.out.print("1. Sending the environment... ");
			
			FileReader file = new FileReader(this.environmentPath);
			String environment = file.readWholeFile();			
			
			String message1 = 
				"<configuration type=\"0\">" 
				+ environment 
				+ "</configuration>";

			this.connection.send(message1);
			System.out.println("ok!");

			// 2. configures the agents

			System.out.print("2. Waiting for information about agent's connection... ");
			AgentInfo[] agentsInfo = receiveAgentsConnectionInfo();

			System.out.print("creating agents... ");
			createAgents(agentsInfo);
			
			System.out.println("ok!");
			
			// 3. configures the log client
			
			System.out.print("3. Configuring log client... ");
			
			String message2 = "<configuration type=\"5\"/>";
			this.connection.send(message2);
			
			System.out.print("receiving connection information... ");
			int logClientPort = receiveLogConnectionInfo();
			
			System.out.print("creating... ");
			
			this.logClient = new LogFileClient(connection.getRemoteSocketAdress(), logClientPort, logFilePath);

			System.out.println("ok!");

			// 4. starts up the simulation

			System.out.print("4. Starting all clients up... ");
			
			for (GravitationalAgentHacked agent : agentsList) {
				agent.startWorking();
			}
			logClient.start();
			
			System.out.print(" sending message to start the simulation... ");
			
			String message3 = "<configuration type=\"3\" parameter=\"" + this.totalCycles + "\"/>";
			this.connection.send(message3);

			System.out.println("ok, simulation started!\n");
			
			// necessary to be notified about incoming messages  
			this.connection.addObserver(this);
		
		} catch (IOException e) {

			e.printStackTrace();
		}
		
	}
	
	private int receiveLogConnectionInfo() {
		String[] serverAnswer = this.connection.retrieveMessages();
		while (serverAnswer.length == 0) {
			serverAnswer = this.connection.retrieveMessages();
		}

		int metricSocketIndex = serverAnswer[0].indexOf("message=\"");
		serverAnswer[0] = serverAnswer[0].substring(metricSocketIndex + 9);

		// returns the port number
		return Integer.parseInt( serverAnswer[0].substring(0, serverAnswer[0].indexOf("\"")) );
	}


	private AgentInfo[] receiveAgentsConnectionInfo() {
		// obtains the answer from the server
		String[] serverAnswer = this.connection.retrieveMessages();
		while (serverAnswer.length == 0) {
			serverAnswer = this.connection.retrieveMessages();
			Thread.yield();
		}
		
		System.out.print("processing answer... ");

		// reads the sockets activated for each agent, as well as agent's IDs
		LinkedList<AgentInfo> agentsInfo = new LinkedList<AgentInfo>();
		String receivedMessage = serverAnswer[0];
		
		int nextAgentIndex = receivedMessage.indexOf("agent_id=\"");
		
		while (nextAgentIndex > -1) {
			receivedMessage = receivedMessage.substring(nextAgentIndex + 10);
			
			String agentId = receivedMessage.substring( 0, receivedMessage.indexOf("\"") );
			int nextSocketIndex = receivedMessage.indexOf("socket=\"");
			
			receivedMessage = receivedMessage.substring(nextSocketIndex + 8);
			int socket = Integer.parseInt(
							receivedMessage.substring(0, receivedMessage.indexOf("\"")) );

			agentsInfo.add(new AgentInfo(agentId, socket));
			
			nextAgentIndex = receivedMessage.indexOf("agent_id=\"");
		}

		AgentInfo[] answer = new AgentInfo[agentsInfo.size()];
		agentsInfo.toArray(answer);
		
		return answer;
	}

	private void createAgents(AgentInfo[] agentsInfo) throws IOException {
		GravitationalAgentHacked agent;
		TcpConnection agentConnection;
		
		String serverAddres = this.connection.getRemoteSocketAdress();
		
		this.agentsList = new LinkedList<GravitationalAgentHacked>();

		for (int i = 0; i < agentsInfo.length; i++) {
			agentConnection = new TcpConnection(serverAddres, agentsInfo[i].port);
			
			agent = new GravitationalAgentHacked(agentsInfo[i].identifier, agentConnection, blackboard);	
			this.agentsList.add(agent);
		}
	}
	
	// called by the the connection (observer) when a message is 
	// received or when the connection is closed
	public void update(){
		if (! this.connection.isWorking()){

			for (GravitationalAgentHacked agent : this.agentsList) {
				agent.stopWorking();
			}

			this.logClient.stopWorking();

			System.out.println("Main client finished.");
		
		} else {
			// should receive anything ?
			System.out.print("Main client received: ");
			System.out.println(this.connection.retrieveMessages()[0]);
			
		}
		
	}
	
	
	// auxiliary inner class
	final class AgentInfo {
		public final String identifier;
		public final int port;

		public AgentInfo(String string, int integer) {
			this.identifier = string;
			this.port = integer;
		}
	}
	
	/**
	 * @param args List of command line arguments: 
	 * 
	 *    index 0: The IP address of the SimPatrol server.
	 *    index 1: The number of the port that the server is listening. 
	 *    index 2: The path of the file that contains the environment (graph + society). 
	 *    index 3: The path of the file that will save the collected events; 
	 *    index 4: The time of simulation.
	 *    index 5: Mass growth function: "A" (for arithmetic) or "G" (for geometric)
	 *    index 6: Distance exponent
	 *    index 7: Gravities combination method: "max" or "sum"
	 */
	public static void execute(String[] args) {
		System.out.println("Gravitational agents!");

		try {
			String serverAddress       = args[0];
			int serverPortNumber       = Integer.parseInt(args[1]);
			String environmentFilePath = args[2];
			String logFilePath         = args[3];
			int cyclesOfSimulation     = Integer.parseInt(args[4]);
			MassGrowth growth          = args[5].toString().equals("G")? MassGrowth.GEOMETRIC : MassGrowth.ARITHMETIC;
			double distanceExponent    = Double.parseDouble(args[6]); 
			GravitiesCombinator combinator = GravitiesCombinator.valueOf(args[7].toUpperCase());
			
			GravitationalMainClientHacked client;			
			
			client = new GravitationalMainClientHacked(serverAddress, serverPortNumber, environmentFilePath, 
							logFilePath, cyclesOfSimulation, growth, distanceExponent, combinator);	

			client.start();

		} catch (Exception e) {			
			System.out.println("\nUsage:\n\n> java gravitational.GravitationalMainClient "
								+ "<Server's IP> <Server's port number> <Environment file path> <Log file> " 
								+ "<Cycles of simulation> <Mass growth> <Exponent> <Gravity combinator>\n\n");
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		
		if (args.length == 0) {
			
			// default parameters, in case no parameters are provided
			args = new String[]{
						"127.0.0.1",
						"5000",
						"res\\environment_files\\grav_test_hacked.xml",
						"tmp\\grav_test_hacked.txt",
						"50",
						"A",
						"1.0",
						"max"
					};
		}
		
		execute(args);
	}
	
}

