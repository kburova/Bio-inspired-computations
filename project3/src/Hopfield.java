/*
    CS420 . Project # 3
    Hopefield Net
    Written by: Ksenia Burova
    03/08/2017
 */

import java.io.*;
import java.util.Random;

public class Hopfield {

    // vector of 50 patterns, each pattern has 100 elements
    private int patterns [][];
    private double weights[][];
    private int N, numOfNets;
    private int numOfStablePatterns[];

    public Hopfield (){
        initilize();
        imprintPatterns();
        testStability();
        printToCSV();
    }
    
    //initialize all patterns
    public void initilize() {

        int r;
        N = 100;
        numOfNets = 50;
        Random rand = new Random();
        weights = new double [N][];
        patterns = new int[numOfNets][];
        numOfStablePatterns = new int[numOfNets];

        //init elements to 1 or -1.
        for (int i = 0; i < numOfNets; i++){
            patterns[i] = new int[N];
            for (int j = 0; j < N; j++){
                r = rand.nextInt(2);
                if (r == 0) r = -1;
                patterns[i][j] = r;
            }
        }
        for (int i = 0; i < N; i++){
            weights[i] = new double[N];
        }
    }

    public void  imprintPatterns() {

        for (int i = 0; i < N; i++){
            for (int j = 0; j < N; j++){
                for (int k = 0; k < numOfNets; k++) {
                    if (i == j) weights[i][j] = 0;
                    weights[i][j] += patterns[k][i] * patterns[k][j];
                }
                // divide weight by 100
                weights[i][j] /= N * 1.0;
            }
        }
    }

    public void testStability(){

        double h, newState, numOfUnStable = 0;
        boolean isStable;

        for (int k = 0; k < numOfNets; k++){
            isStable = true;
            for (int i = 0; i < N; i++){
                h = 0;
                for (int j = 0; j < N; j++){
                    h += weights[i][j] * patterns[k][j];
                }
                System.out.printf("h: %f\n", h);
                if ( h < 0) newState = -1;
                else newState = 1;

                //if state differs, mark it unstable, but mark it once by including bool into conditional
                if (isStable && newState != patterns[k][i]){
                    isStable = false;
                    numOfUnStable++;
                }
            }
            System.out.printf("P: %d, Stable: %b\n", k, isStable);
            numOfStablePatterns[k] = (int) (k + 1 - numOfUnStable);
        }
    }
    private void printToCSV(){
        try {
            File file = new File("MasterExperiment.csv");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter csvDoc = new BufferedWriter(new OutputStreamWriter(fos));
            csvDoc.write(" Name:, Ksenia, Burova, CS420 \n\n");
            csvDoc.write(" p #, Num of Stable, Fraction of Unstable\n");
            for (int i = 0; i < numOfNets; i++){
                csvDoc.write( (i+1) + "," + numOfStablePatterns[i] + "," + (1 - (numOfStablePatterns[i]*1.0/(i+1))) + "\n");
            }

            csvDoc.flush();
            csvDoc.close();
        }catch (IOException e) {
            e.getStackTrace();
        }
    }

    public static void main(String[] args) {
        Hopfield h = new Hopfield();
    }
}
