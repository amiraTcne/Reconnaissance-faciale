import laBdd.*;
import projet.*;
import imgs.*;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.*;

public class Exe{
    public static void main(String[] args){
        BDD test = new BDD();
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
        BDD bdd = new BDD(); 
        ACP acp = new ACP(bdd.createA());
        DisplayImg d = new DisplayImg();

        Vecteur vecteur = acp.getVisageM();
        int largeur = 100;
        int hauteur = 100;
        
        d.affImg(vecteur);
        
        
        /**Affichage du graphe des valeurs propres*/	    
	    GrapheValeursPropres graphe = new GrapheValeursPropres(acp);
	    System.out.println(Arrays.toString(acp.valeursPropresTriees()));
	    
	    JFrame f = new JFrame("Graphique des valeurs propres"); //créer une fenêtre graphique appelée "Graphique des valeurs propres"
	    f.add(graphe); //ajoute le graphe dans la fenêtre
	    f.setSize(2000, 1000); //taille de la fenêtre
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //arrête le programme quand on ferme la fenêtre
	    f.setVisible(true); //affiche réellement la fenêtre
	    
	    /**Affichage valeur propre (image)*/
	    vecteur = acp.eigenfaces()[5];
	    d.affImg(vecteur);
        
        /**Comparaison image dans BDD*/
        ImageVisage im = new ImageVisage(101, "dataset/reference/Zendaya_Coleman_3.jpg");
        d.affImg(im.process());
        Retour[] tab = acp.tableauComparaison(im);
        System.out.println(tab[0].getPourcentage());
        System.out.println(tab[99].getPourcentage());
        
        /**Affichage de l'image la plus proche*/
        vecteur = acp.prendreVecteur(tab[0].getIndice(), acp.getMatriceInitiale());
        d.affImg(vecteur);
        vecteur = acp.prendreVecteur(tab[99].getIndice(), acp.getMatriceInitiale());
        d.affImg(vecteur);
        
        /**Comparaison image pas dans BDD mais personne dedans*/
        ImageVisage imC = new ImageVisage(102, "dataset/test/connu/Emma_Watson_6.jpg");
        d.affImg(imC.process());
        Retour[] tabC = acp.tableauComparaison(imC);
        System.out.println(tabC[0].getPourcentage());
        System.out.println(tabC[99].getPourcentage());
        
        vecteur = acp.prendreVecteur(tabC[0].getIndice(), acp.getMatriceInitiale());
        d.affImg(vecteur);
        vecteur = acp.prendreVecteur(tabC[99].getIndice(), acp.getMatriceInitiale());
        d.affImg(vecteur);
        
        /**Comparaison image pas dans BDD et personne non plus*/
        ImageVisage imI = new ImageVisage(102, "dataset/test/inconnu/Tom_Holland_1.jpg");
        d.affImg(imI.process());
        Retour[] tabI = acp.tableauComparaison(imI);
        System.out.println(tabI[0].getPourcentage());
        System.out.println(tabI[99].getPourcentage());
        
        vecteur = acp.prendreVecteur(tabI[0].getIndice(), acp.getMatriceInitiale());
        d.affImg(vecteur);
        vecteur = acp.prendreVecteur(tabI[99].getIndice(), acp.getMatriceInitiale());
        d.affImg(vecteur);
    }
}
