package Code;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class Plateau extends JFrame {
	private Serpent serp;
	
	public Plateau(double difficulty, int nbrFood) {
		
	  	serp=new Serpent(difficulty,nbrFood,this);
	    add(serp);
	    pack();
	  
        setResizable(true);
        
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int reponse = JOptionPane.showConfirmDialog(serp,
                        "Voulez-vous quitter l'application",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if(reponse == JOptionPane.YES_OPTION ){
                    System.exit(0);
                }
            }
        });
	}
}