package laBdd;

import imgs.ImageVisage;

/**
 * This class is made to represent a person that is present in the dataset.
 * It is defined by its surname (nom) and its name (prenom).
 * @author Meddour Sylia
 * @version 1.0
 */
public class Personne {
	/** The surname of the Personne */
	String nom;
	/** The name of the Personne */
	String prenom;
	
	/**
	 * Constructs a new Personne.
	 * The program will use the name of a file to create a Personne, extracting its nom and prenom from the name of the file.
	 * @param chemin the name of the file. Follows of format of "prenom_nom_X" (where X is a number)
	 */
	public Personne(String chemin) {
		int indice1=0;
		int indice2;
		// we read through chemin until the first '_', incrementing indice1 in the process
		while(indice1<chemin.length() && chemin.charAt(indice1)!='_') {
			indice1++;
		}
		// Initialization of indice2 right after the first '_' 
		indice2=indice1+1;
		// we read through chemin starting from after the first '_' until we reach the second '_', incrementing indice2 in the process
		while(indice2<chemin.length()&& chemin.charAt(indice2)!='_') {
			indice2++;
		}
		// we use indice1 and indice2 to extract 'nom' and 'prenom' from "prenom_nom_X" (X being a number)
		this.prenom=chemin.substring(0, indice1);
		this.nom=chemin.substring(indice1+1, indice2);
	}
    /**
     * Returns the nom of this Personne
     * @return the nom of this Personne
     */
	public String getNom() {
		return this.nom;
	}
    /**
     * Returns the prenom of this Personne
     * @return the prenom of this Personne
     */
	public String getPrenom() {
		return this.prenom;
	}
    /**
     * Returns a hash code for this Personne
     * @return a hash code for this Personne, computed as the sum of the hash code of its nom attribute and the hash code of its prenom attribute.
     */
	@Override 
	public int hashCode(){
		return ((this.getNom()).hashCode() + (this.getPrenom()).hashCode());
	}
    /**
     * Compares this Personne to the specified object. The result is true if and only if the argument is not null and is a Personne object whose nom and prenom attributes are the same as this object.
     * @return true if the given object represents a Personne equivalent to this Personne, false in all other cases
     */
	public boolean equals(Object obj) {
		if(obj instanceof Personne) {
			Personne p = (Personne) obj;
			if((this.getNom()).equals(p.getNom()) && (this.getPrenom()).equals(p.getPrenom())) {
				return true;
			}
		}
		return false;
	}
	public static void deposerImage(String chemin) {
		ImageVisage im1 = null;
		im1.path=chemin; 
		im1.id=0;
	}
}
