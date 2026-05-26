package laBdd;

public class Personne {
	String nom;
	String prenom;
	
	public Personne(String chemin) {
		int indice1=0;
		int indice2;
		//on parcourt le nom de l'image jusqu'au premier '_' on recupere l'indice1 
		while(indice1<chemin.length()&& chemin.charAt(indice1)!='_') {
			indice1++;
		}
		//on initialise l'indice 2 juste après le premier '_' 
		indice2=indice1+1;
		//on parcourt le nom de l'image du premier '_' jusqu'au deuxieme '_' et on recupere l'indice1 
		while(indice2<chemin.length()&& chemin.charAt(indice2)!='_') {
			indice2++;
		}
		//on initialise le nom et prenom à l'aide des indices du debut et de fin de chaque chaine de caracteres "prenom_nom_" 
		this.prenom=chemin.substring(0, indice1);
		this.nom=chemin.substring(indice1+1, indice2);
	}
	public String getNom() {
		return this.nom;
	}
	public String getPrenom() {
		return this.prenom;
	}
	@Override 
	public boolean equals(Object obj) {
		if(obj instanceof Personne) {
			Personne p = (Personne ) obj;
			if(this.getNom()==p.getPrenom()) {
				return true;
			}
		}
		return false;
	}
	public static void deposerImage(String chemin) {
		Image im1;
		im1.chemin=chemin; 
		im1.id= 01;
	}
}
