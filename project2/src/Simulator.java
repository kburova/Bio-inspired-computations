/* Activation/Inhibition Cellular Automaton Simulator
   Written by: Ksenia Burova
   February 19, 2017
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class Simulator {

    final int SpaceSize = 30;
    static private int[][] CA;
    private double H, J1, J2, R1, R2;

    //initialize 30x30 dimension space randomly to 1 or -1 and set all params
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

    //Update the grid using "update rule" formula until it's stabilized
    public void updateTheGrid(){
        boolean AICAstabilized = false;
        int NewState,CurrentState;
        int FarAwaySum, NearSum;
        double distance, StateVal;

        // use indexes arrays to iterate randomly through all the possible states
        // shuffle these for 2 outer loops iterations fro randomness
        List<Integer> Is = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29);
        List<Integer> Ys = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29);
        while ( !AICAstabilized ){
            AICAstabilized = true;

            Collections.shuffle(Is);
            for (int i = 0; i < SpaceSize; i++){
                Collections.shuffle(Ys);
                for (int j = 0; j < SpaceSize; j++){
                    //randomly chosen cell
                    CurrentState = CA[ Is.get(i) ][ Ys.get(j) ];

                    FarAwaySum = 0;
                    NearSum = 0;
                    for (int x = 0; x < SpaceSize; x++){
                        for (int y =0; y < SpaceSize; y++){
                            //not itself
                            if ( !(x == Is.get(i) && y == Ys.get(j)) ){
                                distance = getDistance(x ,y , Is.get(i), Ys.get(j));
                                if (distance < R1){
                                    NearSum += CA[x][y];
                                }else if (distance >= R1 && distance < R2){
                                    FarAwaySum += CA[x][y];
                                }
                            }
                        }
                    }
                    StateVal = H + J1*NearSum + J2*FarAwaySum;
                    if (StateVal < 0)
                        NewState = -1;
                    else
                        NewState = 1;

                    if ( NewState != CurrentState ){
                        AICAstabilized = false;
                        CA[ Is.get(i) ][ Ys.get(j) ] = NewState;
                    }

                }
            }
        }
    }
    //create PNG image
    final public void createJpeg(int index){
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
            File outputfile = new File("Pic_"+ Integer.toString(index) +".png");
            ImageIO.write(bi, "png", outputfile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        writeToHTML( index);
    }

    //write to HTML
    final public void writeToHTML(int ExpNumber){
        try{
            //FileWriter writer = new FileWriter(System.getProperty("user.home") + "/hello.html");
            //writer.write(htmlBuilder.toString()); used string builder
            File f = new File("Experiment_"+ Integer.toString(ExpNumber) + ".html");
            FileOutputStream fs = new FileOutputStream(f);
            BufferedWriter bwh = new BufferedWriter(new OutputStreamWriter(fs));
            bwh.write("<html><head><title>Experiment "+ExpNumber+"</title></head>\n");
            bwh.write("<img src=\"Pic_" + Integer.toString(ExpNumber) + ".png\" style=\"image-rendering: pixelated;\" height=\"100\" width=\"100\"/>\n");


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

    //calculate distance between 2 coordinates (cells) in torus space
    final public double getDistance(int x1, int y1, int x2, int y2){

        double x = Math.abs(x1-x2);
        double y = Math.abs(y1-y2);
        if (x > 15) x = 30 - x;
        if (y > 15) y = 30 - y;

        return x + y;
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
        s.createJpeg(0);
        s.updateTheGrid();
        s.createJpeg(1);

    }
}
