package Code;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

@SuppressWarnings("serial")
public class Jeu extends JFrame{
	private JTextField jtf2;
	private JButton demarrer;
	@SuppressWarnings("rawtypes")
	private JComboBox jb;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Jeu () {
		setBounds(30,30,300,150);
		setLayout(new FlowLayout());
	    this.setLocationRelativeTo(null);
	    Box vb = Box.createVerticalBox();
	    JPanel container1 = new JPanel();
	    container1.setLayout(new FlowLayout());	    
		JLabel jl = new JLabel("Difficulté ");
		container1.add(jl);
		
		String [] difficulte = {"Facile","Moyen","Difficile"};
		jb = new JComboBox(difficulte);
		container1.add(jb);
		vb.add(container1);
		JPanel container2 = new JPanel();
		JLabel jl2 = new JLabel("Nombre de repas");
		container2.add(jl2);
		jtf2= new JTextField(5);
		container2.add(jtf2);
		vb.add(container2);
		
		demarrer = new JButton("Start");
		vb.add(demarrer);
		this.add(vb);
		this.setVisible(true);
		
		
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int reponse = JOptionPane.showConfirmDialog(null,
                        "Voulez-vous quitter l'application",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if(reponse == JOptionPane.YES_OPTION ){
                	System.exit(0);
                }
            
            }
        });
        
        
		ActionListener a = new MonActionListener();
		demarrer.addActionListener(a);
	}
	public int get_nbrfood() throws Hors_Limite {
		int res = Integer.parseInt(jtf2.getText());
		if(res <5 || res >20) throw new Hors_Limite();
		return res;
	}
	private class MonActionListener implements ActionListener { 

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
			double difficult;
			int nbrfood;
			String difficulty = (String) jb.getSelectedItem();
			if(difficulty.equals("Facile")) difficult =0.5;
			else if(difficulty.equals("Moyen")) difficult =1;
			else difficult =2;
			nbrfood = get_nbrfood();

			EventQueue.invokeLater(() -> {
	            JFrame ex = new Plateau(difficult, nbrfood);
	            ex.setVisible(true);
	        });
			dispose();
			}catch(NumberFormatException e1){
					JOptionPane.showMessageDialog(null, "Veuillez saisir un nombre valide",
							"Error", JOptionPane.ERROR_MESSAGE);; 
				}
			catch(Hors_Limite e) {
				JOptionPane.showMessageDialog(null, e,
						"Error", JOptionPane.ERROR_MESSAGE);; 
			}
		}}
	
}
