package project;

import jade.core.AID;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class AgentGui extends JFrame {
    private CustomerAgent myAgent;

    private JTextField factsField;

    AgentGui(CustomerAgent a){
        super(a.getLocalName());

        myAgent = a;

        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
        p.add(new JLabel("(Producto: "));
        factsField = new JTextField(25);
        p.add(factsField);
        p.add(new JLabel(")"));

        getContentPane().add(p, BorderLayout.CENTER);

        JButton addButton = new JButton("Add");
		addButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
                try {
                    String facts = factsField.getText().trim();
                    myAgent.hacerPedido(facts);
                    factsField.setText("");
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(AgentGui.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
                }
			}
		} );
		p = new JPanel();
		p.add(addButton);
        
		getContentPane().add(p, BorderLayout.SOUTH);

        //Acabar con el agente cuando se cierra el GUI
        addWindowListener(new	WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//myAgent.detener = true;
			}
		} );
		
		setResizable(false);
    }

    public void showGui() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}
    
}
