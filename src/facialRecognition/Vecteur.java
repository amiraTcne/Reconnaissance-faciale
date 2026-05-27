package facialRecognition;

import Jama.Matrix;

/** Classe vecteur*/
public class Vecteur {
    private double[] p; 
    /** 
     * Constructeur qui passe une matrice JAMA en Vecteur (ligne par ligne).
     * @param m la matrice à aplatir
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
    /** Constructeur pour créer un vecteur avec des valeurs */
    public Vecteur(double[] p) {
        this.p = p;
    }
    
    /** Constructeur pour créer un vecteur  de taille n vide */
    public Vecteur(int n) {
        this.p = new double[n];
    }
    
    /** Méthode faites pour passer d'un vecteur à la forme matricielle de l'image */
    public static Matrix cheminInverse(Matrix v, int nbLignes, int nbColonnes) {
        Matrix result = new Matrix(nbLignes, nbColonnes);
        for (int i = 0; i < nbLignes; i++) {
            for (int j = 0; j < nbColonnes; j++) {
                result.set(i, j, v.get(i * nbColonnes + j, 0));
            }
        }
        return result;
    }
    
    /** Getter pour obtenir notre vecteur */
    public double[] getP() { return p; }
    
    /** Setter pour changer les valeurs de notre vecteur */
    public void setP(double[] p) { this.p = p; }
    
    /** Getter pour obtenir la taille de notre vecteur */
    public int getTaille() { return p.length; }
    
    /** Setter pour changer une seule valeur de notre vecteur */
    public void setValueP(int indice, int valeur) {
    	p[indice]=valeur;
    }
    /** Getter pour obtenir une des valeurs du vecteur*/
    public double getValue(int indice) {
    	return p[indice];
    }

}
