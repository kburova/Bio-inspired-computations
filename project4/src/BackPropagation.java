import javax.swing.*;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 Project 4. Neural Nets
 Written by: Ksenia Burova
 **/

public class BackPropagation {

    String trainFile, validateFile, testFile;
    BufferedWriter bw;
    String problemName = "Xor";
    private int numberOfInputs = 2;
    private int numberOfLayers = 1;
    private int [] numberOfNeurons = {3};
    private double learningRate = 1;
    private int numberOfEpochs = 1000;

    private double [] input;
    private double [] outputWeights;
    private double [][][] hiddenWeights;
    private double [][] hiddenSigma;
    private double [][] hiddenBiasWeight;
    private double [][] hiddenH;
    private double [][] hiddenDelta;
    private double outputSigma;
    private double outputH;
    private double outputDelta;
    private double outputBiasWeight;
    private double expectedOutput;
    ArrayList <Double> RMSE;

    public BackPropagation(String trainFile, String validateFile, String testFile){
        try {
            bw = new BufferedWriter(new FileWriter(problemName + "_" + numberOfLayers + "_" + Arrays.toString(numberOfNeurons) + "_" + numberOfEpochs+"_"+learningRate + ".csv"));
        }catch (IOException e){}
        this.trainFile = trainFile;
        this.validateFile = validateFile;
        this.testFile = testFile;

        RMSE = new ArrayList<>();
        Random rand = new Random();
        hiddenWeights = new double[numberOfLayers][][];
        outputWeights = new double[numberOfNeurons[numberOfNeurons.length-1]]; //number of nodes in last hidden layer
        input = new double[numberOfInputs];

        hiddenBiasWeight = new double[numberOfLayers][];
        hiddenSigma = new double[numberOfLayers][];
        hiddenH = new double[numberOfLayers][];
        hiddenDelta = new double[numberOfLayers][];



        for (int i = 0; i < numberOfLayers; i++){
            hiddenWeights[i] = new double[ numberOfNeurons[i] ][];
            hiddenBiasWeight[i] = new double[ numberOfNeurons[i]];
            hiddenSigma[i] = new double[ numberOfNeurons[i]];
            hiddenH[i] = new double[ numberOfNeurons[i]];
            hiddenDelta[i] = new double[ numberOfNeurons[i]];

            for (int j = 0; j < numberOfNeurons[i]; j++) {
                int numOfPrevLayerNodes = (i == 0) ? numberOfInputs : numberOfNeurons[i-1];
                hiddenWeights[i][j] = new double[numOfPrevLayerNodes];
                hiddenBiasWeight[i][j] = (rand.nextInt((100 - (-100)) + 1) - 100) / 1000.0;;

                for (int c = 0; c < numOfPrevLayerNodes; c++){
                    /**randomly initialize weights,  generate number form -100 to 100 and divide
                     by 1000 to get range from -0.1 to 0.1 **/
                    hiddenWeights[i][j][c] = (rand.nextInt((100 - (-100)) + 1) - 100) / 1000.0;
                    //System.out.print(weight+" ");
                }
            }
        }
        for (int i = 0; i < numberOfNeurons[numberOfNeurons.length-1]; i++){
            outputWeights[i] = (rand.nextInt((100 - (-100)) + 1) - 100) / 1000.0;
            //System.out.print(weight+" ");
        }
        outputBiasWeight = (rand.nextInt((100 - (-100)) + 1) - 100) / 1000.0;
    }

    private void computeOutputs(){
        /** initialize everything to 0 **/
        for (int i = 0; i < numberOfLayers; i++) {
            for (int j = 0; j < numberOfNeurons[i]; j++) {
                hiddenDelta[i][j] = 0;
                hiddenH[i][j] = 0;
                hiddenSigma[i][j] = 0;
            }
        }
        outputDelta = 0;
        outputH = 0;
        outputSigma = 0;
        /** output(h and sigma) and delta updates for hidden layers **/
        for (int i = 0; i < numberOfLayers; i++) {
            for (int j = 0; j < numberOfNeurons[i]; j++) {
                if (i == 0){ // check if first layer or hidden
                    for (int c = 0; c < numberOfInputs; c++){
                        hiddenH[i][j] += hiddenWeights[i][j][c] * input[c];
                    }
                }else{
                    for (int c = 0; c < numberOfNeurons[i - 1]; c++ ){
                        hiddenH[i][j] += hiddenWeights[i][j][c] * hiddenSigma[i-1][c];
                    }
                }
                hiddenH[i][j] += hiddenBiasWeight[i][j];
                hiddenSigma[i][j] = 1/(1 + Math.exp(-hiddenH[i][j]));
            } //for ( nodes/neurons )
        } //for ( layers )
        /** output(h and sigma) and delta updates for output **/
        for (int j = 0; j < numberOfNeurons[numberOfNeurons.length-1]; j++){
            outputH += hiddenSigma[numberOfLayers-1][j] * outputWeights[j];
        }
        outputH += outputBiasWeight;
        outputSigma =  1/(1 + Math.exp(-outputH));
    }

