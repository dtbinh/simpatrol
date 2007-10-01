/* GoToAction.java */

/* The package of this class. */
package model.action;

/* Imported classes and/or interfaces. */
import model.graph.Vertex;
import model.limitation.AccelerationLimitation;
import model.limitation.DepthLimitation;
import model.limitation.SpeedLimitation;
import model.limitation.StaminaLimitation;

/** Implements the action of moving an agent from
 *  his current position to a new given one on the
 *  graph.
 *  
 *  The path used to move the agent is obtained
 *  by the Dijkstra's algorithm.
 *  
 *  Its effect can be controlled by depth, stamina, speed and acceleration limitations.
 *  @see DepthLimitation
 *  @see StaminaLimitation
 *  @see SpeedLimitation
 *  @see AccelerationLimitation */
public final class GoToAction extends CompoundAction {
	/* Attributes */
	/** The initial speed of the movement.
	 * 
	 *  Measured in "depth unities per cycle" (in a cycled simulator)
	 *  or in "depth unities per second" (in a RT simulator). */
	private double initial_speed;
	
	/** The acceleration of the movement.
	 * 
	 *  Meaured in "depth/cycle^2" (in a cycled simulator)
	 *  or in "depth/sec^2" (in a RT simulator). */
	private double acceleration;
	
	/** The vertex to where the agent shall go. */
	private Vertex goal_vertex;
	
	/* Methods */
	/** Constructor.
	 * 
	 *  @param initial_speed The initial speed of the movement.
	 *  @param acceleration The acceleration of the movement.
	 *  @param goal_vertex The vertex to where the agent shall go. */
	public GoToAction(double initial_speed, double acceleration, Vertex goal_vertex) {
		this.initial_speed = initial_speed;
		this.acceleration = acceleration;
		this.goal_vertex = goal_vertex;
	}
	
	/** Returns the initial speed of the movement.
	 * 
	 *  @return the initial speed of the movement. */
	public double getInitial_speed() {
		return this.initial_speed;
	}
	
	/** Returns the acceleration of the movement.
	 * 
	 *  @return The acceleration of the movement. */
	public double getAcceleration() {
		return this.acceleration;
	}
	
	/** Returns the goal vertex of the movement.
	 * 
	 *  @return The goal vertex of the movement. */
	public Vertex getVertex() {
		return this.goal_vertex;
	}
	
	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// fills the buffer	
		buffer.append("<action type=\"" + ActionTypes.GOTO_ACTION +
					  "\" initial_speed=\"" + this.initial_speed +
					  "\" acceleration=\"" + this.acceleration +
					  "\" vertex_id=\"" + this.goal_vertex.getObjectId() +
				      "\"/>\n");
		
		// returns the answer
		return buffer.toString();
	}
}