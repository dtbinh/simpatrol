/* StaminaLimitation.java */

/* The package of this class. */
package model.limitation;

/** Implements the limitations that control the stamina of an agent. */
public final class StaminaLimitation extends Limitation {
	/* Attributes. */
	/** The cost of stamina associated with the permission. */
	private double cost;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param cost The cost of stamina associated with the parmission. */
	public StaminaLimitation(double cost) {
		super();
		this.cost = cost;
	}
	
	/** Returns the cost of the limitation.
	 * 
	 *  @return The cost of the limitation. */
	public double getCost() {
		return this.cost;
	}
	
	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// fills the buffer
		buffer.append("<limitation type=\"" + LimitationTypes.STAMINA_LIMITATION + "\">\n");
		
		// puts the parameters of the limitation
		for(int i = 0; i < identation + 1; i++) buffer.append("\t");
		buffer.append("<lmt_parameter value=\"" + this.cost + "\"/>\n");
		
		// closes the main tag
		for(int i = 0; i < identation; i++) buffer.append("\t");
		buffer.append("</limitation>\n");
		
		// returns the answer
		return buffer.toString();
	}
}