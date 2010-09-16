/* UniformEventTimeProbabilityDistributionJPanel.java */

/* The package of this class. */
package etpd;

/* Imported classes and/or interfaces. */
import javax.swing.JOptionPane;
import model.etpd.UniformEventTimeProbabilityDistribution;

/**
 * Implements the GUI panel able to configure
 * UniformEventTimeProbabilityDistribution objects.
 * 
 * @see UniformEventTimeProbabilityDistribution
 */
public class UniformEventTimeProbabilityDistributionJPanel extends
		EventTimeProbabilityDistributionJPanel {
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param etpd
	 *            The UniformEventTimeProbabilityDistribution object to be
	 *            configured.
	 */
	public UniformEventTimeProbabilityDistributionJPanel(
			UniformEventTimeProbabilityDistribution etpd) {
		this.initComponents();
		this.initComponents(etpd);
	}

	/**
	 * Initiates the components of the GUI. Generated by NetBeans IDE 3.6.
	 */
	private void initComponents() {// GEN-BEGIN:initComponents
		internal_panel = new javax.swing.JPanel();
		prob_panel = new javax.swing.JPanel();
		prob_label = new javax.swing.JLabel();
		prob_field = new javax.swing.JTextField();
		seed_panel = new javax.swing.JPanel();
		seed_label = new javax.swing.JLabel();
		seed_field = new javax.swing.JTextField();
		seed_button = new javax.swing.JButton();

		setLayout(new java.awt.BorderLayout());

		internal_panel.setLayout(new java.awt.GridLayout(2, 0));

		prob_panel.setLayout(new java.awt.BorderLayout());

		prob_label.setText("Probability value ");
		prob_label.setRequestFocusEnabled(false);
		prob_panel.add(prob_label, java.awt.BorderLayout.WEST);

		prob_field.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
		prob_field.setText("0");
		prob_field.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				prob_fieldKeyReleased(evt);
			}
		});

		prob_panel.add(prob_field, java.awt.BorderLayout.CENTER);

		internal_panel.add(prob_panel);

		seed_panel.setLayout(new java.awt.BorderLayout());

		seed_label.setText("Seed ");
		seed_panel.add(seed_label, java.awt.BorderLayout.WEST);

		seed_field.setEditable(false);
		seed_field.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
		this.seed_field.setText(String
				.valueOf((int) System.currentTimeMillis()));
		seed_panel.add(seed_field, java.awt.BorderLayout.CENTER);

		seed_button.setText("Generate");
		seed_button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				seed_buttonActionPerformed(evt);
			}
		});

		seed_panel.add(seed_button, java.awt.BorderLayout.EAST);

		internal_panel.add(seed_panel);

		add(internal_panel, java.awt.BorderLayout.NORTH);

	}// GEN-END:initComponents

	/**
	 * Complements the initiation of the components of the GUI.
	 * 
	 * @param etpd
	 *            The UniformEventTimeProbabilityDistribution object to be
	 *            configured.
	 */
	private void initComponents(UniformEventTimeProbabilityDistribution etpd) {
		this.etpd = etpd;

		this.prob_field.setText(String.valueOf(this.etpd.getProbability()));
		this.seed_field.setText(String.valueOf(this.etpd.getSeed()));
	}

	/**
	 * Executed when the seed_button is pressed. Generated by NetBeans IDE 3.6.
	 */
	private void seed_buttonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_seed_buttonActionPerformed
		this.seed_field.setText(String
				.valueOf((int) System.currentTimeMillis()));
	}// GEN-LAST:event_seed_buttonActionPerformed

	/**
	 * Executed when the prob_field changes. Generated by NetBeans IDE 3.6.
	 */
	private void prob_fieldKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_prob_fieldKeyReleased
		String str_probability = this.prob_field.getText().trim();
		if (str_probability.length() > 0)
			try {
				double probability = Double.parseDouble(str_probability);
				if (probability < 0 || probability > 1)
					throw new NumberFormatException();
			} catch (NumberFormatException e) {
				if (str_probability.charAt(str_probability.length() - 1) != '.') {
					JOptionPane
							.showMessageDialog(
									this,
									"The probability value must belong to the real interval [0, 1].",
									"Probability value error.",
									JOptionPane.ERROR_MESSAGE);
					this.prob_field.setText("0");
				}
			}
	}// GEN-LAST:event_prob_fieldKeyReleased

	public model.etpd.EventTimeProbabilityDistribution getETPD() {
		try {
			double probability = Double.parseDouble(this.prob_field.getText()
					.trim());
			int seed = Integer.parseInt(this.seed_field.getText().trim());

			if (probability < 0 || probability > 1)
				throw new NumberFormatException();

			this.etpd = new UniformEventTimeProbabilityDistribution(seed,
					probability);
		} catch (NumberFormatException e) {
			JOptionPane
					.showMessageDialog(
							this,
							"The probability value must belong to the real interval [0, 1].",
							"Probability value error.",
							JOptionPane.ERROR_MESSAGE);

			if (this.etpd == null)
				this.prob_field.setText("0");
			else
				this.initComponents(this.etpd);
		}

		return this.etpd;
	}

	/* Attributes. */
	// added manually
	private UniformEventTimeProbabilityDistribution etpd;

	// added by eclipse
	private static final long serialVersionUID = -2238763265459549368L;

	// added by NetBeans IDE 3.6
	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel internal_panel;
	private javax.swing.JTextField prob_field;
	private javax.swing.JLabel prob_label;
	private javax.swing.JPanel prob_panel;
	private javax.swing.JButton seed_button;
	private javax.swing.JTextField seed_field;
	private javax.swing.JLabel seed_label;
	private javax.swing.JPanel seed_panel;
	// End of variables declaration//GEN-END:variables
}