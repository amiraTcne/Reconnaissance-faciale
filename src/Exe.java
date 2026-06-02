import laBdd.*;
import projet.*;
import imgs.*;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.*;

public class Exe{
    public static void main(String[] args){
        Bdd test = new Bdd();
        for(Personne p : test.imagesBdd.keySet()) {
            System.out.println(p.getPrenom());
			for(ImageVisage img : test.imagesBdd.get(p)) {
                System.out.println(img.getId());
                System.out.println(img.getPath());
			}
		}
        Personne p2 = new Personne("dataset/reference/Zac_Efron_4.jpg");
        for(Personne p : test.imagesBdd.keySet()) {
            if(p.equals(p2)){
            System.out.println("test");

            }
		}
        System.out.println(test.rechercher(1).getNom());
        
        /**Test ACP*/
        
        /**Affichage du visage moyen*/
        Bdd bdd = new Bdd(); 
        ACP acp = new ACP(bdd.createA());

        Vecteur vecteur = acp.getVisageM();
        int largeur = 100;
        int hauteur = 100;

        BufferedImage img =
                new BufferedImage(
                        largeur,
                        hauteur,
                        BufferedImage.TYPE_BYTE_GRAY);

        for(int y=0;y<hauteur;y++){

            for(int x=0;x<largeur;x++){

                int indice =
                        x * hauteur + y;

                int valeur =
                        (int)vecteur.getValue(
                                indice);

                valeur =
                        Math.max(
                                0,
                                Math.min(
                                        255,
                                        valeur));

                img.getRaster().setSample(
                        x,
                        y,
                        0,
                        valeur);
            }
        }
        JFrame frame = new JFrame();

        frame.pack();
        
        frame.setSize(500,500);

        frame.add(new JLabel(new ImageIcon(img)));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
        
        /**Affichage du graphe des valeurs propres*/	    
	    GrapheValeursPropres graphe = new GrapheValeursPropres(acp);
	    System.out.println(Arrays.toString(acp.valeursPropresTriees()));
	    
	    JFrame f = new JFrame("Graphique des valeurs propres"); //créer une fenêtre graphique appelée "Graphique des valeurs propres"
	    f.add(graphe); //ajoute le graphe dans la fenêtre
	    f.setSize(2000, 1000); //taille de la fenêtre
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //arrête le programme quand on ferme la fenêtre
	    f.setVisible(true); //affiche réellement la fenêtre
    }
}
