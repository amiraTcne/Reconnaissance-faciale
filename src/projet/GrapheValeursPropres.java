package facialRecognition;

import javax.swing.*;
import java.awt.*;
import Jama.Matrix;


public class GrapheValeursPropres extends JPanel{
	private ACP acp;
	
	public GrapheValeursPropres(ACP acp) {
		this.acp=acp;
	}
	@Override //redefini une méthode de la classe parent 
	protected void paintComponent(Graphics g) { //utilise swing et awt pour créer et affiche le graphique

	        super.paintComponent(g); //permet de partir d'un dessin vide
	        
	        double[] valeurs = acp.valeursProprestriees();
	        
	        // axes
	        g.drawLine(50, 560, 5000, 560); //axe horizontal (x), 
	        //départ:(50, 250) arrivée: (350, 250), 50 pour ne pas coller 
	        //au bord et 250 pour ne coller au sommet
	        g.drawLine(50, 1500, 50, 50); //axe vertical (y)

	        // points
	        for (int i = 0; i < valeurs.length; i++) {

	            int x = 50 + i * 50;//points espacés de 50px

	            int y = 560 - (int)(valeurs[i] *20);//car java augmente les y vers le bas et on veut vers le haut , le *20 permet de mettre le graphique à l'échelle de l'écran

	            g.fillOval(x - 3, y - 3, 6, 6); //dessine un point (X, Y, largeur, hauteur), 
	            //-3 car jave place à partir du coin en haut à gauche, on à un cercle de 6 de 
	            //diamètre donc on enlève le rayon pour centré
	        }
	}
	public static void main(String[] args) {
		Matrix M = new Matrix(new double[][] {
			{4, 2, 6, 7}, 
			{1, 3, 3, 1},
			{2, 2, 4, 7},
			{4, 7, 1, 1}
		});
	    ACP test = new ACP(M);
	    
	    GrapheValeursPropres graphe = new GrapheValeursPropres(test);
	    
	    JFrame f = new JFrame("Graphique des valeurs propres"); //créer une fenêtre graphique appelée "Graphique des valeurs propres"
	    f.add(graphe); //ajoute le graphe dans la fenêtre
	    f.setSize(1000, 700); //taille de la fenêtre
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //arrête le programme quand on ferme la fenêtre
	    f.setVisible(true); //affiche réellement la fenêtre
	}
	
}
