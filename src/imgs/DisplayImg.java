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

/**
 * The purpose of this class is to provide static methods to display images.
 * @author Pernet Gabriel
 * @version 1.0
 */
public class DisplayImg{
    /**
     * This function takes the vectorial representation of a grayscale image and displays it.
     * @param imgVect the Vecteur representing the image
     * @param h number of rows of the Matrix representing the image
     * @param w number of columns of the Matrix representing the image
     */
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
    /**
     * This function takes the vectorial representation of a grayscale image of dimentions 100x100 and displays it.
     * This can be used on the processed images.
     * @param imgVect the Vecteur representing the image
     */
    public static void affImg(Vecteur imgVect){
        affImg(imgVect,100,100);
    }
    /**
     * This function takes the matrix representation of a grayscale image and displays it.
     * @param imgMat the Matrix representing the image
     */
    public static void affImg(Matrix imgMat){
        Vecteur imgVect = new Vecteur(imgMat);
        affImg(imgVect,imgMat.getRowDimension(),imgMat.getColumnDimension());
    }
    /**
     * This function takes an ImageVisage and displays the processed version of the image linked to it.
     * @param img the ImageVisage linked to the image to display in its processed form
     */
    public static void affImgP(ImageVisage img){
        Matrix imgMat = img.processed();
        affImg(imgMat);
    }
    /**
     * This function takes an ImageVisage and displays the image linked to it.
     * @param img the ImageVisage linked to the image to display
     */
    public static void affImgO(ImageVisage img){
        try{
            File imgFile = new File(img.getPath());
            BufferedImage bimg = ImageIO.read(imgFile);
            JFrame frame = new JFrame();
            frame.pack();
            frame.setSize(750,750);
            frame.add(new JLabel(new ImageIcon(bimg)));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
