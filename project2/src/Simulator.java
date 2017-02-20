/* Activation/Inhibition Cellular Automaton Simulator
   Written by: Ksenia Burova
   February 19, 2017
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class Simulator {

    final private int SpaceSize = 30;
    private int[][] CA;
    private double H, J1, J2, R1, R2;
    private double[] Correlation;
    private double[] JointEntropy;
    private double[] MutualInformation;
    private double lambda;

    //initialize 30x30 dimension space randomly to 1 or -1 and set all params
    public void initilize(double r1,double r2,double h,double j1,double j2){
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

        Correlation = new double[15];
        JointEntropy = new double[15];
        MutualInformation = new double[15];
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

    //calculating correlations for distances 0 to 14
    public void calculateCorrelations(){
        double distance,totalSumm,iSumm, FirstTerm, ro_l;

        for(int d = 0; d < 15; d++){
            totalSumm = 0;
            iSumm = 0;
            //cell i loops
            for (int i1 = 0; i1 < SpaceSize; i1++){
                for (int i2 = 0; i2 < SpaceSize; i2++){

                    //cell j loops
                    for (int j1 = 0; j1 < SpaceSize; j1++) {
                        for (int j2 = 0; j2 < SpaceSize; j2++) {

                            //eliminate duplicates
                            if (i1 == j1 && i2 >= j2) continue;

                            distance = getDistance(i1,i2,j1,j2);
                            if (distance == d)
                               totalSumm += CA[i1][i2]*CA[j1][j2];
                        }//j2
                    }//j1
                    iSumm += CA[i1][i2];
                }//i2
            }//i1

            //calculate correlation for d
            if (d == 0)
                FirstTerm = 0;
            else
                FirstTerm = 2 * totalSumm /(Math.pow(SpaceSize,2) * 4 * d);

            ro_l = Math.abs(FirstTerm - Math.pow((iSumm / Math.pow(SpaceSize,2) ),2));
            Correlation[d] = ro_l;
        }//d
    }

    // calculating overall entropy
    public double calculateEntropy(){
        int betta, sum = 0;
        double Pr_plus, Pr_minus, entropy, term1, term2;

        for (int x = 0; x < SpaceSize; x++){
            for (int y = 0; y <SpaceSize; y++){
                betta = (1 + CA[x][y])/2;
                //System.out.println(betta);
                sum += betta;
            }
        }
        Pr_plus = sum / Math.pow(SpaceSize,2);
        Pr_minus = 1 - Pr_plus;

        // avoiding undefined log computations here
        if (Pr_plus == 0)
            term1 = 0;
        else
            term1 = Pr_plus*Math.log(Pr_plus);

        if (Pr_minus == 0)
            term2 = 0;
        else
            term2 = Pr_minus*Math.log(Pr_minus);

        entropy = -(term1 + term2);

        return entropy;
    }

    //calculating joint entropy for each possible distance l 0 to 14
    public void calculateJointEntropy(){
        double PosSum, NegSum, distance, Pr_plus, Pr_minus, Pr_plus_minus, Entropy, coeff;
        double term1, term2, term3;
        int betta_i, betta_j;
        double OverallEntropy = calculateEntropy();

        for(int d = 0; d < 15; d++){
            PosSum = 0;
            NegSum = 0;
            //cell i loops
            for (int i1 = 0; i1 < SpaceSize; i1++){
                for (int i2 = 0; i2 < SpaceSize; i2++){

                    //cell j loops
                    for (int j1 = 0; j1 < SpaceSize; j1++) {
                        for (int j2 = 0; j2 < SpaceSize; j2++) {

                            //eliminate duplicates
                            if (i1 == j1 && i2 >= j2) continue;

                            distance = getDistance(i1,i2,j1,j2);
                            if (d == distance) {
                                betta_i = (1 + CA[i1][i2]) / 2;
                                betta_j = (1 + CA[j1][j2]) / 2;
                                PosSum += betta_i * betta_j;

                                betta_i = (1 + -CA[i1][i2]) / 2;
                                betta_j = (1 + -CA[j1][j2]) / 2;
                                NegSum += betta_i * betta_j;
                            }
                        }//j2
                    }//j1

                }//i2
            }//i1

            coeff = 2 / (Math.pow(SpaceSize,2) * 4 * d);
            Pr_plus = coeff * PosSum;
            Pr_minus = coeff * NegSum;
            Pr_plus_minus = 1 - Pr_plus - Pr_minus;

            if (Pr_plus == 0)
                term1 = 0;
            else
                term1 = Pr_plus*Math.log(Pr_plus);

            if (Pr_minus == 0)
                term2 = 0;
            else
                term2 = Pr_minus*Math.log(Pr_minus);

            if (Pr_plus_minus == 0)
                term3 = 0;
            else
                term3 = Pr_plus_minus*Math.log(Pr_plus_minus);

            Entropy = -( term1 + term2 + term3 );
            JointEntropy[d] = Entropy;
            MutualInformation[d] = 2 * OverallEntropy - Entropy;
        }//d
    }

    public void calculateLambda(){

        double min = 100;
        int distance = 0;

        for (int i = 1; i < 15; i++){
            double temp = Math.pow(Correlation[0], 1 / Math.E);
            if ( Math.abs(temp - Correlation[i]) < min){
                min =  Math.abs(temp - Correlation[i]);
                distance = i;
            }
        }
        lambda = distance;
    }
    //create PNG image
    final public void createPNG(String path, int index){
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
            File outputfile = new File(path+"/Run_"+ Integer.toString(index) +".png");
            ImageIO.write(bi, "png", outputfile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        //writeToHTML( index);
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

    public void simulate() {

        double j1=0,j2=0;
        try {
            File file = new File("MasterExperiment.csv");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write("Your, Name, \n");
            bw.write("Wrap:,true\n");
            new File("Experiments").mkdir();
            new File("Experiments/Experiment_1").mkdir();
            new File("Experiments/Experiment_2").mkdir();
            new File("Experiments/Experiment_3").mkdir();

            int [] possibleR1 = new int[0];
            int [] possibleR2 = new int[0];
            int [] possibleh = new int[0];
            int numOfCombinations = 0;

            for (int i = 1; i <= 3; i++){

                switch(i){
                    case(1):
                        j1 = 1; j2 = 0;
                        possibleR1 = new int[]{ 1,3,6,3};
                        possibleR2 = new int[]{ 15,15,15,9};
                        possibleh =  new int[]{ -1,-1,-2,0};
                        numOfCombinations = possibleh.length;
                        break;
                    case(2):
                        j1 = 0; j2 = -0.1;
                        possibleR1 = new int[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 4, 4, 4, 4, 9, 9, 9};
                        possibleR2 = new int[]{ 2, 4, 4, 4, 6, 6, 6, 9,13, 5, 7, 7, 7,12,12,12,12};
                        possibleh =  new int[]{ 0,-2,-1, 0,-5,-3, 0, 0, 0, 0,-5,-3, 0, 0,-6,-3, 0};
                        numOfCombinations = possibleh.length;
                        break;
                    case(3):
                        j1 = 1; j2 = -0.1;
                        possibleR1 = new int[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 7, 7, 7, 7, 7, 7,12,12, 9, 9, 9, 6};
                        possibleR2 = new int[]{15, 6, 6, 6, 2, 4, 4, 4, 5, 5, 5, 5, 9, 9, 9, 9, 9,13,14,14,14,14,14, 5, 5, 9, 9, 9, 9, 9,14,14,14,14,15, 5, 7,12,14, 9, 9, 9,14,14,14,14,12,12,12,15};
                        possibleh =  new int[]{-1, 0 -3,-5, 0,-2,-1, 0,-4,-2, 0, 2,-3, 0, 3, 6,-6, 0, 3, 0,-3, 6,-1, 0,-1,-3, 0, 3, 6,-6,-3, 0, 3, 6,-1, 0, 0, 0,-1, 0, 1,-3, 0, 3, 0, 2,-6,-3, 0,-2};
                        numOfCombinations = possibleh.length;
                        break;
                    default:
                        break;
                }
                //TODO: create HTML for each experiment
                //TODO: split it into such View

                for (int j = 0; j < numOfCombinations; j++){
                    String path = "Experiments/Experiment_"+ i +"/Combination_" + (j+1);
                    new File( path ).mkdir();
                    //TODO: HTML  Combination row

                    for (int r = 1; r < 5; r++){
                        //TODO: HTML TABLE
                        // Run1, Run2, Run3, Run4
                        // Pic1, Pic2, Pic3, Pic4
                        initilize( possibleR1[j], possibleR2[j], possibleh[j], j1, j2);
                        updateTheGrid();
                        createPNG( path,r);
                    }
                }

            }
//            System.out.print(possibleR1.length);
//            System.out.print(possibleR2.length);
//            System.out.print(possibleh.length);
        }catch (IOException e){
            e.getStackTrace();
        }
    }
    public static void main(String[] args) {

        Simulator s = new Simulator();
        s.simulate();
//        if (args.length != 5) {
//            System.out.println("Usage: java Simulator");
//            System.exit(1);
//        }

//        Simulator s = new Simulator(Double.parseDouble(args[0]),
//                Double.parseDouble(args[1]),
//                Double.parseDouble(args[2]),
//                Double.parseDouble(args[3]),
//                Double.parseDouble(args[4]));
//
//
//        s.print();
//        s.createJpeg(0);
//        s.updateTheGrid();
//        s.createJpeg(1);
//        double [] rv = s.calculateCorrelations();
//        for (double d : rv )
//            System.out.printf( "%f ", d);
//
//        s.calculateEntropy();
    }
}
