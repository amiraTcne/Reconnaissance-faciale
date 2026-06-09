package imgs;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.Color;
import Jama.Matrix;
import projet.Vecteur;

/**
 * This object is used to store a path leading to an image and the position of the image in the matrix used for recognition.
 * The path is the path to the original image and not the processed one, as we want to be able to access the original image quickly when we need to display it during the comparisons. We have no need to store the path of the modified images since, for the ones present in the dataset, we just need to access them once to do all the necessary calculations and they are then stored in their vectorial form, and, for the ones the user provides, we don't need to keep the processed version (so it is never saved, just used).
 * @author Pernet Gabriel
 * @version 0.2
 */
public class ImageVisage{
    /** id is the index of the vector representing the image in all the Matrix objects that will contain all images or 0 if the image is not in the Matrix. */
    public int id;
    /** path is the path, which allows us to retrieve the original image at any point */
    public String path;
    /**
     * Constructs a new ImageVisage.
     * @param id the id for the new ImageVisage. Will be 0 if the image is the one chosen by the user.
     * @param path the path of the file containing the image.
     */
    public ImageVisage(int id, String path){
        this.id = id;
        this.path = path;
    }
    /**
     * Returns the id of this ImageVisage
     * @return the id of this ImageVisage
     */
    public int getId(){
        return this.id;
    }
    /**
     * Returns the path of this ImageVisage
     * @return the path of this ImageVisage
     */
    public String getPath(){
        return this.path;
    }
    /**
     * Compares this ImageVisage to the specified object. The result is true if and only if the argument is not null and is an ImageVisage object whose path leads to the same image file as this object.
     * @return true if the given object represents an ImageVisage equivalent to this ImageVisage, false in all other cases
     */
    public boolean equals(Object o){
        if(o instanceof ImageVisage){
            if((((ImageVisage) o).path).equals(this.path)){
                return true;
            }
        }
        return false;
    }
    /**
     * This function returns a vectorial representaion of a 100x100 grayscale version of the image it is used on.
     * This is used on images that are not already in the dataset and that we wish to compare to the ones present in the dataset.
     * @return a Vecteur with 10000 values in its p attribute representing the image linked to this ImageVisage in 100x100 grayscale format, 
     */
    public Vecteur process(){
        File imgFile = new File(this.getPath());
        BufferedImage img;
        Vecteur imgVect;
        Matrix imgMat = new Matrix(100,100);
        try{
            img = ImageIO.read(imgFile);
            // creates a 100x100 pixels version of the original image
            Image temp = img.getScaledInstance(100,100,Image.SCALE_DEFAULT);
            // Creates a new BufferedImage mirroring the resized version of the original
            BufferedImage procImg = new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
            Graphics2D g = procImg.createGraphics();
            g.drawImage(temp,0,0,null);
            g.dispose();
            int w = procImg.getWidth(); // width
            int h = procImg.getHeight(); // height
            // set the values of the Matrix to the grayscale values of the corresponding pixels
            for(int i=0;i<w;i++){
                for(int j=0;j<h;j++){
                    Color c = new Color(procImg.getRGB(j,i));
                    int gray = (int)(0.299*c.getRed() + 0.587*c.getGreen() + 0.114*c.getBlue());
                    imgMat.set(j,i,gray);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        imgVect = new Vecteur(imgMat);
        return imgVect;
    }
    /**
     * This function's purpose is to fetch a processed version of the image it is used on without changing the path attribute, as we still need to be able to acces the original image to display it.
     * This is used on images already present in the dataset.
     * @return a 100x100 Matrix representing an image, that image being the 100x100 grayscale version of this ImageVisage.
     */
    public Matrix processed(){
        // takes the path of this ImageVisage and replaces dataset by dataReady (the image fetched will be the processed version)
        String pathPro = "dataReady"+this.getPath().substring(this.getPath().indexOf("/"));
        Matrix imgMat = new Matrix(100,100);
        File imgFile = new File(pathPro);
        BufferedImage img;
        try{
            img = ImageIO.read(imgFile);
            int w = img.getWidth(); // width
            int h = img.getHeight(); // height
            // set the values of the Matrix to the values of the pixels of the image
            for(int i=0;i<w;i++){
                for(int j=0;j<h;j++){
                    Color c = new Color(img.getRGB(j,i));
                    imgMat.set(j,i,c.getRed());
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return imgMat;
    }
}