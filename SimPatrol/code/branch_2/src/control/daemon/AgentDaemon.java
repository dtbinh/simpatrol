/* AgentDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.net.SocketException;
import control.coordinator.Coordinator;
import control.robot.StaminaControllerRobot;
import control.simulator.CycledSimulator;
import control.simulator.RealTimeSimulator;
import util.clock.Clock;
import util.clock.Clockable;
import view.connection.AgentUDPConnection;
import model.agent.Agent;

/** Implements the daemons of SimPatrol that attend
 *  an agent's feelings of perceptions, or intentions of actions. */
public abstract class AgentDaemon extends AuxiliaryDaemon implements Clockable {
	/* Attributes. */
	/** Registers if the daemon can work at the current moment. */
	protected boolean can_work;
	
	/** The agent whose perceptions are produced and
	 *  whose intentions are attended. */
	protected Agent agent;
	
	/** The clock that controls the daemon's work. */
	protected Clock clock;
	
	/** The robot that assures the correct spending of stamina to
	 *  the agent of this daemon.
	 *  
	 *  Used only when the simulator is a real time one.
	 *  
	 *  @see RealTimeSimulator */
	protected StaminaControllerRobot stamina_robot;
	
	/** The coordinator that assures the correct spending of stamina to
	 *  the agent of this daemon, as well as the correct count of time.
	 *  
	 *  Used only when the simulator is a cycled one.
	 *  
	 *  @see Coordinator */
	protected static Coordinator coordinator;
	
	/* Methods. */	
	/** Constructor.
	 * 
	 *  Doesn't initiate its own connection, as its subclasses (PerceptionDaemon
	 *  and ActionDaemon) will share one. So the connection must be set by the
	 *  setConnection() method.
	 *  @see PerceptionDaemon
	 *  
	 *  @see ActionDaemon 
	 *  @param thread_name The name of the thread of the daemon.
	 *  @param agent The agent whose perceptions are produced and intentions are attended. */
	public AgentDaemon(String thread_name, Agent agent) {
		super(thread_name);
		this.agent = agent;		
		this.clock = new Clock(thread_name + "'s clock", this);
		this.can_work = false; // the daemon cannot work in the beginning
		this.stamina_robot = null;
		
		this.connection = null;
	}
	
	/** Sets if this daemon can or cannot work.
	 * 
	 *  @param permission TRUE if the daemon can work, FALSE if not. */
	public void setCan_work(boolean permission) {
		this.can_work = permission;
	}
	
	/** Returns the agent of the daemon
	 * 
	 *  @return The agent served by the daemon. */
	public Agent getAgent() {
		return this.agent;
	}
	
	/** Configures the connection of the daemon.
	 * 
	 *  @param connection The connection of the daemon. */
	public void setConnection(AgentUDPConnection connection) {
		this.connection = connection;
	}
	
	/** Configures the stamina robot of this daemon.
	 * 
	 *  Used only when the simulator is a real time one.
	 *  
	 *  @param stamina_robot The robot that assures the correct spending of stamina to the agent of this daemon.
	 *  @see RealTimeSimulator */
	public void setStamina_robot(StaminaControllerRobot stamina_robot) {
		this.stamina_robot = stamina_robot;
		coordinator = null;		
	}
	
	/** Configures the stamina coordinator of this daemon.
	 * 
	 *  Used only when the simulator is a cycled one.
	 *  
	 *  @see CycledSimulator */
	public static void setStamina_coordinator(Coordinator passed_coordinator) {
		coordinator = passed_coordinator;
	}
	
	public void start(int local_socket_number) throws SocketException {
		super.start(local_socket_number);
		if(this.clock != null)
			this.clock.start();
	}	
	
	/** Indicates that the daemon must stop working. */
	public void stopWorking() {
		super.stopWorking();
		if(this.clock != null)
			this.clock.stopWorking();
	}
}