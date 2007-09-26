/* AtomicRechargeAction.java */

/* The package of this class. */
package model.action;

/* Imported classes and/or interfaces. */
import model.limitation.SpeedLimitation;
import model.limitation.StaminaLimitation;

/** Implements the atomic action of recharging the agent's stamina.
 * 
 *  Its effect can be controlled by stamina and speed limitations.
 *  @see StaminaLimitation
 *  @see SpeedLimitation */
public class AtomicRechargeAction extends AtomicAction {
	/* Attributes. */
	/** The value to be added to the agent's stamina. */
	private double stamina;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param stamina The value to be added to the agent's stamina. */
	public AtomicRechargeAction(double stamina) {
		this.stamina = stamina;
	}
	
	/** Returns the value to be added to the agent's stamina.
	 * 
	 *  @return The value to be added to the agent's stamina. */
	public double getStamina() {
		return this.stamina;
	}
	
	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// fills the buffer	
		buffer.append("<action type=\"" + ActionTypes.ATOMIC_RECHARGE_ACTION +
				      "\" stamina=\"" + this.stamina +
				      "\"/>\n");
		
		// returns the answer
		return buffer.toString();
	}
}