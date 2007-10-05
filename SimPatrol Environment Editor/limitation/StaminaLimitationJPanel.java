/* StaminaLimitationJPanel.java */

/* The package of this class. */
package limitation;

/* Imported classes and/or interfaces. */
import javax.swing.JOptionPane;
import model.limitation.Limitation;
import model.limitation.StaminaLimitation;

/** Implements the GUI panel able to configure
 *  StaminaLimitation objects.
 *  
 *  @see StaminaLimitation */
public class StaminaLimitationJPanel extends LimitationJPanel {	
	/* Methods. */
    /** Constructor.
     * 
     *  @param limitation The StaminaLimitation object to be configured. */
    public StaminaLimitationJPanel(StaminaLimitation limitation) {
        this.initComponents();
        this.initComponents(limitation);
    }
    
    /** Initiates the components of the GUI.
     *  Generated by NetBeans IDE 3.6. */
    private void initComponents() {//GEN-BEGIN:initComponents
        cost_panel = new javax.swing.JPanel();
        cost_label = new javax.swing.JLabel();
        cost_field = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());

        cost_panel.setLayout(new java.awt.BorderLayout());

        cost_label.setText("Stamina cost ");
        cost_panel.add(cost_label, java.awt.BorderLayout.WEST);

        cost_field.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        cost_field.setText("0");
        cost_field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
            	cost_fieldKeyReleased(evt);
            }
        });

        cost_panel.add(cost_field, java.awt.BorderLayout.CENTER);

        add(cost_panel, java.awt.BorderLayout.NORTH);

    }//GEN-END:initComponents
    
    /** Complements the initiation of the components of the GUI.
     * 
     *  @param limitation The StaminaLimitation object to be configured. */
    private void initComponents(StaminaLimitation limitation) {
    	this.limitation = limitation;
    	this.cost_field.setText(String.valueOf(this.limitation.getCost()));
    }
    
    /** Executed when the cost_field changes.
     *  Generated by NetBeans IDE 3.6. */
    private void cost_fieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
    	String str_cost = this.cost_field.getText().trim();      
        if(str_cost.length() > 0)
            try {
                double cost = Double.parseDouble(str_cost);
                if(cost < 0)
                    throw new NumberFormatException();                
            }
            catch(NumberFormatException e) {
                if(str_cost.charAt(str_cost.length() - 1) != '.') {
                    JOptionPane.showMessageDialog(this, "The stamina cost must be a real non-negative number.", "Cost error.", JOptionPane.ERROR_MESSAGE);
                    this.cost_field.setText("0");
                }
            }
    }//GEN-LAST:event_jTextField1KeyReleased
    
    @Override
	public Limitation getLimitation() {
        try {
            double cost = Double.parseDouble(this.cost_field.getText().trim());
            
            if(cost < 0)
            	throw new NumberFormatException();
            
            this.limitation = new StaminaLimitation(cost);
        }
        catch(NumberFormatException e) {
        	JOptionPane.showMessageDialog(this, "The stamina cost must be a real non-negative number.", "Cost error.", JOptionPane.ERROR_MESSAGE);
            this.initComponents(this.limitation);
        }
        
        return this.limitation;
	}
    
    /* Attributes. */
    // added manually
    private StaminaLimitation limitation;
    
    // added by Eclipse
    private static final long serialVersionUID = -4306187510280156089L;
    
    // added by NetBeans IDE 3.6
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel cost_label;
    private javax.swing.JPanel cost_panel;
    private javax.swing.JTextField cost_field;
    // End of variables declaration//GEN-END:variables
}