    private void computeDeltaValues(){
        outputDelta = outputSigma * (1-outputSigma) * (expectedOutput - outputSigma);
        for (int i = numberOfLayers-1; i>=0 ; i--){
            for (int j = 0; j < numberOfNeurons[i]; j++){
                if (i == numberOfLayers-1){
                    hiddenDelta[i][j] = hiddenSigma[i][j] * (1-hiddenSigma[i][j]) * outputDelta * outputWeights[j];
                }else{
                    for (int c = 0; c < numberOfNeurons[i+1]; c++){
                        hiddenDelta[i][j] += hiddenSigma[i][j] * (1 - hiddenSigma[i][j]) * hiddenDelta[i+1][c] * hiddenWeights[i+1][c][j];
                    }
                }
            }
        }
    }
    private void updateWeights(){
        /** hidden layers **/
        for (int i = 0; i < numberOfLayers; i++) {
            for (int j = 0; j < numberOfNeurons[i]; j++) {
                if (i == 0){
                    for (int c = 0; c < numberOfInputs; c++){
                        hiddenWeights[i][j][c] += ( learningRate * hiddenDelta[i][j] ) * input[c];
                    }
                }else{
                    for (int c = 0; c < numberOfNeurons[i - 1]; c++ ){
                        hiddenWeights[i][j][c] += ( learningRate * hiddenDelta[i][j] ) * hiddenSigma[i-1][c];
                    }
                }
                hiddenBiasWeight[i][j] += learningRate * hiddenDelta[i][j];
            } //for ( nodes/neurons )
        } //for ( layers )
        /** output node **/
        for (int j = 0; j < numberOfNeurons[numberOfNeurons.length-1]; j++){
            outputWeights[j] += learningRate * outputDelta * hiddenSigma[numberOfLayers-1][j];
        }
        outputBiasWeight += learningRate * outputDelta;
    }
    public void trainNetwork() {
        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader(trainFile));
            while ( (line = br.readLine()) != null ) {
                String [] vals = line.split(" ");
                for (int i = 0; i < numberOfInputs; i++){
                    input[i] = Double.valueOf(vals[i]);
                }
                expectedOutput = Double.valueOf(vals[vals.length-1]);

                computeOutputs();
                computeDeltaValues();
                updateWeights();
            }
            br.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void validateNetwork() {
        double sum = 0.0;
        int numOfPatterns = 0;
        double rmse;
        try{
            String line;
            BufferedReader br = new BufferedReader(new FileReader(validateFile));
            while ( (line = br.readLine()) != null ) {
                String[] vals = line.split(" ");
                for (int i = 0; i < numberOfInputs; i++){
                    input[i] = Double.valueOf(vals[i]);
                }
                expectedOutput = Double.valueOf(vals[vals.length-1]);
                computeOutputs();
                sum += Math.pow((expectedOutput - outputSigma),2);
                numOfPatterns++;
            }
            rmse = Math.sqrt(sum/(2.0*numOfPatterns));
            RMSE.add(rmse);;
            br.close();
        }catch (IOException e){
            e.getStackTrace();
        }
    }

    private void printRMSE(){
        try{
            bw.write("Project 4, CS420\n Artificial, Neural Nets\nCompleted by:, Ksenia Burova\n\n\n");
            bw.write("Epoch #, RMSE\n");
            for (int i = 0; i < RMSE.size(); i++) {
                System.out.println(i+" "+RMSE.get(i));
                bw.write(i + "," + RMSE.get(i) + "\n");
            }
            bw.flush();

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void testNetwork() {
        try{
            String line;
            int solved = 0;
            int notSolved  = 0;
            bw.write("-----,testing network, ----\nExpected,Computed, Computed real, Is solved\n");
            BufferedReader br = new BufferedReader(new FileReader(testFile));
            while ( (line = br.readLine()) != null ) {
                String[] vals = line.split(" ");
                for (int i = 0; i < numberOfInputs; i++){
                    input[i] = Double.valueOf(vals[i]);
                }
                expectedOutput = Double.valueOf(vals[vals.length-1]);
                computeOutputs();
                long result = Math.round(outputSigma);
                bw.write(expectedOutput+","+ result+","+ outputSigma);
                if (expectedOutput != result){
                    bw.write(", no\n");
                    notSolved++;
                }else {
                    bw.write(",yes\n");
                    solved++;
                }
            }
            double accuracy = 1.0 * solved/(solved+notSolved);
            bw.write("Accuracy percentage: ," + accuracy*100 + "%\n");
            br.close();
            bw.flush();
            bw.close();
        }catch (IOException e){
            e.getStackTrace();
        }
    }

    public void run(){
        for (int i = 0; i < numberOfEpochs; i++){
            trainNetwork();
            validateNetwork();
        }
        printRMSE();
        testNetwork();
    }
    public static void main(String[] args) {
        if (args.length != 3){
            System.out.println("Usage: java BackPropagation [train.txt] [validate.txt] [test.txt] ");
        }
//        try{
//            Random r = new Random();
//            BufferedWriter bw  = new BufferedWriter(new FileWriter("xor/training.txt"));
//            for (int i = 0; i < 200; i++){
//                int a = r.nextInt(2);
//                int b = r.nextInt(2);
//                bw.write(a+" "+ b +" "+ (a^b) + "\n");
//            }
//            bw.flush();
//            bw.close();
//            bw  = new BufferedWriter(new FileWriter("xor/validation.txt"));
//            for (int i = 0; i < 100; i++){
//                int a = r.nextInt(2);
//                int b = r.nextInt(2);
//                bw.write(a+" "+ b +" "+ (a^b) + "\n");
//            }
//            bw.flush();
//            bw.close();
//            bw  = new BufferedWriter(new FileWriter("xor/testing.txt"));
//            for (int i = 0; i < 50; i++){
//                int a = r.nextInt(2);
//                int b = r.nextInt(2);
//                bw.write(a+" "+ b +" "+ (a^b) + "\n");
//            }
//            bw.flush();
//            bw.close();
//        }catch (IOException e){}
        BackPropagation bp = new BackPropagation(args[0],args[1],args[2]);
        bp.run();
    }
}
