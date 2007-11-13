/* AgentSpendingStaminaEvent.java */

/* The package of this class. */
package logger.event;

/**
 * Implements the events that are related to the spending of stamina of an
 * agent.
 */
public final class AgentSpendingStaminaEvent extends AgentStaminaEvent {
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent_id
	 *            The id of the agent to which this event is related to.
	 * @param quantity
	 *            The quantity of stamina related to the event.
	 */
	public AgentSpendingStaminaEvent(String agent_id, double quantity) {
		super(agent_id, quantity);
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<event type=\""
				+ EventTypes.AGENT_SPENDING_STAMINA_EVENT + "\" time=\""
				+ simulator.getElapsedTime() + "\" agent_id=\"" + this.agent_id
				+ "\" quantity=\"" + this.quantity + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}
