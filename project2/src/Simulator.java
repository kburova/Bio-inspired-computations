import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

public class Simulator {

    final int SpaceSize = 30;
    static private int[][] CA;
    private double H, J1, J2, R1, R2;

    //initialize space 30x30 randomly to 1 or -1 and set all params
    public Simulator(double r1,double r2,double h,double j1,double j2){
        int r;
        Random rand = new Random();
        CA = new int[SpaceSize][];
        for ( int i = 0; i < SpaceSize; i++ ){
            CA[i] = new int[SpaceSize];
            for (int j = 0; j < SpaceSize; j++){
                r = rand.nextInt(2);
                if (r == 0) r = -1;
                CA[i][j] = r;
            }
        }
        J1 = j1;
        J2 = j2;
        H = h;
        R1 = r1;
        R2 = r2;
    }

    //create PNG image
    final public void createJpeg(){
        BufferedImage bi = new BufferedImage(SpaceSize,SpaceSize, BufferedImage.TYPE_INT_RGB);
        for(int i=0; i < SpaceSize; i++){
            for(int j=0; j < SpaceSize; j++){
                if ( CA[i][j] == 1) {
                    bi.setRGB(i,j, 0xCC0066); //red
                }else{
                    bi.setRGB(i,j, 0xFFFFCC); //yellow
                }
            }
        }
        try {
            // retrieve image
            File outputfile = new File("Experiment.png");
            ImageIO.write(bi, "png", outputfile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        writeToHTML("Experiment.html", 1);
    }

    //write to HTML
    final public void writeToHTML(String filename, int ExpNumber){
        try{
            File f = new File(filename);
            FileOutputStream fs = new FileOutputStream(f);
            BufferedWriter bwh = new BufferedWriter(new OutputStreamWriter(fs));
            bwh.write("<html><head><title>Experiment "+ExpNumber+"</title></head>\n");
            //bwh.write("<body><center><h1>Experiment "+experiment+"</h1></center><table><tr><th>Step 0</th><th>Step 1</th><th>Step 2</th><th>Step 3</th><th>Step 4</th><th>Step 5</th><th>Step 6</th><th>Step 7</th><th>Step 8</th><th>Step 9</th><th>Step 10</th><th>Step 11</th><th>Step 12</th></tr><tr>\n");
            //for(int i=0;i<=12;i++)
                bwh.write("<img src=\"Experiment.png\" style=\"image-rendering: pixelated;\" height=\"100\" width=\"100\"/>\n");
            //bwh.write("</tr></table></body></html>\n");
            fs.flush();
            bwh.flush();
            fs.close();
            bwh.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    //print array and data (for debuging)
    final public void  print(){
        System.out.printf("%.2f %.2f %.2f %.2f %.2f %n", R1, R2, H,J1,J2);

        for (int[] i : CA){
            for(int j: i){
                if (j == 1) {
                    System.out.print("*");
                }else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Usage: java Simulator R1 R2 H J1 J2");
            System.exit(1);
        }

        Simulator s = new Simulator(Double.parseDouble(args[0]),
                Double.parseDouble(args[1]),
                Double.parseDouble(args[2]),
                Double.parseDouble(args[3]),
                Double.parseDouble(args[4]));


        s.print();
        s.createJpeg();

    }
}
