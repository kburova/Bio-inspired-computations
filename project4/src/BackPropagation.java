import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

/**
 Project 4. Neural Nets
 Written by: Ksenia Burova
 **/

public class BackPropagation {

    private int numberOfInputs = 2;
    private int numberOfLayers = 3;
    private int [] numberOfNeurons = {2, 3, 2};
    private double learningRate = 1.3;
    private int numberOfEpochs = 200;
    private double [][][] hiddenWeights;
    private double [] outputWeights;
    private double h, sigma;
    private double [][][] deltaHidden;
    private double [] deltaOutput;
    private double [] input;
    private double [][] hiddenBiasWeight;
    private double outputBiasWeight;

    public BackPropagation(){
        Random rand = new Random();
        hiddenWeights = new double[numberOfLayers][][];
        deltaHidden = new double[numberOfLayers][][];
        outputWeights = new double[numberOfNeurons[numberOfNeurons.length-1]]; //number of nodes in last hidden layer
        deltaOutput = new double[numberOfNeurons[numberOfNeurons.length-1]];
        input = new double[numberOfInputs];
        hiddenBiasWeight = new double[numberOfLayers][];

        for (int i = 0; i < numberOfLayers; i++){
            hiddenWeights[i] = new double[ numberOfNeurons[i] ][];
            deltaHidden[i] = new double[ numberOfNeurons[i] ][];
            hiddenBiasWeight[i] = new double[ numberOfNeurons[i]];
            for (int j = 0; j < numberOfNeurons[i]; j++) {
                int numOfPrevLayerNodes = (i == 0) ? numberOfInputs : numberOfNeurons[i-1];
                hiddenWeights[i][j] = new double[numOfPrevLayerNodes];
                deltaHidden[i][j] = new double[numOfPrevLayerNodes];
                hiddenBiasWeight[i][j] = (rand.nextInt((100 - (-100)) + 1) - 100) / 1000.0;;

                for (int c = 0; c < numOfPrevLayerNodes; c++){
                    /**randomly initialize weights,  generate number form -100 to 100 and divide
                     by 1000 to get range from -0.1 to 0.1 **/
                    hiddenWeights[i][j][c] = (rand.nextInt((100 - (-100)) + 1) - 100) / 1000.0;
                    //System.out.print(weight+" ");
                }
            }
            outputBiasWeight = (rand.nextInt((100 - (-100)) + 1) - 100) / 1000.0;
        }
        for (int i = 0; i < numberOfNeurons[numberOfNeurons.length-1]; i++){
            outputWeights[i] = (rand.nextInt((100 - (-100)) + 1) - 100) / 1000.0;;
            //System.out.print(weight+" ");
        }
    }

    public void trainNetwork() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("problem1/training1.txt"));

            /** initialize everything to 0 **/
            sigma = 0;
            h = 0;
            for (int i = 0; i < numberOfLayers; i++) {
                for (int j = 0; j < numberOfNeurons[i]; j++) {
                    int numOfPrevLayerNodes = (i == 0) ? numberOfInputs : numberOfNeurons[i - 1];
                    for (int c = 0; c < numOfPrevLayerNodes; c++) {
                        deltaHidden[i][j][c] = 0;
                    }
                }
            }
            for (int i = 0; i < numberOfNeurons[numberOfNeurons.length - 1]; i++) {
                deltaOutput[i] = 0;
            }

            for (int i = 0; i < numberOfLayers; i++) {
                for (int j = 0; j < numberOfNeurons[i]; j++) {
                    if (i == 0){
                        for (int c = 0; c < numberOfInputs; c++){
                            h += hiddenWeights[i][j][c] * input[c];
                        }
                    }else{
                        for (int c = 0; c < numberOfNeurons[i - 1]; c++ ){
                            h += hiddenWeights[i][j][c] /* * sigma */;
                        }
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void validateNetwork(int epoch) {}
    public void testNetwork() {}

    public void run(){
        for (int i = 0; i < numberOfEpochs; i++){
            trainNetwork();
            validateNetwork(i);
        }
        testNetwork();
    }
    public static void main(String[] args) {
        if (args.length != 3){
            System.out.println("Usage: java BackPropagation [train.txt] [validate.txt] [test.txt] ");
        }
        BackPropagation bp = new BackPropagation();
        bp.run();
    }
}
