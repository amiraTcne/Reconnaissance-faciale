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



public class Prep{
    public static void iteration(File imgFile){
        if (imgFile.isFile()){
            BufferedImage img;
            try{
                img = ImageIO.read(imgFile);
                Image temp = img.getScaledInstance(100,100,Image.SCALE_DEFAULT);
                BufferedImage newImg = new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
                Graphics2D g = newImg.createGraphics();
                g.drawImage(temp,0,0,null);
                g.dispose();
                int w = newImg.getWidth();
                int h = newImg.getHeight();
                for(int i=0;i<w;i++){
                    for(int j=0;j<h;j++){
                        Color c = new Color(newImg.getRGB(i,j));
                        int gray = (int)(0.299*c.getRed() + 0.587*c.getGreen() + 0.114*c.getBlue());
                        newImg.setRGB(i,j,(new Color(gray,gray,gray)).getRGB());
                    }
                }
                if((imgFile.toPath()).getNameCount()>1){
                    ImageIO.write(newImg,"jpg",new File("dataReady/"+(imgFile.toPath()).subpath(1,(imgFile.toPath()).getNameCount())));
                }else{
                    ImageIO.write(newImg,"jpg",new File("dataReady/"+(imgFile.toPath()).getFileName()));
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            File[] files = (new File((imgFile.getPath()))).listFiles();
            try{
            if((imgFile.toPath()).getNameCount()>1){
                Files.createDirectories(Paths.get("dataReady/"+(imgFile.toPath()).subpath(1,(imgFile.toPath()).getNameCount())));
            }else{
                Files.createDirectories(Paths.get("dataReady/"+(imgFile.toPath()).getFileName()));
            }
            }catch (IOException e) {
                e.printStackTrace();
            }
            for(File imgFile2 : files){
                iteration(imgFile2);
            }
        }
    }
    public static void main(String[] args){
        String dir = "dataset";
        File[] files =  (new File(dir)).listFiles();
        for(File imgFile : files){
            iteration(imgFile);
        }
    }
}