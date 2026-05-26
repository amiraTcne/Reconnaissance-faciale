package projet;

import java.util.ArrayList;
import Jama.Matrix;

public class ACP {
	private ArrayList <Vecteur> base = new ArrayList <Vecteur>();
	private float seuil;
	private Matrix matriceInitiale;
	private Vecteur visageM;
	private Matrix matriceEtude;
	private Matrix matriceReduite;
	private Matrix matriceProjection;
	private Retour r = new Retour();
	
	public ACP(Matrix matriceInitiale, int n, int m) {
		this.matriceInitiale = matriceInitiale; //de taille n*m
		this.matriceEtude = new Matrix(n, m); //remplie de 0 de taille n*m
		this.matriceReduite = new Matrix(m, m); //remplie de 0 de taille m*m
		this.seuil = 80;
	}
	
	
	private void visageMoyen() {
		int n = matriceInitiale.getRowDimension();
	    int m = matriceInitiale.getColumnDimension();
		for (int i=0; i<m; i++) {
			int moyenne=0;
			for (int j=0; j<n; j++) {
				moyenne = moyenne + matriceInitiale.get(j,  i);
			}
			visageM.add(moyenne);
		}
	}
	
	private void soustraire() {
		for (int i=0; i<matriceInitiale.size(); i++) {
			for (int j=0; j<visageM.size(); j++) {
				matriceEtude[i][j] = marticeInitiale[i][j] - visageM[j];
			}
		}
	}
	
	private void multiplication() { //AT x A 
		Matrcie matriceTranspose = matriceEtude.transpose();
		for (int i=0; i<matriceEtude.size(); i++) {
			for (int j=0; j<matriceEtude.size(); j++) {
				int somme=0;
				for (int k=0; k<matriceEtude[0].size(); k++) {
					somme = somme + matriceEtude[i][k]*matriceTranspose[k][j];
				}
				matriceReduite[i][j] = somme;
			}
		}
	}
	
	private void valeurPropres() {
		
	}
	public static void main(String[] args) {
		
	}
}
