
/**
 * @author Pernet Gabriel
 * @version 0.1
 */
public class ImageVisage{
    public int id;
    public String path;
    public ImageVisage(int id, String path){
        this.id = id;
        this.path = path;
    }
    public int getId(){
        return this.id;
    }
    public String getPath(){
        return this.path;
    }
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