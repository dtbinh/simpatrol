/* Society.java */

/* The package of this class. */
package model.agent;

/* Imported classes and/or interfaces. */
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import view.XMLable;

/** Implements the societies of agents of SimPatrol. */
public abstract class Society implements XMLable {
	/* Attributes. */
	/**
	 * The object id of the society. Not part of the patrol problem modelling.
	 */
	private String id;

	/** The label of the society. */
	private String label;

	/** The set of agents of the society. */
	protected Set<Agent> agents;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param label
	 *            The label of the society.
	 * @param agents
	 *            The agents that compound the society.
	 */
	public Society(String label, Agent[] agents) {
		this.label = label;

		this.agents = Collections.synchronizedSet(new HashSet<Agent>());
		for (int i = 0; i < agents.length; i++)
			this.agents.add(agents[i]);
	}

	/**
	 * Returns the label of the society.
	 * 
	 * @return The label of the society.
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Returns the agents of the society.
	 * 
	 * @return The set of agents of the society.
	 */
	public Agent[] getAgents() {
		Object[] agents_array = this.agents.toArray();

		Agent[] answer = new Agent[agents_array.length];
		for (int i = 0; i < answer.length; i++)
			answer[i] = (Agent) agents_array[i];

		return answer;
	}

	/**
	 * Adds a given agent to the society.
	 * 
	 * @param agent
	 *            The agent to be added.
	 * @return TRUE if the agent was added successfully, FALSE if not.
	 */
	public abstract boolean addAgent(Agent agent);

	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<society id=\"" + this.id + "\" label=\"" + this.label
				+ "\" is_closed=\"true");

		// inserts the agents, if there are some, and closes the main tag
		Object[] agents_array = this.agents.toArray();
		if (agents_array.length > 0) {
			buffer.append("\">\n");

			for (int i = 0; i < agents_array.length; i++)
				buffer.append(((Agent) agents_array[i])
						.fullToXML(identation + 1));

			// finishes the buffer content
			for (int i = 0; i < identation; i++)
				buffer.append("\t");
			buffer.append("</society>\n");
		} else
			buffer.append("\"/>\n");

		// returns the buffer content
		return buffer.toString();
	}

	public String reducedToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<society id=\"" + this.id + "\" label=\"" + this.label);

		// inserts the agents, if there are some, and closes the main tag
		Object[] agents_array = this.agents.toArray();
		if (agents_array.length > 0) {
			buffer.append("\">\n");

			for (int i = 0; i < agents_array.length; i++)
				buffer.append(((Agent) agents_array[i])
						.reducedToXML(identation + 1));

			// finishes the buffer content
			for (int i = 0; i < identation; i++)
				buffer.append("\t");
			buffer.append("</society>\n");
		} else
			buffer.append("\"/>\n");

		// returns the buffer content
		return buffer.toString();
	}

	public boolean equals(Object object) {
		if (object instanceof XMLable)
			return this.id.equals(((XMLable) object).getObjectId());
		else
			return super.equals(object);
	}

	public String getObjectId() {
		return this.id;
	}

	public void setObjectId(String object_id) {
		this.id = object_id;
	}
}