package facialRecognition;

import java.util.ArrayList;
import java.util.Arrays;

import java.lang.Math;

import Jama.Matrix;
import Jama.EigenvalueDecomposition;

/**
 * ACP class of the facial recognition project
 * @author Liah, Amira
 * @version 1.0
 */
public class ACP {
	/** The basis of eigenvectors. */
	private Vecteur[] base;
	/** The threshold above which an image is considered to correspond to a person. */
	private float seuil;
	/** The number of pixels we have in each image.*/
	private int n;
	/** The number of images we have*/
	private int m;
	/**The matrix whose columns correspond to our original images*/
	private Matrix matriceInitiale = new Matrix(n,m);
	/**The "average face" vector, calculated by averaging each pixel across all our images*/
	private Vecteur visageM;
	/**The original matrix from which the vector "VisageM" has been subtracted from each column*/
	private Matrix matriceEtude;
	/**"matriceEtude" transposed multiplied by "matriceEtude"*/
	private Matrix matriceReduite;
	/**"matriceReduite" whose columns have been projected in "base"*/
	private Matrix matriceProjection;
	/**Allows the method "comparer()" to return the image id and its percentage match with a new image*/
	private Retour r = new Retour();

	//private double[] valeurs;
	
	//constructors
	
//	public ACP(double[] valeurs) {
//		this.valeurs = valeurs;
//	}
	
	public ACP(Matrix matriceInitiale) {
		this.matriceInitiale = matriceInitiale;
		this.n=matriceInitiale.getRowDimension();
		this.m=matriceInitiale.getColumnDimension();
		this.visageM = new Vecteur(n);
		this.matriceEtude = new Matrix(n,m);
		this.matriceReduite = new Matrix(m,m);
		this.matriceProjection = new Matrix(m,m);
		visageMoyen();
		centrer();
		reduireDimension();
		creerBase();
		this.seuil = (float) 0.8;
	}
	
	
	
	private void visageMoyen() {
		for (int i=0; i<n; i++) {
			int moyenne=0;
			for (int j=0; j<m; j++) {
				moyenne =(int) (moyenne + matriceInitiale.get(i, j)); //moyenne de chaque pixel
			}
			visageM.setValueP(i, moyenne); //ajoute la valeur dans le vecteur du visage moyen
		}
	}
	
	private void centrer() {
		for (int i=0; i<n; i++) {
			for (int j=0; j<m; j++) {
				matriceEtude.set(i, j, matriceInitiale.get(i, j) - visageM.getValue(i)); //soustrait le visage moyen à chaque visage pour ne garder que les différences
			}
		}
	}
	
	private void reduireDimension() { //AT * A
		Matrix matriceTranspose = matriceEtude.transpose(); //Utilise la méthode transposé de Jama
		matriceReduite = matriceTranspose.times(matriceEtude); //utilise la méthode times (multiplication) de Jama
	}
	
	private double[] valeursPropres() { //Utilise la méthode de Jama pour trouver les valeurs propres
		EigenvalueDecomposition eig = matriceReduite.eig();
		return eig.getRealEigenvalues();
	}
	
	public double[] valeursProprestriees() {
		double[] valeurs = valeursPropres();
		//trie dans l'ordre croissant les vap
		Arrays.sort(valeurs);
		//inverse le tableau pour les avoir dans l'ordre décroissant
		int n = valeurs.length;
		double[] valeursTriees = new double[n];
		for (int i = 0; i < n; i++) {
            valeursTriees[i] = valeurs[n - 1 - i];
        }
		return valeursTriees;
	}
	
	
	private void creerBase() {
		EigenvalueDecomposition eig = matriceReduite.eig();
		Matrix vecteurPropre = eig.getV(); //prend les vecteurs propres avec la méthode de Jama
		int n = matriceReduite.getRowDimension();
		int nbComposantes = 2; //nombre de composantes principales que l'on garde, non défini précisément pour l'instant
		this.base = new Vecteur[nbComposantes];
		for (int i=0; i<nbComposantes; i++) { //transforme la matrice de vecteur propres en un tableau de Vecteur
			Vecteur V = new Vecteur(n);
			for (int j=0; j<n; j++) {
				V.setValueP(j, vecteurPropre.get(j, i));
			}
			this.base[i]=V;
		}
	}
	
	private Vecteur prendreVecteur(int i, Matrix M) { //prend un vecteur d'une matrice
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
		Vecteur vecteurIm = im.process(); //traite l'image que l'on veut comparer
		vecteurIm = projection(base, vecteurIm); //projete l'image que l'on veut comparer
		Vecteur compare = new Vecteur(n);
		for (int i=0; i<m; i++) {
			double b= Math.abs(1-(projeter.getValue(i) - vecteurIm.getValue(i)/projeter.getValue(i))); //compare chaque pixel pour avoir la ressemblance en pourcentage
			compare.setValueP(i, b);
		}
		for (int i=0; i<m; i++){
			pourcentage += compare.getValue(i);
		}
		pourcentage = pourcentage / m; //pourcentage global en faisant la moyenne des pourcentages
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
		ACP acp=new ACP(M);
		double[] vap = acp.valeursPropres();
		//Vecteur[] vep = acp.creerBase();
		System.out.println(Arrays.toString(vap));
		//System.out.println(Arrays.toString(creerBase()));
		
		

        //System.out.println(Arrays.toString(vp));

        
//        JFrame f = new JFrame("Valeurs propres"); //créer une fenêtre graphique appelée "Valeurs Propres"
//
//        ACP panneau = new ACP(vp); //Créer un panneau de dessin
//
//        f.add(panneau); //ajoute le panneau dans la fenêtre
//
//        f.setSize(1000,700); //taille de la fenêtre
//
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //arrête le programme quand on ferme la fenêtre
//
//        f.setVisible(true); //affiche réellement la fenêtre
	}
}
