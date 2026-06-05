package projet;

import java.util.Arrays;

import java.lang.Math;

import Jama.Matrix;
import Jama.EigenvalueDecomposition;
import imgs.ImageVisage;

/**
 * ACP class of the facial recognition project
 * @author Liah, Amira
 * @version 1.2
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
	/**The matrix whose columns correspond to our original images and whose rows correspond to the pixels*/
	private Matrix matriceInitiale;
	/**The "average face" vector, calculated by averaging each pixel across all our images*/
	private Vecteur visageM;
	/**The original matrix from which the vector "VisageM" has been subtracted from each column*/
	private Matrix matriceEtude;
	/**"matriceEtude" transposed multiplied by "matriceEtude"*/
	private Matrix matriceReduite;
	/**"matriceReduite" whose columns have been projected in "base"*/
	private Matrix matriceProjection;
	
	
	//constructor
	
	/** 
	 * Constructor creating an instance of ACP.
	 * @param matriceInitial The initial matrix whose columns correspond to the original images.
	 */
	public ACP(Matrix matriceInitiale) {
		this.matriceInitiale = matriceInitiale;
		this.n=matriceInitiale.getRowDimension();
		this.m=matriceInitiale.getColumnDimension();
		this.visageM = new Vecteur(n);
		this.matriceEtude = new Matrix(n,m);
		this.matriceReduite = new Matrix(m,m);
		visageMoyen();
		centrer();
		reduireDimension();
		creerBase();
		projeterMatrice();
		this.seuil = (float) 75;
	}
	
	public Vecteur getVisageM() {
		return visageM;
	}

	public Matrix getMatriceInitiale() {
		return matriceInitiale;
	}

	public Matrix getMatriceEtude() {
		return matriceEtude;
	}

	/** 
	 * Calculates the average face using matriceInitial.
	 */
	private void visageMoyen() {
		for (int i=0; i<n; i++) {
			double moyenne=0;
			for (int j=0; j<m; j++) {
				moyenne = (moyenne + matriceInitiale.get(i, j)); //moyenne de chaque pixel
			}
			visageM.setValueP(i, moyenne/m); //ajoute la valeur dans le vecteur du visage moyen
		}
	}
	
	/** 
	 * Subtracts the mean face from each column of matriceInitial.
	 * This gives us the matrix we will be working with : matriceEtude.
	 */
	private void centrer() {
		for (int i=0; i<n; i++) {
			for (int j=0; j<m; j++) {
				matriceEtude.set(i, j, matriceInitiale.get(i, j) - visageM.getValue(i)); //soustrait le visage moyen à chaque visage pour ne garder que les différences
			}
		}
	}
	
	/** 
	 * Calculates matriceEtude transposed multiplied by matriceEtude. 
	 * The goal is to reduce the dimension in order to find the eigenvalues of matriceEtude using as few calculations as possible.
	 */
	private void reduireDimension() { //AT * A
		Matrix matriceTranspose = matriceEtude.transpose(); //Utilise la méthode transposé de Jama
		matriceReduite = matriceTranspose.times(matriceEtude); //utilise la méthode times (multiplication) de Jama
	}
	
	/** 
	 * Calculates the eigenvalues of matriceReduite.
	 * @return The eigenvalues of matriceReduite.
	 */
	private double[] valeursPropres() { //Utilise la méthode de Jama pour trouver les valeurs propres
		EigenvalueDecomposition eig = matriceReduite.eig();
		return eig.getRealEigenvalues();
	}
	
	/** 
	 * Sorts the eigenvalues of matriceReduit in order to place them in descending order in a graph.
	 * @return Sorted eigenvalues of martriceReduite.
	 */
	public double[] valeursPropresTriees() {
		double[] valeurs = valeursPropres();
		//trie dans l'ordre croissant les vap
		Arrays.sort(valeurs);
		//inverse le tableau pour les avoir dans l'ordre décroissant
		int taille = valeurs.length;
		double[] valeursTriees = new double[taille];
		for (int i = 0; i < taille; i++) {
            valeursTriees[i] = valeurs[taille - 1 - i];
        }
		return valeursTriees;
	}

	/**
	 * Calculates the number of eigenfaces kept into the basis using cumulative variance.
	 * @return The number of eigenfaces kept.
	 */
	public int nbCompoVarCumulee(double seuil) {
		double s = 0;
		double[] vap = valeursPropresTriees();
		for (int i=0; i<vap.length;i++) {
			s+=vap[i];
		}
		if (s == 0) {
			return 0;
		}
		double sommePartielle = 0;
		int nbComposantes = 0;
		// var = sommePartielle/s : tant qu'elle est < 0.9 et qu'on a pas déjà additionné toutes les vap, on continue.
		// Théoriquement, juste la premiere condition suffit car quand on aura additionné toutes les vap donc que 
		// nbComposantes sera égal à vap.length, la variance sera égale à 1. On met la 2e conditions juste par prudence.
		while (sommePartielle/s<seuil && nbComposantes<vap.length) {
			sommePartielle+=vap[nbComposantes];
			nbComposantes ++;
		}
		return nbComposantes;
	}
	
	/** 
	 * Calculates the eigenvectors of matriceReduite.
	 * @return The eigenvectors of matriceReduite.
	 */
	public Matrix vecteursPropres() {
		EigenvalueDecomposition eig = matriceReduite.eig();
		return eig.getV();
	}
	
	/**
	 * Sorts the original indexes of the eigenvalues (the valeursPropres()' ones) according to the result of valeursPropresTriees() (descending order).
	 * The goal is to know which are the first eigenfaces we keep for the basis (these are the ones with the highest associated eigenvalues).
	 * @return The sorted indexes of the eigenvalues (the index of the highest one first and the index of the lower one last).
	 */
	public int[] indicesValeursPropresTriees() {
		double[] valeurs = valeursPropres();
		//indices contient les indices originaux, de 0 à m-1, avec m le nb de vap
		Integer[] indices = new Integer[valeurs.length];
	    for (int i = 0; i < valeurs.length; i++) {
	        indices[i] = i;
	    }
	    //
	    Arrays.sort(indices, (i, j) -> Double.compare(valeurs[j], valeurs[i]));
	    int[] indicesTries = new int[valeurs.length];
	    for (int i = 0; i < indices.length; i++) {
	    	indicesTries[i] = indices[i];
	    }
	    return indicesTries;
	}
	
	/**
	 * Calculates all the eigenfaces based on the eigenvectors.
	 * @return A table with all the eigenfaces.
	 */
	public Vecteur[] eigenfaces() {
		EigenvalueDecomposition eig = matriceReduite.eig();
		Matrix vecteurPropre = eig.getV();
		int m = matriceReduite.getRowDimension();
		int[] ordre = indicesValeursPropresTriees();
		Vecteur[] eigenfaces = new Vecteur[m];
		for (int i=0; i<m; i++) { //transforme la matrice de vecteur propres en un tableau de Vecteur
			Matrix v = new Matrix(m,1);
			for (int j=0; j<m; j++) {
				v.set(j,0,vecteurPropre.get(j, ordre[i]));
			}
			eigenfaces[i]=prendreVecteur(0,matriceEtude.times(v));
		}
		return eigenfaces;
	}
	
	/** 
	 * Creates the basis of eigenfaces based on the number calculated before thanks to cumulative variance.
	 */
	private void creerBase() {
		EigenvalueDecomposition eig = matriceReduite.eig();
		Matrix vecteurPropre = eig.getV(); //prend les vecteurs propres avec la méthode de Jama
		int m = matriceReduite.getRowDimension();
		int nbComposantes = nbCompoVarCumulee(0.9); //nombre de composantes principales que l'on garde
		int[] ordre = indicesValeursPropresTriees();
		base = new Vecteur[nbComposantes];
		for (int i=0; i<nbComposantes; i++) { //transforme la matrice de vecteur propres en un tableau de Vecteur
			Matrix v = new Matrix(m,1);
			for (int j=0; j<m; j++) {
				v.set(j,0,vecteurPropre.get(j, ordre[i]));
			}
			//pour avoir les eigenfaces, on prend un vecteur propre
			//de matriceReduite et on le multiplie à gauche par matriceEtude
			//but : remonter à l'espace d'origine 
			//normaliser pour rendre toutes les eigenfaces comparables, pour que la longueur des eigenfaces n'ait pas d'impact : 
			//c'est la direction du vecteur qui compte.
			base[i] = (new Vecteur(matriceEtude.times(v))).normaliser();		
		}
	}
	
	/** 
	 * Returns a column vector of a matrix.
	 * @param i The index of the column.
	 * @param M The matrix from which we want to extract the vector.
	 */
	public Vecteur prendreVecteur(int i, Matrix m) { //prend un vecteur (une colonne) d'une matrice
		int taille= m.getRowDimension();
		Vecteur v = new Vecteur(taille);
		for (int j=0; j<taille; j++) {
			v.setValueP(j, m.get(j, i));
		}
		return v;
	}
	
	
	/**
	 * Projects a vector into base, the eigenfaces basis.
	 * @param V The vector you want to project
	 * @return The projected vector
	 */
	public Vecteur projeter(Vecteur v) {
	    int taille = v.getTaille();
	    Vecteur proj = new Vecteur(base.length); //base.length, pas base[0].getTaille()
	    for (int i = 0; i < base.length; i++) {
	        double a = 0;
	        for (int j = 0; j < taille; j++) {
	            a += base[i].getValue(j) * v.getValue(j);
	        }
	        proj.setValueP(i, a); //coordonnée scalaire seulement
	    }
	    return proj;
	}

	/**
	 * Projects an image vector into base, the eigenfaces basis.
	 * @param V The vector you want to project
	 * @return The projected vector
	 */
	public Vecteur projeterImg(Vecteur v) {
		int n = v.getTaille();
		Vecteur proj = new Vecteur(n, 1); //Créer un vecteur nul de taille n
		for (int i=0; i<base.length; i++) {
			double a=0;
			for (int j=0; j<n; j++) {
				a+=base[i].getValue(j) * v.getValue(j); //Produit scalaire
			}
			for (int j=0; j<n; j++) {
				double b = proj.getValue(j) + a*base[i].getValue(j);
				proj.setValueP(j,  b);
			}
		}
		return proj;
	}
	
	/**
	 * creates matriceProjection projecting all the centered images into the basis of eigenfaces.
	 */
	private void projeterMatrice() {
		matriceProjection = new Matrix(this.base.length, m);
		for (int j=0;j<m;j++) {
			Vecteur V = new Vecteur(base.length);
			V=projeter(prendreVecteur(j, matriceEtude));
			for (int i=0;i<base.length;i++) {
				matriceProjection.set(i,j,V.getValue(i));
			}
		}
	}
	
	
	/**
	 * Compares a new image to an image we have in our database.
	 * @param nouvelleIm The new image we want to compare.
	 * @param imDeReferenceProjetee The reference image we are going to compare nouvelleIm with.
	 * @return The distance between both images calculated thanks to the Euclidean distance.
	 */
	private float comparer(ImageVisage nouvelleIm, Vecteur imDeReferenceProjetee) {
	    Vecteur vecteurIm = nouvelleIm.process();
	    // centrer
	    for (int i = 0; i < n; i++) {
	        vecteurIm.setValueP(i, vecteurIm.getValue(i) - visageM.getValue(i));
	    }
	    vecteurIm = projeter(vecteurIm);
	    // distance euclidienne
	    double distance = 0;
	    for (int i = 0; i < base.length; i++) {
	        double diff = imDeReferenceProjetee.getValue(i) - vecteurIm.getValue(i);
	        distance += diff * diff;
	    }
	    return (float) Math.sqrt(distance);
	}

	//METHODE comparer() D'AVANT
	//	private float comparer(ImageVisage nouvelleIm, Vecteur imDeReferenceProjetee) {
	//		float pourcentage=0;
	//		int nb = imDeReferenceProjetee.getTaille();
	//		Vecteur vecteurIm = nouvelleIm.process(); //traite l'image que l'on veut comparer
	//		vecteurIm = projeter(vecteurIm); //projete l'image que l'on veut comparer
	//		Vecteur compare = new Vecteur(nb);
	//		for (int i=0; i<nb; i++) {
	//			double b= Math.abs(1-(imDeReferenceProjetee.getValue(i) - vecteurIm.getValue(i)/imDeReferenceProjetee.getValue(i))); //compare chaque pixel pour avoir la ressemblance en pourcentage
	//			compare.setValueP(i, b);
	//		}
	//		for (int i=0; i<nb; i++){
	//			pourcentage += compare.getValue(i);
	//		}
	//		pourcentage = pourcentage / nb; //pourcentage global en faisant la moyenne des pourcentages
	//		return pourcentage;
	//	}
	
	/**
	 * Calculates the match percentage between two images based on the distance calculated with comparer().
	 * @param distance The distance calculated with comparer().
	 * @return The match percentage.
	 */
	private float pourcentage(float distance) { //distance est la distance renvoyé par la methode comparer(...)
		float max = 15000;
		float p = 100 * (1-distance/max);
		return p;
	}
	
	/**
	 * Compares a new images to all our reference images.
	 * @param nouvelleIm The new images we want to compare.
	 * @return A table of Retour in which the index of each image in our database is linked to its percentage match with the new image.
	 */
	public Retour[] tableauComparaison(ImageVisage nouvelleIm) {
		Retour[] tableau = new Retour[m];
		//Création du tableau avec pour chque image l'indice et le pourcentage de ressemblance
		for (int i=0; i<m; i++) {
			Retour r = new Retour();
			Vecteur V = prendreVecteur(i, matriceProjection);
			float distance = comparer(nouvelleIm, V);
			float p = pourcentage(distance);
			r.setPourcentage(p);
			r.setIndice(i);
			tableau[i]=r;
		}
		//tri fusion  du tableau par rapport au pourcentage
		triFusion(tableau);
		return tableau;
	}
	
	/**
	 * Sorts a table of Retour based on the percentages, using the merge sort.
	 * @param tab The table we want to sort.
	 */
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
	
	/**
	 * Merges two part of a table into one (this in a step of the merge sort).
	 * @param tab The merged table.
	 * @param gauche The left part of the table.
	 * @param droite The right part of the table.
	 */
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
	
	/**
	 * 
	 * @param tableau
	 * @param m
	 * @return
	 */
	public Vecteur identifier(Retour[] tableau, Matrix m) {
		if (tableau[0].getPourcentage()>=seuil) {
			Vecteur v = prendreVecteur(tableau[0].getIndice(), m);
			return (v);
		}
		return null;
	}
	
	
	//main function
//	public static void main(String[] args) {
//		Matrix M = new Matrix(new double[][] {
//			{4, 2, 6, 7}, 
//			{1, 3, 3, 1},
//			{2, 2, 4, 7},
//			{4, 7, 1, 1}
//		});
//		ACP acp=new ACP(M);
//		double[] vap = acp.valeursPropres();
//		//Vecteur[] vep = acp.creerBase();
//		System.out.println(Arrays.toString(vap));
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
//	}
}
