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

		/**Test ACP*/
        BDD bdd = new BDD();
        ImageVisage im = new ImageVisage(97, "dataset/reference/Zendaya_Coleman_4.jpg"); 
        //ACP acp = new ACP(bdd.);
        int largeur = 100;
        int hauteur = 100;

        //Vecteur vecteur = acp.visageM;
        
        Vecteur vecteur = new Vecteur(largeur*hauteur);
        
        for(int i=0;i<vecteur.getTaille();i++){
            vecteur.setValueP(i, i % 256);
        }
        
        vecteur = im.process();

        BufferedImage img =
                new BufferedImage(
                        largeur,
                        hauteur,
                        BufferedImage.TYPE_BYTE_GRAY);

        for(int y=0;y<hauteur;y++){

            for(int x=0;x<largeur;x++){

                int indice =
                        y * largeur + x;

                int valeur =
                        (int)vecteur.getValue(indice);

                valeur =
                        Math.max(
                                0,
                                Math.min(
                                        255,
                                        valeur));

                img.getRaster().setSample(
                        x,
                        y,
                        0,
                        valeur);
            }
        }

        JFrame frame = new JFrame();

        frame.pack();
        
        frame.setSize(500,500);

        frame.add(new JLabel(new ImageIcon(img)));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }
    
}
