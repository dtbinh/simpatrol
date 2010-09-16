/* AgentVisitEvent.java */

/* The package of this class. */
package control.event;

/** Implements the events that are related to the visit of an agent to a vertex. */
public final class AgentVisitEvent extends AgentEvent {
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent_id
	 *            The id of the agent to which this event is related to.
	 */
	public AgentVisitEvent(String agent_id) {
		super(agent_id);
	}

	public String fullToXML(int identation, double event_time) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<event type=\"" + EventTypes.AGENT_VISIT + "\" time=\""
				+ event_time + "\" agent_id=\"" + this.AGENT_ID + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}
