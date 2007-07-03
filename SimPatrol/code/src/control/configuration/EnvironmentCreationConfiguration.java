/* EnvironmentCreationConfiguration.java */

/* The package of this class. */
package control.configuration;

/* Imported classes and/or interfaces. */
import model.Environment;

/** Implements objects that express configurations to create
 *  the environment of the simulation.
 *  @see Environment */
public final class EnvironmentCreationConfiguration extends Configuration {
	/* Attributes. */
	/** The environment being created. */
	private Environment environment;
	
	/* Methods. */
	/** Constructor.
	 *  @param sender_address The IP address of the sender of the configuration.
	 *  @param sender_socket The number of the UDP socket of the sender.
	 *  @param environment The environment being created. */
	public EnvironmentCreationConfiguration(String sender_address, int sender_socket, Environment environment) {
		super(sender_address, sender_socket);
		this.environment = environment;
	}
	
	/** Returns the environment of the configuration.
	 *  @return The environment breing created. */
	public Environment getEnvironment() {
		return this.environment;
	}
	
	protected int getType() {
		return ConfigurationTypes.ENVIRONMENT_CREATION; 
	}
	
	/** Obtains the XML version of this configuration at the current moment.
	 *  @param identation The identation to organize the XML. 
	 *  @param current_time The current time, measured in cycles or in seconds.
	 *  @return The XML version of this configuration at the current moment. */	
	public String toXML(int identation, int current_time) {
		// holds the answer to the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation and fills the "configuration" tag
		for(int i = 0; i < identation; i++) buffer.append("/t");
		buffer.append("<configuration type=\"" + this.getType() +
				      "\" sender_adress=\"" + this.sender_address +
				      "\" sender_socket=\"" + this.sender_socket +
				      "\">\n");
		
		// puts the environment
		buffer.append(this.environment.toXML(identation + 1, current_time));
		
		// closes the tag
		for(int i = 0; i < identation; i++) buffer.append("/t");
		buffer.append("</configuration>\n");		
		
		// return the answer to the method
		return buffer.toString();
	}
	
	/** Give preference to use this.toXML(int identation, int current_time) 
	 * @deprecated */
	public String toXML(int identation) {
		return this.toXML(identation, 0);
	}	
}