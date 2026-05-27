package projet;

import java.util.ArrayList;
import java.util.Arrays;

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
		this.seuil = 80;
	}
	
	
	private void visageMoyen() {
		for (int i=0; i<n; i++) {
			int moyenne=0;
			for (int j=0; j<m; j++) {
				moyenne =(int) (moyenne + matriceInitiale.get(i, j));
			}
			visageM.setValueP(i, moyenne);
		}
	}
	
	private void centrer() {
		for (int i=0; i<n; i++) {
			for (int j=0; j<m; j++) {
				matriceEtude.set(i, j, matriceInitiale.get(i, j) - visageM.getValue(i));
			}
		}
	}
	
	private void reduireDimension() { //AT x A 
		Matrix matriceTranspose = matriceEtude.transpose();
		matriceReduite = matriceTranspose.times(matriceEtude);
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
	
	private static double[] valeursPropres(Matrix M) {
		EigenvalueDecomposition eig = M.eig();
		double[] valeurs = eig.getRealEigenvalues();
		return valeurs;
	}
	
	
	@Override //redefini une méthode de la classe parent 
	 protected void paintComponent(Graphics g) {

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
		Matrix vecteurPropre = eig.getV();
		int n=M.getRowDimension();
		
		int nbComposantes = 2;
		Vecteur [] base = new Vecteur[nbComposantes];
		for (int i=0; i<nbComposantes; i++) {
			Vecteur V = new Vecteur(n);
			for (int j=0; j<n; j++) {
				V.setValueP(j, vecteurPropre.get(j, i));
			}
			base[i]=V;
		}
		
		return base;
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
