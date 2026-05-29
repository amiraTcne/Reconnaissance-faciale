package laBdd;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Bdd {
	 public Map <Personne, ArrayList<Images>> imagesBdd = new HashMap<>();
	 private String dir = "dataset/reference"; //dossier qui contient toutes les images 
	 private File[] files =  (new File(dir)).listFiles(); // la liste de mes fichiers images 
	 private int identifiant =1; 
	 
	 /**
	  * on parcourt la liste des images 
	  * pour chaque fichier image on crée une personne pour recuperer le nom et prenom 
	  * ensuite verifier si cette personne existe deja dans la map 
	  * si ce n'est pas le cas on crée une liste vide d'images associée à cette personne 
	  * enfin on crée une image on lui attribue un identifiant et le chemin 
	  * on stocke l'image dans la liste associée à la personne courante 
	  * on incremente l'indentifiant pour le mettre à jour pour la prochaine image 
	  */
	 public Bdd() {
		 for(File f : files) {
			 Personne p = new Personne(f.getName());
			 if(! imagesBdd.containsKey(p)) {
				 ArrayList<Images> listeImages = new ArrayList<Images>(); 
				 imagesBdd.put(p, listeImages);
			 }
			 Images im= new Images(identifiant, f.getPath());
			 imagesBdd.get(p).add(im);
			 identifiant++;
		 
		 }
	 }
	 
	 public Personne rechercher(int idimage) {
		 Personne laPersonne = null;
			for(Personne p : imagesBdd.keySet()) {
				 for(Images img : imagesBdd.get(p)) {
					 if(img.id==idimage) {
						 laPersonne=p;
					 }
				 }
			 }
			return laPersonne;
		 }
}

