import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @author Pernet Gabriel
 * @version 0.5
 * The purpose of this class is to prepare the dataset to be used by the rest of the program.
 * There is a folder named dataset that contains multiple images in different subdirectories.
 * The main function of this class will fill the dataReady folder so that it mirrors the dataset folder but alter the images in the process.
 * All the images in the dataReady folder will have dimensions of 100x100 and be in grayscale.
 */
public class Prep{
    /**
     * This method processes a <code>File</code> : <code>imgFile</code>.
     * If <code>imgFile</code> is a directory, the method creates the corresponding sub-directory in the dataReady folder (if it doesn't already exists).
     * It then calls itself for each <code>File</code> in <code>imgFile</code>.
     * If <code>imgFile</code> is an image, the method :
     *      1. converts the image in 100x100 format
     *      2. creates a clone of the image in grayscale
     *      3. saves the grayscale image in the dataReady folder so that its path mirrors the one of the original image in dataset
     * @param imgFile   a <code>File</code> that is either an image or a directory.
     *                  If it is a directory, it either contains images or other directories.
     */
    public static void iteration(File imgFile){
        // if imgFile is an image (because all non directory Files are images)
        if (imgFile.isFile()){
            BufferedImage img;
            try{
                img = ImageIO.read(imgFile);
                // creates a 100x100 pixels version of the original image
                Image temp = img.getScaledInstance(100,100,Image.SCALE_DEFAULT);
                // Creates a new BufferedImage mirroring the resized version of the original
                BufferedImage newImg = new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
                Graphics2D g = newImg.createGraphics();
                g.drawImage(temp,0,0,null);
                g.dispose();
                int w = newImg.getWidth(); //width
                int h = newImg.getHeight(); //height
                // Every pixel is replaced by its grayscale equivalent
                for(int i=0;i<w;i++){
                    for(int j=0;j<h;j++){
                        Color c = new Color(newImg.getRGB(i,j));
                        int gray = (int)(0.299*c.getRed() + 0.587*c.getGreen() + 0.114*c.getBlue());
                        newImg.setRGB(i,j,(new Color(gray,gray,gray)).getRGB());
                    }
                }
                // save the new processed image
                if((imgFile.toPath()).getNameCount()>1){
                    ImageIO.write(newImg,"jpg",new File("dataReady/"+(imgFile.toPath()).subpath(1,(imgFile.toPath()).getNameCount())));
                }else{
                    ImageIO.write(newImg,"jpg",new File("dataReady/"+(imgFile.toPath()).getFileName()));
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        // if imgFile is a directory (because it is not an image)
        }else{
            // We want to process all files in the directory so here, we list them all
            File[] files = (new File((imgFile.getPath()))).listFiles();
            // creates directories if needed so that dataReady mirrors dataset
            try{
                if((imgFile.toPath()).getNameCount()>1){
                    Files.createDirectories(Paths.get("dataReady/"+(imgFile.toPath()).subpath(1,(imgFile.toPath()).getNameCount())));
                }else{
                    Files.createDirectories(Paths.get("dataReady/"+(imgFile.toPath()).getFileName()));
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            // calls itself recursively on each file in the directory
            for(File imgFile2 : files){
                iteration(imgFile2);
            }
        }
    }
    public static void main(String[] args){
        String dir = "dataset"; // this is the path of the directory that contains the images
        // We want to process everything contained in dataset so we list it all here
        File[] files =  (new File(dir)).listFiles();
        // We call iteration() on every file/directory in dataset, iteration handles the rest
        for(File imgFile : files){
            iteration(imgFile);
        }
    }
}