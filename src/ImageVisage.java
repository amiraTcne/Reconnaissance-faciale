import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.Color;


/**
 * @author Pernet Gabriel
 * @version 0.1
 */
public class ImageVisage{
    public int id;
    public String path;
    /**
     * Constructor for Imagevisage.
     * @param id the id for the new ImageVisage. Will be 0 if the image is the one chosen by the user.
     * @param path the path of the file containing the image.
     * @version 1.0
     */
    public ImageVisage(int id, String path){
        this.id = id;
        this.path = path;
    }
    /**
     * Proper way to obtain the value of this.id
     * @version 1.0
     * @return value of id attribute
     */
    public int getId(){
        return this.id;
    }
    /**
     * Proper way to obtain the value of this.path
     * @version 1.0
     * @return value of path attribute
     */
    public String getPath(){
        return this.path;
    }
    /**
     * This function returns a vectorial representaion of a 100x100 grayscale version of the image it is used on.
     * This is used on images that are not already in the dataset and that we wish to compare to the ones present in the dataset.
     * @version 0.1
     * @return a <code>Vecteur</code> with 10000 values in its p attribute representing <code>this</code> (an image) in 100x100 grayscale format, 
     */
    public Vecteur process(){
        File imgFile = new File(this.getPath());
        BufferedImage img;
        Vecteur imgVect;
        Matrice imgMat = new Matrice(100,100);
        try{
            img = ImageIO.read(imgFile);
            Image temp = img.getScaledInstance(100,100,Image.SCALE_DEFAULT);
            BufferedImage procImg = new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
            Graphics2D g = procImg.createGraphics();
            g.drawImage(temp,0,0,null);
            g.dispose();
            int w = procImg.getWidth();
            int h = procImg.getHeight();
            for(int i=0;i<w;i++){
                for(int j=0;j<h;j++){
                    Color c = new Color(procImg.getRGB(j,i));
                    int gray = (int)(0.299*c.getRed() + 0.587*c.getGreen() + 0.114*c.getBlue());
                    imgMat.setValeur(j,i,gray);
                }
            }
            imgVect = imgMat.cheminInverse();
            return imgVect;
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * This function purpose is to fetch a processed version of the image it is used on.
     * This is used on images already present in the dataset.
     * @version 0.1
     * @return a 100x100 <code>Matrice</code> representing an image, that image being the 100x100 grayscale version of <code>this</code>.
     */
    public Matrice processed(){
        String pathPro = "dataReady"+this.getPath().substring(this.getPath().indexOf("/"));
        Matrice imgMat = new Matrice(100,100);
        File imgFile = new File(pathPro);
        BufferedImage img;
        try{
            img = ImageIO.read(imgFile);
            int w = img.getWidth();
            int h = img.getHeight();
            for(int i=0;i<w;i++){
                for(int j=0;j<h;j++){
                    Color c = new Color(img.getRGB(j,i));
                    imgMat.setValeur(j,i,c.getRed());
                }
            }
            return imgMat;
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}