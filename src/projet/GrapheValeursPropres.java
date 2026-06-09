package projet;

import javax.swing.*;
import java.awt.*;
import Jama.Matrix;


public class GrapheValeursPropres extends JPanel{
	private ACP acp;
	
	public GrapheValeursPropres(ACP acp) {
		this.acp=acp;
	}
	/**
	 * This method uses swing and awt to create and display the graph.
	 * @param g 
	 */
	@Override
	protected void paintComponent(Graphics g) { //utilise swing et awt pour créer et affiche le graphique

	        super.paintComponent(g); //permet de partir d'un dessin vide
	        
	        double[] valeurs = acp.valeursPropresTriees();
	        
	        // axes
	        g.drawLine(50, 1100, 5000, 1100); //axe horizontal (x), 
	        //départ:(50, 250) arrivée: (350, 250), 50 pour ne pas coller 
	        //au bord et 250 pour ne coller au sommet
	        g.drawLine(50, 1100, 50, 50); //axe vertical (y)
	        // points
			for(int i=1;i<10;i++){
				g.drawString(String.valueOf(i)+"E7",40,1100-105*i);
			}
	        for (int i = 0; i < valeurs.length; i++) {

	            int x = 50 + i * 30;//points espacés de 50px

	            int y = 1150 - (int)(valeurs[i] /87000);//car java augmente les y vers le bas et on veut vers le haut

	            g.fillOval(x - 3, y - 3, 6, 6); //dessine un point (X, Y, largeur, hauteur),
				g.drawString(String.valueOf(i+1),x,1110);
	            //-3 car jave place à partir du coin en haut à gauche, on à un cercle de 6 de 
	            //diamètre donc on enlève le rayon pour centré
	        }
	}
}
