package laBdd;

import imgs.ImageVisage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import calculs.Vecteur;
import Jama.Matrix;

/**
 * This class and its methods handle everything needed to store the ImageVisage and Personne used by the rest of the program in such a way that we do not lose track of them.
 * @author Meddour Sylia
 * @author Pernet Gabriel
 * @version 1.0
 */
public class Bdd {
	/** This is the Map used to store the different Personne and the 5 ImageVisage linked to them */
	public Map<Personne, ArrayList<ImageVisage>> imagesBdd = new HashMap<>();
	/** This is how many images are in this Bdd */
	private int nbimgs;
	/**
	 * Construcs a new Bdd.
	 * The program will browse the dataset and create a Personne for each person present in the dataset and link to each Personne the 5 images in the dataset that are of this person, creating an ImageVisage for each image.
	 * A total of 100 ImageVisage will be crated, id 1 to 100.
	 */
	public Bdd() {
		String dir = "dataset/reference";
		int id = 1;
		File[] files = (new File(dir)).listFiles();
		// browses through the directory that contains all the images, f being an image File
		for(File f : files) {
			// Creates a Personne for f
			Personne p = new Personne(f.getName());
			// Verifies if this Personne is already present in imagesBdd
			if((imagesBdd.containsKey(p))==false) {
				// and if not, an empty List of ImageVisage is created and linked to the Personne.
				ArrayList<ImageVisage> listeImages = new ArrayList<ImageVisage>(); 
				imagesBdd.put(p, listeImages);
			}
			ImageVisage im = new ImageVisage(id, f.getPath()); // an ImageVisage for f is created.
			imagesBdd.get(p).add(im); // the ImageVisage im is added to the List of ImageVisage of the corresponding Personne of imagesBdd.
			id++; // id is incremented for the creation of the next ImageVisage that will be added to the Bdd.
		}
		this.nbimgs = id-1;
	}
	
    /**
     * Returns the nbimgs of this Bdd
     * @return the nbimgs of this Bdd
     */
	public int getNbimgs(){
		return this.nbimgs;
	}

	/**
	 * This method's aim is to identify which Personne is linked to a certain ImageVisage.
	 * This method takes an int (idimage) as input and searches this to find an ImageVisage whose id is idimage.
	 * If a match is found, the method can return the Personne for which the match has been found.
	 * @param idimage the id of the image for which we want to know the Personne it represents
	 * @return the corresponding Personne or null if idimage is not the id of an ImageVisage present in this
	 */
	public Personne rechercher(int idimage) {
		Personne laPersonne = null;
		for(Personne p : imagesBdd.keySet()) {
			for(ImageVisage img : imagesBdd.get(p)) {
				if(img.id==idimage) {
					laPersonne=p;
				}
			}
		}
		return laPersonne;
	}

	/**
	 * This method's aim is to get an ImageVisage from this by using its id.
	 * This method takes an int (idimage) as input and searches this to find an ImageVisage whose id is idimage.
	 * If a match is found, the method can return the ImageVisage for which the match has been found.
	 * @param idimage the id of the ImageVisage we want to have
	 * @return the ImageVisage or null if idimage is not the id of an ImageVisage present in this
	 */
	public ImageVisage getImg(int idimage) {
		ImageVisage imgn = null;
		for(Personne p : imagesBdd.keySet()) {
			for(ImageVisage img : imagesBdd.get(p)) {
				if(img.id==idimage) {
					return img;
				}
			}
		}
		return imgn;
	}

	/**
	 * This method creates the Matrix used by ACP from the ImageVisage stored in this.
	 * @return the Matrix
	 */
	public Matrix createA(){
		ImageVisage img;
		Matrix imgMat;
		Vecteur imgVect;
		Matrix a = new Matrix(10000,this.getNbimgs());
		for(int i=0;i<this.getNbimgs();i++){
			img = this.getImg(i+1);
			imgMat = img.processed();
			imgVect = new Vecteur(imgMat);
			for(int j=0;j<10000;j++){
				a.set(j,i,imgVect.getValue(j));
			}
		}
		return a;
	}
}
