/* LogClient.java */

/* The package of this class. */
package log_clients;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import util.Keyboard;
import util.file.FileWriter;
import util.net.UDPClientConnection;

/**
 * Implements a client object that collects the events generated by the
 * SimPatrol simulator, during a simulation.
 */
public class LogFileClient extends Thread {
	/* Attributes. */
	/** Registers if the log client shall stop working. */
	private boolean stop_working;

	/** The UDP connection of the log client. */
	private UDPClientConnection connection;

	/**
	 * The object that writes on the output file the obtained events.
	 */
	private FileWriter file_writer;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param remote_socket_address
	 *            The IP address of the SimPatrol server.
	 * @param remote_socket_number
	 *            The number of the socket that the server writes to, related to
	 *            this client.
	 * @param file_path
	 *            The path of the file where the events will be saved.
	 * @throws IOException
	 */
	public LogFileClient(String remote_socket_address,
			int remote_socket_number, String file_path) throws IOException {
		this.stop_working = false;
		this.connection = new UDPClientConnection(remote_socket_address,
				remote_socket_number);
		this.file_writer = new FileWriter(file_path);
	}

	/** Indicates that the client must stop working. */
	public void stopWorking() {
		this.stop_working = true;
	}

	public void run() {
		// starts its connection
		this.connection.start();

		while (!this.stop_working) {
			String[] events = this.connection.getBufferAndFlush();

			for (int i = 0; i < events.length; i++)
				this.file_writer.print(events[i]);
		}

		try {
			this.connection.stopWorking();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.file_writer.close();
	}

	/**
	 * Turns this class into an executable one. Util when running this client in
	 * an individual machine.
	 * 
	 * @param args
	 *            Arguments: index 0: The IP address of the SimPatrol server.
	 *            index 1: The number of the socket that the server is supposed
	 *            to writes to this client. index 2: The path of the file that
	 *            will store the collected events.
	 */
	public static void main(String args[]) {
		try {
			String server_address = args[0];
			int server_socket_number = Integer.parseInt(args[1]);
			String file_path = args[2];

			LogFileClient client = new LogFileClient(server_address,
					server_socket_number, file_path);
			client.start();

			System.out.println("Press [t] key to terminate this client.");
			String key = "";
			while (!key.equals("t"))
				key = Keyboard.readLine();

			client.stopWorking();
		} catch (Exception e) {
			System.out
					.println("Usage \"java log_clients.LogFileClient\n"
							+ "<IP address> <Remote socket number> <File path>\"");
		}
	}
}