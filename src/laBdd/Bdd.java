package laBdd;

import imgs.ImageVisage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Meddour Sylia
 * @version 1.0
 */
public class Bdd {
	public Map<Personne, ArrayList<ImageVisage>> imagesBdd = new HashMap<>();
	 
	/**
	 * This is the constructor for Bdd.
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
}