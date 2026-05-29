package projet;

import java.util.ArrayList;
import java.util.Arrays;

import java.lang.Math;

import javax.swing.*;
import java.awt.*;

import Jama.Matrix;
import Jama.EigenvalueDecomposition;

public class ACP extends JPanel{
	private ArrayList <Vecteur> base = new ArrayList <Vecteur>();
	private float seuil;
	private int n;
	private int m;
	private Matrix matriceInitiale = new Matrix(n,m);
	private Vecteur visageM = new Vecteur(n);
	private Matrix matriceEtude = new Matrix(n,m);
	private Matrix matriceReduite = new Matrix(n,m);
	private Matrix matriceProjection = new Matrix(n,m);
	private Retour r = new Retour();
	
	private double[] valeurs;
	
	public ACP(double[] valeurs) {
		this.valeurs = valeurs;
	}
	
	public ACP(Matrix matriceInitiale, int n, int m) {
		this.matriceInitiale = matriceInitiale;
		this.n=matriceInitiale.getRowDimension();
		this.m=matriceInitiale.getColumnDimension();
		visageMoyen();
		centrer();
		reduireDimension();
		this.seuil = (float) 0.8;
	}
	
	
	private void visageMoyen() {
		for (int i=0; i<n; i++) {
			int moyenne=0;
			for (int j=0; j<m; j++) {
				moyenne =(int) (moyenne + matriceInitiale.get(i, j)); /**moyenne de chaque pixel*/
			}
			visageM.setValueP(i, moyenne); /**ajoute la valeur dans le vecteur du visage moyen*/
		}
	}
	
	private void centrer() {
		for (int i=0; i<n; i++) {
			for (int j=0; j<m; j++) {
				matriceEtude.set(i, j, matriceInitiale.get(i, j) - visageM.getValue(i)); /**soustrait le visage moyen à chaque visage pour ne garder que les différences*/
			}
		}
	}
	
	private void reduireDimension() { /**AT * A*/
		Matrix matriceTranspose = matriceEtude.transpose(); /**Uitilise la méthode transposé de Jama*/
		matriceReduite = matriceTranspose.times(matriceEtude); /**utilise la méthode times (multiplication) de Jama*/
//		for (int i=0; i<matriceEtude.size(); i++) {
//			for (int j=0; j<matriceEtude.size(); j++) {
//				int somme=0;
//				for (int k=0; k<matriceEtude[0].size(); k++) {
//					somme = somme + matriceEtude[i][k]*matriceTranspose[k][j];
//				}
//				matriceReduite[i][j] = somme;
//			}
//		}
	}
	
	private static double[] valeursPropres(Matrix M) {/**Utilise la méthode de Jama pour trouver les valeurs propres*/
		EigenvalueDecomposition eig = M.eig();
		double[] valeurs = eig.getRealEigenvalues();
		return valeurs;
	}
	
	
	@Override //redefini une méthode de la classe parent 
	 protected void paintComponent(Graphics g) { /**utilise swing et awt pour créer et affiche le graphique*/

	        super.paintComponent(g); //permet de partir d'un dessin vide

	        // axes
	        g.drawLine(50, 250, 350, 250); //axe horizontal (x), 
	        //départ:(50, 250) arrivée: (350, 250), 50 pour ne pas coller 
	        //au bord et 250 pour ne coller au sommet
	        g.drawLine(50, 550, 50, 50); //axe vertical (y)

	        // points
	        for (int i = 0; i < valeurs.length; i++) {

	            int x = 50 + i * 50;//points espacés de 50px

	            int y = 250 - (int)(valeurs[i] * 20);//car java augmente les y vers le bas et on veut vers le haut 

	            g.fillOval(x - 3, y - 3, 6, 6); //dessine un point (X, Y, largeur, hauteur), 
	            //-3 car jave place à partir du coin en haut à gauche, on à un cercle de 6 de 
	            //diamètre donc on enlève le rayon pour centré
	        }
	    }
	
	private static Vecteur[] creationBase(Matrix M) {
		EigenvalueDecomposition eig = M.eig();
		Matrix vecteurPropre = eig.getV(); /**prend les vecteurs propres avec la méthode de Jama*/
		int n=M.getRowDimension();
		
		int nbComposantes = 2; /**nombre de composantes principales que l'on garde*/
		Vecteur [] base = new Vecteur[nbComposantes];
		for (int i=0; i<nbComposantes; i++) { /**transforme la matrice de vecteur propres en un tableau de Vecteur*/
			Vecteur V = new Vecteur(n);
			for (int j=0; j<n; j++) {
				V.setValueP(j, vecteurPropre.get(j, i));
			}
			base[i]=V;
		}
		
		return base;
	}
	
