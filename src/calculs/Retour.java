package calculs;

/**
 * Retour is used to return a % of likeness between faces and the index in the Matrix used in ACP for the face that is from this Matrix.
 * @author Riche Liah
 * @author Tarchoune Amira
 * @version 0.5
 */
public class Retour {
	/** the % of likeness between the faces */
	private float pourcentage;
	/** the index in the Matrix */
	private Integer indice;
	
	/**
	 * Constructor for Retour. Does nothing.
	 */
	public Retour() {}
	
    /**
     * Returns the pourcentage of this Retour
     * @return the pourcentage of this Retour
     */
	public float getPourcentage() {
		return pourcentage;
	}

	/**
	 * Sets the value of the pourcentage of this Retour to the value passed in parameter
	 * @param pourcentage new value for this.pourcentage
	 */
	public void setPourcentage(float pourcentage) {
		this.pourcentage = pourcentage;
	}

    /**
     * Returns the indice of this Retour
     * @return the indice of this Retour
     */
	public Integer getIndice() {
		return indice;
	}

	/**
	 * Sets the value of the indice of this Retour to the value passed in parameter
	 * @param pourcentage new value for this.indice
	 */
	public void setIndice(Integer indice) {
		this.indice = indice;
	}
	
}
