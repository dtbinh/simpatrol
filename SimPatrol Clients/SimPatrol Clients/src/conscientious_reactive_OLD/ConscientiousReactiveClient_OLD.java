/* ConscientiousReactiveClient.java */

/* The package of this class. */
package conscientious_reactive_OLD;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;

import util.net_OLD.TCPClientConnection_OLD;
import util.net_OLD.UDPClientConnection_OLD;

import common_OLD.Agent_OLD;
import common_OLD.Client_OLD;

/**
 * Implements a client that connects to the SimPatrol server and configures it,
 * letting conscientious reactive agent clients connect to it, in the sequence.
 */
public final class ConscientiousReactiveClient_OLD extends Client_OLD {
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param remote_socket_address
	 *            The IP address of the SimPatrol server.
	 * @param remote_socket_number
	 *            The number of the socket that the server is supposed to listen
	 *            to this client.
	 * @param environment_file_path
	 *            The path of the file that contains the environment.
	 * @param metrics_file_paths
	 *            The paths of the files that will save the collected metrics:
	 *            index 0: The file that will save the mean instantaneous
	 *            idlenesses; index 1: The file that will save the max
	 *            instantaneous idlenesses; index 2: The file that will save the
	 *            mean idlenesses; index 3: The file that will save the max
	 *            idlenesses;
	 * @param metrics_collection_rate
	 *            The time interval used to collect the metrics.
	 * @param log_file_path
	 *            The path of the file to log the simulation.
	 * @param time_of_simulation
	 *            The time of simulation.
	 * @param is_real_time_simulator
	 *            TRUE if the simulator is a real time one, FALSE if not.
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public ConscientiousReactiveClient_OLD(String remote_socket_address,
			int remote_socket_number, String environment_file_path,
			String[] metrics_file_paths, int metrics_collecting_rate,
			String log_file_path, int time_of_simulation,
			boolean is_real_time_simulator) throws UnknownHostException,
			IOException {
		super(remote_socket_address, remote_socket_number,
				environment_file_path, metrics_file_paths,
				metrics_collecting_rate, log_file_path, time_of_simulation,
				is_real_time_simulator);
	}

	protected void createAndStartAgents(String[] agent_ids, int[] socket_numbers)
			throws IOException {
		this.agents = new HashSet<Agent_OLD>();

		for (int i = 0; i < agent_ids.length; i++) {
			ConscientiousReactiveAgent_OLD agent = new ConscientiousReactiveAgent_OLD();

			if (this.IS_REAL_TIME_SIMULATOR)
				agent.setConnection(new UDPClientConnection_OLD(this.CONNECTION
						.getRemoteSocketAdress(), socket_numbers[i]));
			else
				agent.setConnection(new TCPClientConnection_OLD(this.CONNECTION
						.getRemoteSocketAdress(), socket_numbers[i]));

			agent.start();
		}
	}

	/**
	 * Turns this class into an executable one.
	 * 
	 * @param args Arguments: 
	 *             index 0: The IP address of the SimPatrol server.
	 *             index 1: The number of the socket that the server is supposed to listen to this client. 
	 *             index 2: The path of the file that contains the environment. 
	 *             index 3. The path of the file that will save the mean instantaneous idlenesses; 
	 *             index 4. The path of the file that will save the max instantaneous idlenesses.
	 *             index 5. The path of the file that will save the meanidlenesses.
	 *             index 6. The path of the file that will save the max idlenesses.
	 *             index 7: The time interval used to collect the metrics.
	 *             index 8: The path of the file that will save the collected events.
	 *             index 9: The time of simulation. 
	 *             index 10: false if the simulator is a cycled one, true if not.
	 */
	public static void main(String[] args) {
		System.out.println("Conscientious reactive agents!");

		try {
			String remote_socket_address = args[0];
			int remote_socket_number = Integer.parseInt(args[1]);
			String environment_file_path = args[2];
			String[] metric_file_paths = { args[3], args[4], args[5], args[6] };
			int metrics_collecting_rate = Integer.parseInt(args[7]);
			String log_file_path = args[8];
			int time_of_simulation = Integer.parseInt(args[9]);
			boolean is_real_time_simulator = Boolean.parseBoolean(args[10]);

			ConscientiousReactiveClient_OLD client = new ConscientiousReactiveClient_OLD(
					remote_socket_address, remote_socket_number,
					environment_file_path, metric_file_paths,
					metrics_collecting_rate, log_file_path, time_of_simulation,
					is_real_time_simulator);
			client.start();
		} catch (Exception e) {
			System.out
					.println("Usage \"java cognitive_coordinated.CognitiveCoordinatedClient\n"
							+ "<IP address> <Remote socket number> <Environment file path>\n"
							+ "<Metric file name 1 | \"\"> <Metric file name 2 | \"\"> <Metric file name 3 | \"\"> <Metric file name 4 | \"\">\n"
							+ "<Metric collecting rate> <Log file name> <Time of simulation> <Is real time simulator? (true | false)>\"");
		}
	}
}