	private Vecteur prendreVecteur(int i, Matrix M) { /**prend un vecteur d'une matrice*/
		n= M.getRowDimension();
		Vecteur V = new Vecteur(n);
		for (int j=0; j<n; j++) {
			V.setValueP(j, M.get(j, i));
		}
		return V;
	}
	
	private Vecteur projection(Vecteur[] base, Vecteur V) {
		n = V.getTaille();
		Vecteur proj = new Vecteur(n, 1); //Créer un vecteur nul de taille n
		for (int i=0; i<base.length; i++) {
			double a=0;
			for (int j=0; j<n; j++) {
				a+=base[i].getValue(j) * V.getValue(j); //Produit scalaire
			}
			for (int j=0; j<n; j++) {
				double b = proj.getValue(j) + a*base[i].getValue(j);
				proj.setValueP(j,  b);
			}
		}
		return proj;
	}
	
	private float comparer(ImageVisage im, Vecteur projeter, Vecteur[] base) {
		float pourcentage=0;
		m= projeter.getTaille();
		Vecteur vecteurIm = im.process(); /**traite l'image que l'on veut comparer*/
		vecteurIm = projection(base, vecteurIm); /**projete l'image que l'on veut comparer*/
		Vecteur compare = new Vecteur(n);
		for (int i=0; i<m; i++) {
			double b= Math.abs(1-(projeter.getValue(i) - vecteurIm.getValue(i)/projeter.getValue(i))); //compare chaque pixel pour avoir la ressemblance en pourcentage
			compare.setValueP(i, b);
		}
		for (int i=0; i<m; i++){
			pourcentage += compare.getValue(i);
		}
		pourcentage = pourcentage / m; /**pourcentage global en faisant la moyenne des pourcentages*/
		return pourcentage;
	}
	
	public Retour[] tableauComparaison(Matrix M, ImageVisage im, Vecteur[] base) {
		m = M.getColumnDimension();
		Retour[] tableau = new Retour[m];
		//Création du tableau avec pour chque image l'indice et le pourcentage de ressemblance
		for (int i=0; i<m; i++) {
			Retour r = new Retour();
			Vecteur V = prendreVecteur(i, M);
			float p = comparer(im, V, base);
			r.setPourcentage(p);
			r.setIndice(i);
			tableau[i]=r;
		}
		
		//tri fusion  du tableau par rapport au pourcentage
		triFusion(tableau);
		
		return tableau;
	}
	
	private void triFusion(Retour[] tab) {
	    if (tab.length <= 1) return;

	    int milieu = tab.length / 2;

	    Retour[] gauche = new Retour[milieu];
	    Retour[] droite = new Retour[tab.length - milieu];

	    for (int i = 0; i < milieu; i++) {
	        gauche[i] = tab[i];
	    }

	    for (int i = milieu; i < tab.length; i++) {
	        droite[i - milieu] = tab[i];
	    }

	    triFusion(gauche);
	    triFusion(droite);

	    fusion(tab, gauche, droite);
	}
	
	private void fusion(Retour[] tab, Retour[] gauche, Retour[] droite) {

	    int i = 0;
	    int j = 0;
	    int k = 0;

	    while (i < gauche.length && j < droite.length) {

	        // ordre décroissant (plus grand pourcentage d'abord)
	        if (gauche[i].getPourcentage() >= droite[j].getPourcentage()) {
	            tab[k] = gauche[i];
	            i++;
	        } else {
	            tab[k] = droite[j];
	            j++;
	        }

	        k++;
	    }

	    while (i < gauche.length) {
	        tab[k] = gauche[i];
	        i++;
	        k++;
	    }

	    while (j < droite.length) {
	        tab[k] = droite[j];
	        j++;
	        k++;
	    }
	}
	
	public Vecteur identifier(Retour[] tableau, Matrix M) {
		if (tableau[0].getPourcentage()>=seuil) {
			Vecteur V = prendreVecteur(tableau[0].getIndice(), M);
			return (V);
		}
		return null;
	}
	
	public static void main(String[] args) {
		Matrix M = new Matrix(new double[][] {
			{4, 2, 6, 7}, 
			{1, 3, 3, 1},
			{2, 2, 4, 7},
			{4, 7, 1, 1}
		});
		System.out.println(Arrays.toString(valeursPropres(M)));
		System.out.println(Arrays.toString(creationBase(M)));
		
		double[] vp = valeursPropres(M);

        System.out.println(Arrays.toString(vp));

        
        JFrame f = new JFrame("Valeurs propres"); //créer une fenêtre graphique appelée "Valeurs Propres"

        ACP panneau = new ACP(vp); //Créer un panneau de dessin

        f.add(panneau); //ajoute le panneau dans la fenêtre

        f.setSize(1000,700); //taille de la fenêtre

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //arrête le programme quand on ferme la fenêtre

        f.setVisible(true); //affiche réellement la fenêtre
	}
}
