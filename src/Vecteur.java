/** Classe vecteur*/
public class Vecteur {
    private double[] p; 
    
    /** Constructeur pour créer un vecteur avec des valeurs */
    public Vecteur(double[] p) {
        this.p = p;
    }
    
    /** Constructeur pour créer un vecteur  de taille n vide */
    public Vecteur(int n) {
        this.p = new double[n];
    }
    
    /** Méthode faites pour passer d'un vecteur à la forme matricielle de l'image */
    public Matrice cheminInverse(int nbLignes, int nbColonnes) {
        Matrice result = new Matrice(nbLignes, nbColonnes);
        for (int i = 0; i < nbLignes; i++) {
            for (int j = 0; j < nbColonnes; j++) {
                result.setValeur(i, j, this.p[i * nbColonnes + j]);
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
}