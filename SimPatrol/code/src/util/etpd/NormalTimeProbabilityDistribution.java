/* NormalTimeProbabilityDistribution.java */

/* The package of this class. */
package util.etpd;

/* Imported classes and/or interfaces. */
import cern.jet.random.Normal;

/** Implements the probability distributions of happening
 *  an event based on the time of simulation that are given
 *  by a normal function. */
public class NormalTimeProbabilityDistribution extends EventTimeProbabilityDistribution {
	/* Attributes. */
	/** The mean of the normal function. */
	private double mean;
	
	/** The standard deviation of the normal function. */
	private double standard_deviation;
	
	/* Methods. */
	/** Constructor.
	 *  @param seed The seed for the random number generation.
	 *  @param mean The mean of the normal function.
	 *  @param standard_deviation The standard deviation of the normal function. */
	public NormalTimeProbabilityDistribution(int seed, double mean, double standard_deviation) {
		super(seed);
		this.mean = mean;
		this.standard_deviation = standard_deviation;
		
		// never forget to instantiate this.rn_distributor!!!
		this.rn_distributor = new Normal(this.mean, this.standard_deviation, this.rn_generator); 
	}

	public boolean nextBoolean() {
		// never forget to increase next_bool_counter, before any code!
		this.next_bool_counter++;
		
		if(rn_distributor.nextInt() == next_bool_counter) return true;
		else return false;
	}
	
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		// partially fills the buffer 
		buffer.append("<etpd id=\"" + this.getObjectId() +
				      "\" seed=\"" + this.seed +
				      "\" next_bool_count=\"" + this.next_bool_counter +
				      "\" type=\"" + EventTimeProbabilityDistributionTypes.NORMAL +
				      "\">\n");
		
		// puts the mean value
		for(int i = 0; i < identation + 1; i++)
			buffer.append("\t");
		
		buffer.append("<pd_parameter value=\"" + this.mean + "\"/>\n");
		
		// puts the standard deviation value
		for(int i = 0; i < identation + 1; i++)
			buffer.append("\t");
		
		buffer.append("<pd_parameter value=\"" + this.standard_deviation + "\"/>\n");
		
		// finishes the buffer content
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		buffer.append("</etpd>\n");
		
		// returns the buffer content
		return buffer.toString();
	}
}