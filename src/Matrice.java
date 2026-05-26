public class Matrice {
    private Vecteur[] colonnes; // liste de Vecteurs (.i.e. une matrice), chaque vecteur est une colonne
    
    
    /** Retourne le vecteur ( la colonne) à l'indice j */
    public Matrice(int nbLignes, int nbColonnes) {
        this.colonnes = new Vecteur[nbColonnes];
        for (int j = 0; j < nbColonnes; j++) {
            this.colonnes[j] = new Vecteur(nbLignes);
        }
    }
    
    /** Getter pour obtenir le vecteur ( la colonne) à l'indice j */
    public Vecteur getColonne(int j) { return colonnes[j]; }

    /** Setter pour le vecteur (la colonne) à l'indice j */
    public void setColonne(int j, Vecteur v) { colonnes[j] = v; }
    
    /** Setter le nombre de colonnes dans la matrice */
    public int getNbColonnes() { return colonnes.length; }
    
    /** Getter le nombre de lignes dans la matrice*/
    public int getNbLignes() { return colonnes[0].getTaille(); }

    /** Setter pour la valeur à la ligne i, colonne j */
    public double getValeur(int i, int j) {
        return colonnes[j].getP()[i];
    }

    /** Setter pour la valeur à la ligne i, colonne j */
    public void setValeur(int i, int j, double val) {
        colonnes[j].getP()[i] = val;
    }
    
    /** Methode pour faire la transposee d'une matrice */
    public Matrice transposee() {
        Matrice result = new Matrice(getNbColonnes(), getNbLignes());
        for (int i = 0; i < getNbLignes(); i++) {
            for (int j = 0; j < getNbColonnes(); j++) {
                result.setValeur(j, i, this.getValeur(i, j));
            }
        }
		return result;
    	
    }
}