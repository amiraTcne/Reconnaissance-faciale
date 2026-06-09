package laBdd;

import imgs.ImageVisage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import projet.Vecteur;
import Jama.Matrix;

/**
 * This class and its methods handle everything needed to store the <code>ImageVisage</code> and <code>Personne</code> used by the rest of the program
 * in such a way that we do not lose track of them.
 * @author Meddour Sylia
 * @version 1.0
 */
public class Bdd {
	/** This is the <code>Map</code> used to store the different <code>Personne</code> and the 5 <code>ImageVisage</code> linked to them */
	public Map<Personne, ArrayList<ImageVisage>> imagesBdd = new HashMap<>();
	 
	/**
	 * Construcs a new Bdd.
	 * The program will browse the dataset and create a <code>Personne</code> for each person present in the dataset and
	 * link to each <code>Personne</code> the 5 images in the dataset that are of this person, creating an <code>ImageVisage</code> for each image.
	 * A total of 100 <code>ImageVisage</code> will be crated, id 1 to 100.
	 */
	public Bdd() {
		String dir = "dataset/reference";
		int id = 1;
		File[] files = (new File(dir)).listFiles();
		// browses through the directory that contains all the images, f being an image <code>File</code>
		for(File f : files) {
			// Creates a <code>Personne</code> for f
			Personne p = new Personne(f.getName());
			// Verifies if this <code>Personne</code> is already present in <code>imagesBdd</code>.
			if((imagesBdd.containsKey(p))==false) {
				// and if not, an empty <code>List</code> of <code>ImageVisage</code> is created and linked to the <code>Personne</code>.
				ArrayList<ImageVisage> listeImages = new ArrayList<ImageVisage>(); 
				imagesBdd.put(p, listeImages);
			}
			ImageVisage im = new ImageVisage(id, f.getPath()); // an <code>ImageVisage</code> for f is created.
			imagesBdd.get(p).add(im); // the <code>ImageVisage</code> im is added to the <code>List</code> of <code>ImageVisage</code> of the corresponding <code>Personne</code> of <code>imagesBdd</code>.
			id++; // id is incremented for the creation of the next <code>ImageVisage</code> that will be added to the <code>Bdd</code>.
		}
	}
	
	/**
	 * This method's aim is to identify which <code>Personne</code> is linked to a certain <code>ImageVisage</code>.
	 * This method takes an int (<code>idimage</code>) as input and searches <code>this</code> to find an <code>ImageVisage</code> whose id is <code>idimage</code>.
	 * If a match is found, the method can return the <code>Personne</code> for which the match has been found.
	 * @param idimage the id of the image for which we want to know the <code>Personne</code> it represents
	 * @return the corresponding <code>Personne</code> or null if <code>idimage</code> is not the id of an <code>ImageVisage</code> present in <code>this</code>
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
	 * This method's aim is to get an <code>ImageVisage</code> from <code>this</code> by using its id.
	 * This method takes an int (<code>idimage</code>) as input and searches <code>this</code> to find an <code>ImageVisage</code> whose id is <code>idimage</code>.
	 * If a match is found, the method can return the <code>ImageVisage</code> for which the match has been found.
	 * @param idimage the id of the <code>ImageVisage</code> we want to have
	 * @return the <code>ImageVisage</code> or null if <code>idimage</code> is not the id of an <code>ImageVisage</code> present in <code>this</code>
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
	 * This method creates the <code>Matrix</code> used by <code>ACP</code> from the <code>ImageVisage</code> stored in <code>this</code>.
	 * @return the <code>Matrix</code>
	 */
	public Matrix createA(){
		ImageVisage img;
		Matrix imgMat;
		Vecteur imgVect;
		Matrix a = new Matrix(10000,100);
		for(int i=0;i<100;i++){
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
