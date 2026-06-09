package projet;

import java.util.Arrays;

import java.lang.Math;
import laBdd.Bdd;
import laBdd.Personne;
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
				moyenne = (moyenne + matriceInitiale.get(i, j)); //average of each pixel
			}
			visageM.setValueP(i, moyenne/m); //adds the value in the average face vector
		}
	}
	
	/** 
	 * Subtracts the mean face from each column of matriceInitial.
	 * This gives us the matrix we will be working with : matriceEtude.
	 */
	private void centrer() {
		for (int i=0; i<n; i++) {
			for (int j=0; j<m; j++) {
				matriceEtude.set(i, j, matriceInitiale.get(i, j) - visageM.getValue(i)); //substracts the average face from each face to keep only the differences.
			}
		}
	}
	
	/** 
	 * Calculates matriceEtude transposed multiplied by matriceEtude. 
	 * The goal is to reduce the dimension in order to find the eigenvalues of matriceEtude using as few calculations as possible.
	 */
	private void reduireDimension() { //AT * A
		Matrix matriceTranspose = matriceEtude.transpose(); //uses transpose() method from Jama.
		matriceReduite = matriceTranspose.times(matriceEtude); //uses times() (multiplication) method from Jama.
	}
	
	/** 
	 * Calculates the eigenvalues of matriceReduite.
	 * @return The eigenvalues of matriceReduite.
	 */
	private double[] valeursPropres() { //uses a method from Jama to find the eigenvalues.
		EigenvalueDecomposition eig = matriceReduite.eig();
		return eig.getRealEigenvalues();
	}
	
	/** 
	 * Sorts the eigenvalues of matriceReduit in order to place them in descending order in a graph.
	 * @return Sorted eigenvalues of martriceReduite.
	 */
	public double[] valeursPropresTriees() {
		double[] valeurs = valeursPropres();
		//sorts in ascending order
		Arrays.sort(valeurs);
		//reverse the array to sort it in descending order
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
		// var = sommePartielle/s: as long as it is < 0.9 and we haven't already summed all the eigenvalues, we continue.
		// Theoretically, just the first condition is sufficient because once we have added all the eigenvalues — that is, when 
        // nbComponents equals vap.length — the variance will be equal to 1. We include the second condition just in case, to avoid errors.
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
		//indices contains the original indexes, from 0 to m-1, with m representing the number of eigenvalues.
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
		for (int i=0; i<m; i++) { //converts the eigenvector matrix into an array of Vecteur.
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
		Matrix vecteurPropre = eig.getV(); //calculates the eigenvectors using the Jama method.
		int m = matriceReduite.getRowDimension();
		int nbComposantes = nbCompoVarCumulee(0.9); //number of principal components to keep.
		int[] ordre = indicesValeursPropresTriees();
		base = new Vecteur[nbComposantes];
		for (int i=0; i<nbComposantes; i++) { //converts the eigenvector matrix into an array of Vecteur.
			Matrix v = new Matrix(m,1);
			for (int j=0; j<m; j++) {
				v.set(j,0,vecteurPropre.get(j, ordre[i]));
			}
			//To obtain the eigenfaces, we take an eigenvector
            //of matriceReduite and multiply it on the left by matriceEtude.
			//goal : return to the original space.
            //Normalizing to make all eigenfaces comparable, so that the length of the eigenfaces has no impact :
            //it is the direction of the vector that matters.
			base[i] = (new Vecteur(matriceEtude.times(v))).normaliser();		
		}
	}
	
	/** 
	 * Returns a column vector of a matrix.
	 * @param i The index of the column.
	 * @param M The matrix from which we want to extract the vector.
	 */
	public Vecteur prendreVecteur(int i, Matrix m) { //take a vector (a column) of a matrix.
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
	    Vecteur proj = new Vecteur(base.length); //base.length, not base[0].getTaille()
	    for (int i = 0; i < base.length; i++) {
	        double a = 0;
	        for (int j = 0; j < taille; j++) {
	            a += base[i].getValue(j) * v.getValue(j);
	        }
	        proj.setValueP(i, a); //scalar coordinate only
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
		Vecteur proj = new Vecteur(n); //create a zero vector of size n
		for (int i=0; i<base.length; i++) {
			double a=0;
			for (int j=0; j<n; j++) {
				a+=base[i].getValue(j) * v.getValue(j); //Scalar product
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
	 * @param imDeReferenceProjetee The vector of the reference image we are going to compare nouvelleIm with.
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
	
	/**
	 * Compares the vector of a new image to an image we have in our database.
	 * @param vecteurIm The vector of the new image we want to compare.
	 * @param imDeReferenceProjetee The reference image we are going to compare nouvelleIm with.
	 * @return The distance between the 2 images associated to the 2 vectors, calculated thanks to the Euclidean distance.
	 */
	private float comparer(Vecteur vecteurIm, Vecteur imDeReferenceProjetee) {
	    // centrer
	    for (int i = 0; i < n; i++) {
	        vecteurIm.setValueP(i, vecteurIm.getValue(i) - visageM.getValue(i));
	    }
	    vecteurIm = projeter(vecteurIm);
	    // euclidean distance
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
	private float pourcentage(float distance) { 
		float max = 15000;
		float p = 100 * (1-distance/max);
		return p;
	}

	//have to be commented
	public float pourcentageImage(ImageVisage nouvelleIm, int idImage) {
	    Vecteur ref = prendreVecteur(idImage - 1, matriceProjection);
	    float distance = comparer(nouvelleIm, ref);
	    return pourcentage(distance);
	}
	
	/**
	 * Compares a new images to all our reference images.
	 * @param nouvelleIm The new images we want to compare.
	 * @return A table of Retour in which the index of each image in our database is linked to its percentage match with the new image.
	 */
	public Retour[] tableauComparaison(ImageVisage nouvelleIm) {
		Retour[] tableau = new Retour[m];
		//Create an array containing the index and similarity percentage for each image
		for (int i=0; i<m; i++) {
			Retour r = new Retour();
			Vecteur V = prendreVecteur(i, matriceProjection);
			Vecteur vecteurIm = nouvelleIm.process();
			float distance = comparer(vecteurIm, V);
			float p = pourcentage(distance);
			r.setPourcentage(p);
			r.setIndice(i);
			tableau[i]=r;
		}
		//merge sort of the array based on percentage
		triFusion(tableau);
		return tableau;
	}

	/**
	 * 
	 * @param nouvelleIm
	 * @return
	 */
	//pour chaque personne de la BDD, on veut l'indice de l'image la plus ressemblante associé au pourcentage de correspondance
	public Retour[] uneImageParPersonne(ImageVisage nouvelleIm) {
	    Bdd bdd = new Bdd();
	    Retour[] tableau = new Retour[bdd.imagesBdd.size()];
	    int i = 0;

	    for (Personne p : bdd.imagesBdd.keySet()) {
	        float maxPourcentage = -1;
	        int idMeilleureImage = -1;

	        for (ImageVisage img : bdd.imagesBdd.get(p)) {
	            float pourcentage = pourcentage(comparer(nouvelleIm, prendreVecteur(img.id-1, matriceProjection)));
	            if (pourcentage > maxPourcentage) {
	                maxPourcentage = pourcentage;
	                idMeilleureImage = img.id;
	            }
	        }

	        Retour r = new Retour();
	        r.setPourcentage(maxPourcentage);
	        r.setIndice(idMeilleureImage);
	        tableau[i] = r;
	        i++;
	    }

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
	        //descending order (highest percentage first)
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
	private int seuilStat(Vecteur im) {
		double [] lambda = valeursPropresTriees();
		double Tnew = 0;
		double Talpha;
		double p = im.getTaille();
		int n = 100; //nombre d'images d'apprentissage
		double F = 1.86; //car n=100, K=43 et alpha=0.99
		for (int i=0; i<p; i++) {
			Tnew += (im.getValue(i)*im.getValue(i))/lambda[i];
		}
		Talpha =(p*(n-1))/(n-p) * F;
		if (Tnew < Talpha) {
			return 1;
		}
		return 0;
	}

	
	/**
	 * If there is a match, links the new image to the corresponding one.
	 * @param nouvelleIm The new image we want to compare.
	 * @return The vector associated to the corresponding image, or nothing if there is no match.
	 */
	public Vecteur identifier(ImageVisage nouvelleIm) {
		Retour[] tableau = tableauComparaison(nouvelleIm);
		if (tableau[0].getPourcentage()>=seuil) {
			Vecteur v = prendreVecteur(tableau[0].getIndice(), matriceInitiale);
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
