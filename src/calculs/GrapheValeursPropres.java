package calculs;

import javax.swing.*;
import java.awt.*;
import Jama.Matrix;


/**
 * This class is used to display a graph allowing us to visualize the importance of the different eigenvalues 
 * @author Pernet Gabriel
 * @author Riche Liah
 * @author Tarchoune Amira
 * @version 0.5
 */
public class GrapheValeursPropres extends JPanel{
	private ACP acp;

    /**
     * Constructs a new GrapheValeursPropres
     * @param acp value of acp for the new GrapheValeursPropres
     */
	public GrapheValeursPropres(ACP acp) {
		this.acp=acp;
	}
	/**
	 * This method uses swing and awt to create and display the graph.
	 * We pass the delegate a copy of the Graphics object to protect the rest of the paint code from irrevocable changes
	 * @param g the Graphics object to protect
	 */
	@Override
	protected void paintComponent(Graphics g) 

	        super.paintComponent(g); // we start from an empty drawing
	        
	        double[] valeurs = acp.valeursPropresTriees();
	        
	        g.drawLine(50, 1100, 5000, 1100); // x axis
	        // start:(50, 250) finish: (350, 250), 50 and 250 to leave a margin on the left and the top
	        g.drawLine(50, 1100, 50, 50); // y axis
	        // points
			for(int i=1;i<10;i++){
				g.drawString(String.valueOf(i)+"E7",40,1100-105*i);
			}
	        for (int i = 0; i < valeurs.length; i++) {

	            int x = 50 + i * 30;// points are 50px apart

	            int y = 1150 - (int)(valeurs[i] /87000);//because java increments y topdown and we want it to be bottom up

	            g.fillOval(x - 3, y - 3, 6, 6); //draws a point (X, Y, width, height), -3 because java places it starting from the top left corner and we place a circle with a diameter of 6 (so we do -3 to have it centered where we want it to)
				g.drawString(String.valueOf(i+1),x,1110);
	        }
	}
}
