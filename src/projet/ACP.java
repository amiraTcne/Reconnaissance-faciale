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
	
	
	//constructors
	
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
		this.seuil = (float) 0.8;
	}
	
	
	/** 
	 * Calculates the average face using matriceInitial.
	 * We don't divide by the number of images because a scalar does not change a vector.
	 */
	private void visageMoyen() {
		for (int i=0; i<n; i++) {
			int moyenne=0;
			for (int j=0; j<m; j++) {
				moyenne =(int) (moyenne + matriceInitiale.get(i, j)); //moyenne de chaque pixel
			}
			visageM.setValueP(i, moyenne/m); //ajoute la valeur dans le vecteur du visage moyen
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public Vecteur getVisageM() {
		return visageM;
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
		int n = valeurs.length;
		double[] valeursTriees = new double[n];
		for (int i = 0; i < n; i++) {
            valeursTriees[i] = valeurs[n - 1 - i];
        }
		return valeursTriees;
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
	 * Sorts 
	 * @return
	 */
	//utile pour savoir quels sont les indices des vecteurs propres que nous devons garder
	//trie les indices par ordre décroissant des valeurs propres
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
	 * 
	 * @return
	 */
	private Vecteur[] eigenfaces() {
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
	 * 
	 */
	private void creerBase() {
		EigenvalueDecomposition eig = matriceReduite.eig();
		Matrix vecteurPropre = eig.getV(); //prend les vecteurs propres avec la méthode de Jama
		int m = matriceReduite.getRowDimension();
		int nbComposantes = 2; //nombre de composantes principales que l'on garde, non défini précisément pour l'instant
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
	private Vecteur prendreVecteur(int i, Matrix M) { //prend un vecteur (une colonne) d'une matrice
		n= M.getRowDimension();
		Vecteur V = new Vecteur(n);
		for (int j=0; j<n; j++) {
			V.setValueP(j, M.get(j, i));
		}
		return V;
	}
	
	
	/**
	 * Projects a vector into base, the eigenfaces basis.
	 * @param V The vector you want to project
	 * @return The projected vector
	 */
	private Vecteur projeter(Vecteur V) {
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
	
	
	/**
	 * creates matriceProjection
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
	 * 
	 * @param nouvelleIm
	 * @param imDeReferenceProjetee
	 * @return
	 */
	private float comparer(ImageVisage nouvelleIm, Vecteur imDeReferenceProjetee) {
		float pourcentage=0;
		int nb = imDeReferenceProjetee.getTaille();
		Vecteur vecteurIm = nouvelleIm.process(); //traite l'image que l'on veut comparer
		vecteurIm = projeter(vecteurIm); //projete l'image que l'on veut comparer
		Vecteur compare = new Vecteur(nb);
		for (int i=0; i<nb; i++) {
			double b= Math.abs(1-(imDeReferenceProjetee.getValue(i) - vecteurIm.getValue(i)/imDeReferenceProjetee.getValue(i))); //compare chaque pixel pour avoir la ressemblance en pourcentage
			compare.setValueP(i, b);
		}
		for (int i=0; i<nb; i++){
			pourcentage += compare.getValue(i);
		}
		pourcentage = pourcentage / nb; //pourcentage global en faisant la moyenne des pourcentages
		return pourcentage;
	}
	
	
	/**
	 * 
	 * @param nouvelleIm
	 * @return
	 */
	public Retour[] tableauComparaison(ImageVisage nouvelleIm) {
		Retour[] tableau = new Retour[m];
		//Création du tableau avec pour chque image l'indice et le pourcentage de ressemblance
		for (int i=0; i<m; i++) {
			Retour r = new Retour();
			Vecteur V = prendreVecteur(i, matriceProjection);
			float p = comparer(nouvelleIm, V);
			r.setPourcentage(p);
			r.setIndice(i);
			tableau[i]=r;
		}
		
		//tri fusion  du tableau par rapport au pourcentage
		triFusion(tableau);
		
		return tableau;
	}
	
	/**
	 * 
	 * @param tab
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
	 * 
	 * @param tab
	 * @param gauche
	 * @param droite
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
	 * @param M
	 * @return
	 */
	public Vecteur identifier(Retour[] tableau, Matrix M) {
		if (tableau[0].getPourcentage()>=seuil) {
			Vecteur V = prendreVecteur(tableau[0].getIndice(), M);
			return (V);
		}
		return null;
	}
	
	
	//main function
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
