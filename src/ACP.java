package facialRecognition;

import java.util.Arrays;
import java.util.ArrayList;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class ACP {
	private ArrayList <Vecteur> base = new ArrayList <Vecteur>();
	private float seuil;
	private int n;
	private int m;
	private Matrix matriceInitiale = new Matrix(n,m); //celle avec toutes les images
	private Vecteur visageM = new Vecteur(n); //moyenne des pixels de toutes les images
	private Matrix matriceEtude = new Matrix(n,m); //matriceInitiale à laquelle on a soustrait visageM à chacune des colonnes
	private Matrix matriceReduite = new Matrix(m,m); //AT x A 
	private Matrix matriceProjection;
	private Retour r = new Retour();
	
	public ACP(Matrix matriceInitiale) {
		this.matriceInitiale = matriceInitiale; 
		this.n = matriceInitiale.getRowDimension();
		this.m = matriceInitiale.getColumnDimension();
		//this.visageM = visageMoyen();
		visageMoyen();
		soustraire();
		multiplication();
		valeursPropres(matriceReduite);
		this.seuil = 80;
	}
	
	
	private void visageMoyen() {
		//Vecteur v = new Vecteur(n);
		for (int i=0; i<m; i++) {
			int moyenne=0;
			for (int j=0; j<n; j++) {
				moyenne = (int) (moyenne + matriceInitiale.get(i,j));
			}
			//v.setValueP(i, moyenne);
			visageM.setValueP(i,moyenne);
		}
		//return v;
	}
	
	private void soustraire() {
		for (int i=0; i<n; i++) {
			for (int j=0; j<m; j++) {
				matriceEtude.set(i, j, matriceInitiale.get(i,j) - visageM.getValue(i));
			}
		}
	}
	
	private void multiplication() { //AT x A 
		Matrix matriceTranspose = matriceEtude.transpose();
		matriceReduite = matriceTranspose.times(matriceEtude);
//		for (int i=0; i<matriceEtude.size(); i++) {
//			for (int j=0; j<matriceEtude.size(); j++) {
//				int somme=0;
//				for (int k=0; k<matriceEtude[0].size(); k++) {
//					somme = somme + matriceEtude[i][k]*matriceTranspose[k][j];
//				}
//				m[i][j] = somme;
//			}
//		}
	}
	
	private static double[] valeursPropres(Matrix M) {
		EigenvalueDecomposition eig = M.eig();
		double[] valeurs = eig.getRealEigenvalues();
		return valeurs;
	}
	
	
	public static void main(String[] args) {
		
		Matrix M = new Matrix(2,2,1);
		System.out.println(Arrays.toString(valeursPropres(M)));
		
	}
}