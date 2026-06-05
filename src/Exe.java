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
        /**Test ACP*/
        
        
        Bdd bdd = new Bdd(); 
        ACP acp = new ACP(bdd.createA());
        
        
        //Affichage du graphe des valeurs propres
	    GrapheValeursPropres graphe = new GrapheValeursPropres(acp);
	    System.out.println(Arrays.toString(acp.valeursPropresTriees()));
	    
	    JFrame f = new JFrame("Graphique des valeurs propres"); //créer une fenêtre graphique appelée "Graphique des valeurs propres"
	    f.add(graphe); //ajoute le graphe dans la fenêtre
	    f.setSize(2000, 1200); //taille de la fenêtre
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //arrête le programme quand on ferme la fenêtre
	    f.setVisible(true); //affiche réellement la fenêtre
	    /*
        //Affichage du visage moyen
        DisplayImg.affImg(acp.getVisageM());
        
	    //Affichage valeur propre (image)
	    DisplayImg.affImg(acp.eigenfaces()[5]);
        
        
        // Projection d'image
        ImageVisage imProj = bdd.getImg(2);
        DisplayImg.affImgO(imProj);
        DisplayImg.affImgP(imProj);
        DisplayImg.affImg(acp.projeterImg(new Vecteur(imProj.processed())));
        
        //Comparaison image dans Bdd
        ImageVisage im = new ImageVisage(101, "dataset/reference/Zendaya_Coleman_3.jpg");
        DisplayImg.affImg(im.process());
        Retour[] tab = acp.tableauComparaison(im);
        System.out.println(tab[0].getPourcentage());// taux de ressemblance de la plus proche
        System.out.println(tab[99].getPourcentage());// taux de ressemblance de la plus éloignée
        DisplayImg.affImg(acp.prendreVecteur(tab[0].getIndice(), acp.getMatriceInitiale()));// image la plus proche
        DisplayImg.affImg(acp.prendreVecteur(tab[99].getIndice(), acp.getMatriceInitiale()));// image la plus éloignée
        
        //Comparaison image pas dans Bdd mais personne dedans
        ImageVisage imC = new ImageVisage(102, "dataset/test/connu/Emma_Watson_6.jpg");
        DisplayImg.affImg(imC.process());
        Retour[] tabC = acp.tableauComparaison(imC);
        System.out.println(tabC[0].getPourcentage());// taux de ressemblance de la plus proche
        System.out.println(tabC[99].getPourcentage());// taux de ressemblance de la plus éloignée
        DisplayImg.affImg(acp.prendreVecteur(tabC[0].getIndice(), acp.getMatriceInitiale()));// image la plus proche
        DisplayImg.affImg(acp.prendreVecteur(tabC[99].getIndice(), acp.getMatriceInitiale()));// image la plus éloignée
        */
        //Comparaison image pas dans Bdd et personne non plus
        ImageVisage imI = new ImageVisage(103, "dataset/test/inconnu/Tom_Holland_1.jpg");
        DisplayImg.affImg(imI.process());
        Retour[] tabI = acp.tableauComparaison(imI);
        System.out.println(tabI[0].getPourcentage());// taux de ressemblance de la plus proche
        System.out.println(tabI[99].getPourcentage());// taux de ressemblance de la plus éloignée
        DisplayImg.affImg(acp.prendreVecteur(tabI[0].getIndice(), acp.getMatriceInitiale()));// image la plus proche
        DisplayImg.affImg(acp.prendreVecteur(tabI[99].getIndice(), acp.getMatriceInitiale()));// image la plus éloignée
        
    }
}
