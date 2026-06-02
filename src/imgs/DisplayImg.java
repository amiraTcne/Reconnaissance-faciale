package imgs;

import projet.Vecteur;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

import Jama.Matrix;

public class DisplayImg{
    public static void affImg(Vecteur imgVect,int h,int w){
        int indice;
        int valeur;
        BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
        for(int y=0;y<h;y++){
            for(int x=0;x<w;x++){
                indice = x * h + y;
                valeur = (int)imgVect.getValue(indice);
                img.getRaster().setSample(x,y,0,valeur);
            }
        }
        JFrame frame = new JFrame();
        frame.pack();
        frame.setSize(500,500);
        frame.add(new JLabel(new ImageIcon(img)));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    public static void affImg(Matrix imgMat){
        Vecteur imgVect = new Vecteur(imgMat);
        affImg(imgVect,imgMat.getRowDimension(),imgMat.getColumnDimension());
    }
    public static void affImgP(ImageVisage img){
        Matrix imgMat = img.processed();
        affImg(imgMat);
    }
    public static void affImgO(ImageVisage img){
        try{
            File imgFile = new File(img.getPath());
            BufferedImage bimg = ImageIO.read(imgFile);
            JFrame frame = new JFrame();
            frame.pack();
            frame.setSize(500,500);
            frame.add(new JLabel(new ImageIcon(bimg)));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
