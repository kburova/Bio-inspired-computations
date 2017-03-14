/*
    CS420 . Project # 3
    Hopefield Net
    Written by: Ksenia Burova
    03/08/2017
 */

import java.io.*;
import java.util.*;

public class Hopfield {

    // vector of 50 patterns, each pattern has 100 elements
    private int patterns [][];
    private int N, numOfNets;
    private double numOfStablePatternsAverage[];
    private int numOfStablePatterns[];
    private double unstableProbability[];
    private double unstableProbabilityAve[];
    private double basins[][];
    private double basinsAve[][];
    private int numOfExperiments;
    List <Integer> permulation;

    public Hopfield (int num) {

        N = 100;
        numOfNets = 50;
        numOfExperiments = num;
        permulation = new ArrayList<>();
        for (int i = 0; i < 100; i++){
            permulation.add(i);
        }
    }

    public void Run(){
        numOfStablePatternsAverage = new double[numOfNets];
        unstableProbabilityAve = new double[numOfNets];
        basinsAve = new double[numOfNets][];
        for (int i =0; i<numOfNets; i++){
            basinsAve[i] = new double[i+1];
        }

        for (int i = 0; i < numOfExperiments; i++){
            init();
            testStability();
            for (int j = 0; j < numOfNets; j++){
                for (int l = 0 ; l < basins[j].length; l++){
                    basinsAve[j][l] += basins[j][l];
                }
                numOfStablePatternsAverage[j] += numOfStablePatterns[j];
                unstableProbabilityAve[j] += unstableProbability[j];
            }
        }

        for (int j = 0; j < numOfNets; j++){
            for (int l = 0 ; l < basins[j].length; l++){
                basinsAve[j][l] /= numOfExperiments;
            }
            numOfStablePatternsAverage[j] /= numOfExperiments*1.0;
            unstableProbabilityAve[j] /= numOfExperiments*1.0;
        }
        //printToCSV();
    }
    
    //initialize all patterns
    public void init() {

        int r;
        Random rand = new Random(10);
        patterns = new int[numOfNets][];
        basins = new double[numOfNets][];
        numOfStablePatterns = new int[numOfNets];
        unstableProbability = new double[numOfNets];

        //init elements to 1 or -1.
        for (int i = 0; i < numOfNets; i++){
            patterns[i] = new int[N];
            for (int j = 0; j < N; j++){
                r = rand.nextInt(2);
                if (r == 0) r = -1;
                patterns[i][j] = r;
            }
        }

    }

    //update states according to sigma
    public int[] updateStates(double w[][], int net[]){
        int newStates[] = new int[N];
        double h;

            for (int i = 0; i < N; i++) {
                h = 0.0;
                for (int j = 0; j < N; j++) {
                    h += (w[i][j] * net[j]);
                }

                if (h < 0) newStates[i] = -1;
                else newStates[i] = 1;
            }
        return newStates;
    }

    //imprint pattern and test for stability
    public void  testStability() {

        int h[];
        double basin, basinAve;
        boolean stable;
        double w[][] =  new double[N][];
        for (int i = 0; i < N; i++ ) {
            w[i] = new double[N];
        }
        //going through each pattern
        for (int p = 0; p < numOfNets; p++){
            basins[p] = new double[p+1];
            for (int i = 0; i < N; i++ ){
                for (int j = 0; j < N; j++) {
                    if (i == j) w[i][j] = 0;
                    w[i][j] += (patterns[p][i] * patterns[p][j])/100.0;
                    //System.out.printf("%5.2f ",w[i][j]);
                }
                //System.out.println();
            }

            //check for stability
            for (int k = 0; k <= p ; k++){
                stable = true;
                h = updateStates(w, patterns[k]);

                for (int i = 0; i < N; i++){
                    if (h[i] != patterns[k][i]){
                        stable = false;
                        break;
                    }
                }

                if (stable){
                    numOfStablePatterns[p] += 1;

                    //graduate part
                    Collections.shuffle(permulation);
                    basinAve = 0;
                    int count;
                    for (int run = 0; run < 5; run++) {
                        for (count = 0; count < 50; count++) {
                            int net[] = patterns[k].clone();
                            for (int position = 0; position <= count; position++) {
                                int index = permulation.get(position);
                                net[index] *= -1;
                            }
                            for (int i = 0; i < 10; i++) {
                                net = updateStates(w, net);
                            }
                            if (Arrays.equals(net, patterns[k]) == false) {
                                break;
                            }
                        }
                        if (count != 50) basin = count + 1;
                        else basin = 50;
                        basinAve += basin;
                    }
                    basinAve /= 5;
                }else{
                   basinAve = 0;
                }
                basins[p][k] = basinAve;
                //graduate part end
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
            csvDoc.write("\nBasins:\n");
            csvDoc.write("numOfImprints/p");
            for (int i = 0; i < numOfNets; i++) {
                csvDoc.write("," + (i+1) );
            }
            csvDoc.write("\n");
            for (int j = 0; j < numOfNets; j++) {
                csvDoc.write( String.valueOf(j+1) );
                for (int l = 0; l < basinsAve[j].length; l++) {
                    csvDoc.write("," + basinsAve[j][l] );
                }
                csvDoc.write("\n");
            }
            csvDoc.flush();
            csvDoc.close();
        }catch (IOException e) {
            e.getStackTrace();
        }
    }
    public static void main(String[] args) {
        Hopfield h = new Hopfield(50);
        h.Run();
    }
}
