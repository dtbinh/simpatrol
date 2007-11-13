/* AgentStaminaEvent.java */

/* The package of this class. */
package logger.event;

/** Implements the events that are related to the stamina of an agent. */
public abstract class AgentStaminaEvent extends AgentEvent {
	/* Attributes. */
	/** The quantity of stamina related to the event. */
	protected double quantity;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent_id
	 *            The id of the agent to which this event is related to.
	 * @param quantity
	 *            The quantity of stamina related to the event.
	 */
	public AgentStaminaEvent(String agent_id, double quantity) {
		super(agent_id);
		this.quantity = quantity;
	}
}
