import laBdd.*;
import projet.*;
import imgs.*;

public class Exe{
    public static void main(String[] args){
        Bdd test = new Bdd();
        for(Personne p : test.imagesBdd.keySet()) {
            System.out.println(p.getPrenom());
			for(ImageVisage img : test.imagesBdd.get(p)) {
                System.out.println(img.getId());
                System.out.println(img.getPath());
			}
		}
        Personne p2 = new Personne("dataset/reference/Zac_Efron_4.jpg");
        for(Personne p : test.imagesBdd.keySet()) {
            if(p.equals(p2)){
            System.out.println("test");

            }
		}
        System.out.println(test.rechercher(1).getNom());
    }
}