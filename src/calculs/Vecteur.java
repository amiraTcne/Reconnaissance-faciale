package calculs;

import Jama.Matrix;

/**
 * Represents a mathematical vector, internally stored as a 1D array of doubles.
 * Used in the eigenfaces algorithm to represent flattened images
 * (a 100x100 image becomes a vector of size 10000) and intermediate computation results.
 * @author Ouerghi Hedy
 * @version 0.5
 */
public class Vecteur {
    private double[] p;

    /**
     * Constructs a new Vecteur by flattening a Jama Matrix (row by row)
     * @param m the Matrix to flatten
     */
    public Vecteur(Matrix m) {
        int nbLignes = m.getRowDimension();
        int nbColonnes = m.getColumnDimension();
        this.p = new double[nbLignes * nbColonnes];
        for (int i = 0; i < nbLignes; i++) {
            for (int j = 0; j < nbColonnes; j++) {
                this.p[i * nbColonnes + j] = m.get(i, j);
            }
        }
    }

    /**
     * Constructs a new Vecteur from a given array of values.
     * @param p the array of values
    */
    public Vecteur(double[] p) {
        this.p = p;
    }

    /**
     * Constructs a new empty Vecteur of size n (all zeros by default).
     * @param n the size for the new Vecteur
     */
    public Vecteur(int n) {
        this.p = new double[n];
    }

    /**
     * Returns a string representation of the Vecteur, a comma-separated list of values enclosed in brackets.
     * @return a string representation of the Vecteur
     */
    @Override
    public String toString() {
        String s = new String();
        for (int i = 0; i < this.getTaille() - 1; i++) {
            s = s + String.valueOf(this.getValue(i)) + ",";
        }
        s += String.valueOf(this.getValue(this.getTaille() - 1));
        return "[" + s + "]";
    }

    /**
     * Reshapes this Vecteur back into a 2D JAMA matrix (row by row).
     * Used to convert a flattened vector back into its image Matrix form.
     * @param nbLignes   number of rows in the resulting Matrix
     * @param nbColonnes number of columns in the resulting Matrix
     * @return a Matrix of size nbLignes x nbColonnes containing the values of this Vecteur
     */
    public Matrix cheminInverse(int nbLignes, int nbColonnes) {
        Matrix result = new Matrix(nbLignes, nbColonnes);
        for (int i = 0; i < nbLignes; i++) {
            for (int j = 0; j < nbColonnes; j++) {
                result.set(i, j, this.getValue(i * nbColonnes + j));
            }
        }
        return result;
    }
    
    /**
     * Normalizes a Vecteur.
     * @return The normalized Vecteur.
     */
    public Vecteur normaliser() {
    	double s =0;
    	for (int i=0;i<p.length;i++) {
    		s+=p[i]*p[i];
    	}
    	double normeEuclidienne = Math.sqrt(s);
    	Vecteur vn = new Vecteur(p.length);
    	for (int i=0;i<p.length;i++) {
    		vn.setValueP(i, (p[i]/normeEuclidienne));;
    	}
    	return vn;
    }

    /**
     * Returns p, the array of values forming this Vecteur
     * @return an array containing all the values in this Vecteur
     */
    public double[] getP() { return p; }

    /**
     * Sets the array of values of the Vecteur as the one passed in parameter
     * @param p the new array of values for this Vecteur
     */
    public void setP(double[] p) { this.p = p; }

    /**
     * Returns the size of this Vecteur (number of values)
     * @return the size of this Vecteur
     */
    public int getTaille() { return p.length; }

    /**
     * Sets a single value at a given index.
     * @param indice index of the value to set
     * @param valeur the new value
     */
    public void setValueP(int indice, double valeur) {
        p[indice] = valeur;
    }

    /**
     * Returns the value stored at a given index.
     * @param indice index of the value to retrieve
     * @return the value stored at the given index
     */
    public double getValue(int indice) {
        return p[indice];
    }
}
