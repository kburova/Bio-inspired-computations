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
    private int N, numOfNets;
    private double numOfStablePatternsAverage[];
    private int numOfStablePatterns[];
    private double unstableProbability[];
    private double unstableProbabilityAve[];
    private double basins[];
    private int numOfExperiments;

    public Hopfield (int num){

        N = 100;
        numOfNets = 50;
        numOfExperiments = num;

        numOfStablePatternsAverage = new double[numOfNets];
        unstableProbabilityAve = new double[numOfNets];

        for (int i = 0; i < numOfExperiments; i++){
            //System.out.println(i);
            initilize();
            testStability();
            for (int j = 0; j < numOfNets; j++){
                numOfStablePatternsAverage[j] += numOfStablePatterns[j];
                unstableProbabilityAve[j] += unstableProbability[j];
            }
        }

        for (int j = 0; j < numOfNets; j++){
            numOfStablePatternsAverage[j] /= numOfExperiments*1.0;
            unstableProbabilityAve[j] /= numOfExperiments*1.0;
        }
        printToCSV();
    }
    
    //initialize all patterns
    public void initilize() {

        int r;
        Random rand = new Random();
        patterns = new int[numOfNets][];
        basins = new double[numOfNets];
        numOfStablePatterns = new int[numOfNets];
        unstableProbability = new double[numOfNets];

        //init elements to 1 or -1.
        for (int i = 0; i < numOfNets; i++){
            patterns[i] = new int[N];
            for (int j = 0; j < N; j++){
                r = rand.nextInt(2);
                if (r == 0) r = -1;
                patterns[i][j] = r;
                //System.out.printf("%2d ", r);
            }
            //System.out.println();
        }
    }

    public void  testStability() {

        double h;
        int newState;
        boolean stable;
        double w[][] =  new double[N][];
        for (int i = 0; i < N; i++ ) {
            w[i] = new double[N];
        }
        //going through each pattern
        for (int p = 0; p < numOfNets; p++){

            for (int i = 0; i < N; i++ ){
                for (int j = 0;j < N; j++) {
                    w[i][j] += (patterns[p][i] * patterns[p][j])/100.0;
                    //System.out.printf("%5.2f ",w[i][j]);
                }
                //System.out.println();
            }

            //check for stability
            for (int k = 0; k <= p ; k++){
                stable = true;

                for (int i = 0; i < N; i++){
                    h = 0.0;
                    for (int j = 0; j < N; j++){
                        h += (w[i][j] * patterns[k][j]);
                    }

                    if (h < 0) newState = -1;
                    else newState = 1;

                    if (patterns[k][i] != newState){
                        stable = false;
                        break;
                    }
                }
                if (stable){
                    //System.out.printf("p:%d k:%d\n", p, k );
                    numOfStablePatterns[p] += 1;


                    // generate array of numbers 1-100 in random order
                    // for (i = 0; i < 50; i++){
                    //  net[] = pattens[k];
                    //      for (j = 0; j < 10;j++)
                    //
                }else{
                   basins[p] = 0.0;
                }
            }
            unstableProbability[p] = 1 - numOfStablePatterns[p]/(1.0 * (p+1));
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
                csvDoc.write( (i+1) + "," + numOfStablePatternsAverage[i] + "," + unstableProbabilityAve[i] + "\n");
            }

            csvDoc.flush();
            csvDoc.close();
        }catch (IOException e) {
            e.getStackTrace();
        }
    }

    public static void main(String[] args) {

        Hopfield h = new Hopfield(100);
    }
}
