package Code;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Serpent extends JPanel implements ActionListener{
	private int taille; // taille du serpent à tout moment
	private int tailleMin; // taille min pour que le serpent survit
	private int nbrDeplacement = 0; //Déplacement effectué par le serpent après son dernier repas
	private int seconde=0; //temps actuel
	private boolean inGame=true; // savoir si le jeu est toujours en cours ou pas
	private int nbrFoodManger=0; // Total des repas que le serpent à manger
	private int nbrFoodGoal; // Nombre de food défini par un utilisateur pour terminer le jeu
	private boolean win=false; // Savoir si on a atteint les objectifs ou pas

	private int limit_repas_toxique;
	private int actuelle_repas_toxique;

	private JButton score;

	private ArrayList<Food> f = new ArrayList<>(); // liste de tous les aliments présents sur la map
	
	/* Les différentes directions que peut faire le serpent 
	 * Au départ, on a décider qu'il se dirige vers la droite
	 */
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    
    private Image corps;
    private Image food;
    private Image tete;
    private Image poison;
	
    
    private final int tab[][] = new int[Globals.B_WIDTH][Globals.B_HEIGHT]; //tableau à deux dimensions qui donne une trace des éléments présent sur la map
    
    /* Coordonné du serpent à tout moment, en sachant que l'indice 0 correspond à sa tête */
    private int x[] = new int [Globals.TAILLE_MAX];
    private int y[] = new int [Globals.TAILLE_MAX];

    private Timer timer;
    
	/* Constructeur d'un serpent */
	public Serpent (double difficulty, int nbrFoodGoal, JFrame fen) {
		this.nbrFoodGoal = nbrFoodGoal;
        setFocusable(true);
        setPreferredSize(new Dimension(Globals.B_WIDTH , Globals.B_HEIGHT));
        loadImages();
        
		taille = 4;
		tailleMin=2;
	//	Random rand = new Random();
		int repas = /*rand.nextInt(15) +*/ 5 ; //Mettre entre 5 et 20 repas
		int repas_toxique = (int) Math.round(repas * difficulty);
		actuelle_repas_toxique = repas_toxique;
		limit_repas_toxique = (int) Math.round(nbrFoodGoal * difficulty);
		
		//placer la tete du serpent sur le plateau
		x[0] = 170;
		y[0] = 170 ;

		/* placer le reste de son corps */
		for(int i =1; i<taille; i++) {
			x[i] = 170 - i*Globals.PIXEL_SIZE;
			y[i] = 170;
			tab[170 - i*Globals.PIXEL_SIZE][170] = Globals.SNAKE_BODY; // actualiser le contenu du plateau
		}

		/* Répartir sur le plateau différents repas */
		for(int i = 0; i<repas_toxique; i++) {
			ajouter_food_negative();

		}
		for(int i=0;i<repas; i++) {
			ajouter_food_positive();
		}
		
        JPanel cp = new JPanel();
        cp.setLayout(new BorderLayout());
        cp.add(this,BorderLayout.CENTER);
        JPanel pan = new JPanel();
        pan.setLayout(new FlowLayout());
        JLabel jtf = new JLabel("Score");
        pan.add(jtf);

        score = new JButton(); //Ce boutton représente juste le nombre de repas que le serpent à déjà manger
        score.setBackground(Color.white);
        score.setText(String.valueOf(0));
        pan.add(score);
        
        JLabel objectif = new JLabel("Objectif");
        pan.add(objectif);
        JButton goal = new JButton(); // Ce boutton affiche l'objectif que l'utilsateur a défini au départ
        goal.setBackground(Color.white);
        goal.setText(String.valueOf(nbrFoodGoal));
        pan.add(goal);
        
        cp.add(pan,BorderLayout.SOUTH);
        fen.setContentPane(cp);
		
		timer = new Timer(Globals.DELAY,this);
		timer.start();
	}
	
    private void loadImages() {

        ImageIcon iid = new ImageIcon("src/resources/corps.png");
        corps = iid.getImage();

        ImageIcon iia = new ImageIcon("src/resources/pommevert.png");
        food = iia.getImage();

        ImageIcon iih = new ImageIcon("src/resources/tete.png");
        tete = iih.getImage();
        
        ImageIcon iip = new ImageIcon("src/resources/pommerouge.png");
        poison = iip.getImage();
    }
    

	public void goLeft() {
	    leftDirection = true;
	    rightDirection = false;
	    upDirection = false;
	    downDirection = false;
	}
	
	public void goRight() {
	    leftDirection = false;
	    rightDirection = true;
	    upDirection = false;
	    downDirection = false;
	}
	
	public void goUp() {
	    leftDirection = false;
	    rightDirection = false;
	    upDirection = true;
	    downDirection = false;
	}
	
	public void goDown() {
	    leftDirection = false;
	    rightDirection = false;
	    upDirection = false;
	    downDirection = true;
	}
	
    /** 
     * Fonction qui va faire déplacer le serpent d'une case
     * */
	
	public void deplacer() {
		nbrDeplacement++;
		tab[ x[(taille-1)]] [y[(taille-1)]] = 0; // Correspond au dernier emplacement de la queue du serpent
        for (int z = taille; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }
    	tab[x[0]][y[0]] = Globals.SNAKE_BODY;
    	
        if (leftDirection) {
            x[0] -= Globals.PIXEL_SIZE;
        }

        else if (rightDirection) {
            x[0] += Globals.PIXEL_SIZE;
        }

        else if (upDirection) {
            y[0] -= Globals.PIXEL_SIZE;
        }

        else if (downDirection) {
            y[0] += Globals.PIXEL_SIZE;
        }
        
        /* Si le nouvel endroit où se trouve le serpent contient de la nourriture, ce dernier le mange */
        if(tab[x[0]][y[0]] == Globals.FOOD_NEG || tab[x[0]][y[0]] == Globals.FOOD_POS) {
        	manger();
        	nbrDeplacement=0; // On réinitialise le nombre de déplacement car le serpent vient de manger
        }
	}

	/** 
	 * Fonction qui va décider de la direction que le serpent va prendre durant la prochaine étape 
	 * en étudiant tous les cas possible
	 * Si cela est possible, le serpent ne changera de direction que dans 30% des cas et reste
	 * donc sur la même direction dans 70% des cas 
	 * */
	public void change_direction() {
		double p = Math.random();
		
		/* Dans ce premier cas, le serpent se dirige vers la droite */
		if(rightDirection) {
			
			/* Si le serpent se trouve tout à droite de la map, 3 cas peuvent se présenter */
			if(x[0]==Globals.LIM_DROITE) { 
			
				/* Il se trouve au coin supérieur droite, donc ne peut que descendre */
				if(y[0]==Globals.LIM_HAUT) {goDown();} 
				
				/*  Il se trouve au coin inférieur droite, donc ne peut que monter */ 
				else if(y[0]==Globals.LIM_BAS) {goUp();}
				
				/* Il se trouve entre les 2 cas précédents, donc, peut descendre ou monter */
				else{
						if(p<0.5) goUp();
						else goDown();
					}
			}else {
			/* Ici, il ne se trouve quelque part entre la limite à droite et à gauche du plateau.
			 * 3 cas sont encore présents dans ce cas
			 */	
				
				/* Il se trouve sur le bord supérieur, donc ne peut pas aller en haut */
				if(y[0]==Globals.LIM_HAUT) {
					if(p>0.7 && p<=1) goDown();
				}
				
				/* Il se trouve sur le bord inférieur, donc ne peut pas aller en bas */
				else if(y[0]==Globals.LIM_BAS){
					if(p>0.7 && p<=1) goUp();
				}
				
				/* Il ne se trouve sur aucun des bords, donc peut aller soit  en haut, soit en bas, soit rester sur la même direction */
				else {
					if(p>0.7 && p<=0.85) {
						goUp();
					}
					else if(p>0.85 && p<=1.0) {
						goDown();
					}
				}
			}
		}
		
		
		/* On va appliquer le même raisonnement que précédemment pour les cas restants */
		else if(leftDirection) {
			if(x[0]==Globals.LIM_GAUCHE) {
				if(y[0]==Globals.LIM_HAUT) {goDown();}
				else if(y[0]==Globals.LIM_BAS) {goUp();}
				else{
						if(p<0.5) {goUp();}
						else {goDown();}
					}
			}
			else {
				if(y[0]==Globals.LIM_HAUT) {
					if(p>0.7 && p<=1 ) goDown();
				}
				else if(y[0]==Globals.LIM_BAS){
					if(p>0.7 && p<=1) goUp();
				}
				else {
					if(p>0.7 && p<=0.85) {
						goUp();
					}
					else if(p>0.85 && p<=1.0) {
						goDown();
					}
				}
			}
		}
		
		else if(upDirection) {
			if(y[0]==Globals.LIM_HAUT) {
				if(x[0]==Globals.LIM_GAUCHE) { goRight();}
				else if(x[0]==Globals.LIM_DROITE) {goLeft();}
				else{
						if(p<0.5) {goLeft();}
						else {goRight();}
					}
			}
			else {
				if(x[0]==Globals.LIM_GAUCHE) {
					if(p>0.7 && p<=1) goRight();
				}
				else if(x[0]==Globals.LIM_DROITE){
					if(p>0.7 && p<=1) goLeft();
				}
				else {
					if(p>0.7 && p<=0.85) {
						goRight();
					}
					else if(p>0.85 && p<=1.0) {
						goLeft();
					}
				}
			}
		}
		
		else if(downDirection) {
			if(y[0]==Globals.LIM_BAS) {
				if(x[0]==Globals.LIM_GAUCHE) {goRight();}
				else if(x[0]==Globals.LIM_DROITE) { goLeft();}
				else{
						if(p<0.5) {goLeft();}
						else {goRight();}
					}
			}
			else {
				if(x[0]==Globals.LIM_GAUCHE) {
					if(p>0.7 && p<=1) goRight();
				}
				else if(x[0]==Globals.LIM_DROITE){
					if(p>0.7 && p<=1) goLeft();
				}
				else {
					if(p>0.7 && p<=0.85) {
						goRight();
					}
					else if(p>0.85 && p<=1.0) {
						goLeft();
					}
				}
			}
		}
		
	}
	
	
	/** 
	 * Méthode qui ajoute un food négative quelque part sur le plateau 
	 * */
	public void ajouter_food_negative() {
        Random rand = new Random();
        int food_x = ((rand.nextInt(Globals.B_WIDTH))/Globals.PIXEL_SIZE )* Globals.PIXEL_SIZE;
        int food_y = ((rand.nextInt(Globals.B_HEIGHT))/Globals.PIXEL_SIZE) * Globals.PIXEL_SIZE;

        /* Si l'endroit où le nouveau "food" devait apparître est vide, on l'ajoute
         * sinon, on cherche une nouvelle endroit pour en mettre un
         */
        if(tab[food_x][food_y]==0 ) {
        	tab[food_x][food_y] = Globals.FOOD_NEG;
        	f.add(new FoodNegative(food_x,food_y));
        }
        else ajouter_food_negative();
	}
	
	
	/** Méthode qui ajoute un food positive quelque part sur le plateau 
	 */
	public void ajouter_food_positive() {
        Random rand = new Random();
        int food_x = ((rand.nextInt(Globals.B_WIDTH))/Globals.PIXEL_SIZE )* Globals.PIXEL_SIZE;
        int food_y = ((rand.nextInt(Globals.B_HEIGHT))/Globals.PIXEL_SIZE) * Globals.PIXEL_SIZE;
        
        if(tab[food_x][food_y]==0 ) {
        	f.add(new FoodPositive(food_x,food_y));
        	tab[food_x][food_y] = Globals.FOOD_POS;
        }
        else ajouter_food_positive();
	}
	
	/** Méthode qui supprime un food_négative du plateau
	 * @param x abscisse de l'élément en question
	 * @param y ordonnée de l'élément en question
	 * */
	public void enlever_food_Negative(int x,int y) {
		f.remove(new FoodNegative(x,y));
	}
	
	public void enlever_food_Positive(int x,int y) {
		f.remove(new FoodPositive(x,y));
		
	}

	
	/** Méthode qui fait faire manger le serpent
	 * */
	public void manger() {
		int a = x[0];
		int b = y[0];
		/* Le serpent grossit s'il mange le bon type d'aliment,sinon il rétrécit */
		if(tab[a][b] == Globals.FOOD_POS) {
			grossir(); 
			score.setText(String.valueOf(getFoodManger()));
			enlever_food_Positive(a,b); // on va enlever l'aliment qui vient d'être mangé pour le faire réapparaître après
			tab[a][b]=0;
			if (nbrFoodManger<nbrFoodGoal) ajouter_food_positive();
		}
		else if(tab[a][b] == Globals.FOOD_NEG) {
			retrecir();
			enlever_food_Negative(a,b);
			tab[a][b]=0;
			if(actuelle_repas_toxique<limit_repas_toxique) ajouter_food_negative();
		}
	}
	
	/** Méthode qui fait grossir le serpent
	 * */
	public void grossir() {
		taille = taille + 1;		
		nbrFoodManger++;
		{
			/* S'il arrive à atteindre l'objectif, le jeu s'arrête */
			if(nbrFoodManger>= nbrFoodGoal) {
				win=true;
				inGame=false;
				timer.stop();
			}
		}
	}
	
	
	/** Méthode qui fait rétrécir le serpent
	 * */
	public void retrecir(){
		taille = taille - 1;
		/* Si le serpent rétrécit trop, il meurt */
		if(taille<tailleMin) {
			inGame=false;
			timer.stop();
		}
	} 
	
	/** Méthode qui retourne le nombre d'aliment qu'il a déjà mangé
	 * */
	public int getFoodManger() {
		return nbrFoodManger;
	}

	/** Cette méthode permet au serpent de "sentir" les bons aliments et donc aller à sa recherche
	 * @param decalage nombre de case qui lui sépare de la case où il est en train de "sentir"
	 * @return true s'il a réussi à trouver de la nourriture quelque part  
	 * */
	public boolean trouve_nourriture(int decalage) {
		int tete_x=x[0];
		int tete_y=y[0];
		
		/* Le serpent cherche à sa gauche s'il y a de la nourriture */
			if( ((!rightDirection) && (tete_x - decalage*Globals.PIXEL_SIZE)>= (Globals.LIM_GAUCHE)
					&& tab[(tete_x - decalage*Globals.PIXEL_SIZE)][tete_y]== Globals.FOOD_POS )) {
				goLeft();return true;
			}
		/* Cherche à sa droite */
			if( (!leftDirection) && (tete_x + decalage*Globals.PIXEL_SIZE)<= Globals.LIM_DROITE 
					&& tab[(tete_x + decalage*Globals.PIXEL_SIZE)][tete_y]== Globals.FOOD_POS )  {
				goRight();return true; 
			}
			
		/* Cherche en haut */
			if( (!downDirection) && (tete_y - decalage*Globals.PIXEL_SIZE) >= Globals.LIM_HAUT 
					&& tab[tete_x][tete_y - decalage*Globals.PIXEL_SIZE]== Globals.FOOD_POS )  {
				goUp();return true; 
			}
			
		/* Cherche en bas */
			if( (!upDirection) && (tete_y + decalage*Globals.PIXEL_SIZE) <= Globals.LIM_BAS 
					&& tab[tete_x][tete_y + decalage*Globals.PIXEL_SIZE]== Globals.FOOD_POS )  {
				goDown();return true; 
			}
		return false;
	}
	
	/** Toutes les actions effectuées à chaque tour
	 * */
    public void actionPerformed(ActionEvent e) {
		
    	seconde += Globals.DELAY ;
    	/* On ajoute un food negative toutes les 4 secondes */
    	if(seconde>4000 && actuelle_repas_toxique<limit_repas_toxique) {
			ajouter_food_negative();
			actuelle_repas_toxique++;
			seconde=0;
		}
    	
    	/* Tous les 100 déplacements sans avoir manger, le serpent rétrécit */
		if(nbrDeplacement==100) {
			retrecir();
			nbrDeplacement=0;
		}
		
		/* Le serpent va d'abord chercher s'il n'y aurait pas de la bonne nourriture à coter de lui 
		 * pour décider de la direction qu'il doit prendre
		 * sinon, s'il n'en trouve pas, il choisit une direction au hasard */
    	if(!trouve_nourriture(1) && !trouve_nourriture(2) && !trouve_nourriture(3)) change_direction();
    	deplacer();
    	
    	repaint();
    }
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
        if(inGame) {
		setBackground(Color.CYAN);
        for (int z = 0; z < taille; z++) {
            if (z == 0) {
                g.drawImage(tete, x[z], y[z], this);
            } else {
                g.drawImage(corps, x[z], y[z], this);
            }
        }
        
        for(Food alim : f) {
        	if(alim instanceof FoodNegative) g.drawImage(poison, alim.getX(), alim.getY(), this);
        	if(alim instanceof FoodPositive) { 
        		g.drawImage(food, alim.getX(), alim.getY(), this);
        		}
        	}
        Toolkit.getDefaultToolkit().sync();
        }
        else {
        	String msg;
        	if(win) msg = "Goal achieved";
        	else msg = "Game Over";
        	Font small = new Font("Helvetica", Font.BOLD, 20);
            FontMetrics metr = getFontMetrics(small);
            g.setColor(Color.white);
            g.setFont(small);
            g.drawString(msg, (Globals.B_WIDTH - metr.stringWidth(msg)) / 2, Globals.B_HEIGHT / 2);
        }
	}
}