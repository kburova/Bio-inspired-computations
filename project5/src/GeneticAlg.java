import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

import static java.lang.Math.pow;

/** Project 5. Genetic algorithms **/

public class GeneticAlg {

    int parent1, parent2;
    int numOfGens;
    int populationSize;
    double mutationProb;
    double crossoverProbability;
    int seed;
    int numOfGenerations;
    String [] population;
    String [] offsprings;

    double [] fitness;
    double [] normalizedFitness;
    double [] runningFitness;
    Random r ;
    ArrayList <Double> avgFitness;
    ArrayList <Double> bestFitness;
    ArrayList <Double> avgCorrectBits;
    ArrayList <Integer> bestCorrectBits;
    BufferedWriter bw;
    int currentRun;
    int numOfRuns = 10;

    public GeneticAlg(int numOfGens,
                      int populationSize,
                      double mutationProb,
                      double crossoverProbability,
                      int seed,
                      int numOfGenerations){

        this.numOfGens = numOfGens;
        this.populationSize = populationSize;
        this.mutationProb = mutationProb;
        this.crossoverProbability = crossoverProbability;
        this.seed = seed;
        this.numOfGenerations = numOfGenerations;
        r = new Random(seed);
        population = new String[populationSize];
        offsprings = new String[populationSize];
        fitness = new double[populationSize];
        normalizedFitness = new double[populationSize];
        runningFitness = new double[populationSize];
        avgFitness = new ArrayList<>();
        bestFitness = new ArrayList<>();
        avgCorrectBits = new ArrayList<>();
        bestCorrectBits = new ArrayList<>();
        try {
            bw = new BufferedWriter(new FileWriter("data4_"+populationSize+"_"+numOfGens+ "_"+mutationProb+ "_"+crossoverProbability+"_"+seed+"_"+ numOfGenerations+ ".csv"));
        }catch (IOException e){}

    }

    String getRandomBitString(int size){
        int generation;
        String s = "";
        if (size <=32 ) {
            generation = r.nextInt((int) pow(2, size));
            s = String.format("%" + size + "s", Integer.toBinaryString(generation)).replace(' ', '0');
        }else{
           for (int i = 0; i < size; i++)
               s += Integer.toString( r.nextInt(2));
        }
        return s;
    }

    void runExperiment(){

        for (currentRun = 0; currentRun < numOfRuns; currentRun++) {
            for ( int p = 0; p < populationSize; p++ ) {
                population[p] = getRandomBitString(numOfGens);
            }

            for (int i = 0; i < numOfGenerations; i++) {
                double totalFitness = 0.0;
                double totalNormFitness = 0.0;
                /** calculate fitness for each individual**/

                for (int p = 0; p < populationSize; p++) {
                    fitness[p] = calculateFitness(population[p]);
                    totalFitness += fitness[p];
                }
                /** calculate normalized fitness and running normalized fitness
                 for future probabilistic choice of parents**/
                for (int p = 0; p < populationSize; p++) {
                    normalizedFitness[p] = fitness[p] / totalFitness;
                    totalNormFitness += normalizedFitness[p];
                    runningFitness[p] = totalNormFitness;
                }

                for (int j = 0; j < populationSize / 2; j++) {
                    /** select 2 parents **/
                    double r1 = r.nextDouble();
                    double r2 = r.nextDouble();
                    parent1 = (Arrays.binarySearch(runningFitness, r1) + 1) * -1;
                    parent2 = (Arrays.binarySearch(runningFitness, r2) + 1) * -1;
                    while (parent1 == parent2) {
                        r2 = r.nextDouble();
                        parent2 = (Arrays.binarySearch(runningFitness, r2) + 1) * -1;
                    }
                    /** mate parents **/
                    double crossover = r.nextDouble();
                    if (crossover <= crossoverProbability) {
                        int randomBit = r.nextInt(numOfGens);
                        offsprings[j * 2] = population[parent1].substring(0, randomBit) + population[parent2].substring(randomBit);
                        offsprings[j * 2 + 1] = population[parent2].substring(0, randomBit) + population[parent1].substring(randomBit);
                    } else {
                        offsprings[j * 2] = population[parent1];
                        offsprings[j * 2 + 1] = population[parent2];
                    }
                    StringBuilder os1 = new StringBuilder();
                    StringBuilder os2 = new StringBuilder();

                    /** mutations on the offspring **/
                    for (int b = 0; b < numOfGens; b++) {
                        int mutatedBit;
                        /** offspring 1 **/
                        mutatedBit = r.nextInt(numOfGens);
                        if (mutatedBit <= mutationProb) os1.append(offsprings[j * 2].charAt(b) == '0' ? '1' : '0');
                        else os1.append(offsprings[j * 2].charAt(b));

                        /** offspring 2**/
                        mutatedBit = r.nextInt(numOfGens);
                        if (mutatedBit <= mutationProb) os2.append(offsprings[j * 2 + 1].charAt(b) == '0' ? '1' : '0');
                        else os2.append(offsprings[j * 2 + 1].charAt(b));
                    }

                    offsprings[j * 2] = os1.toString();
                    offsprings[j * 2 + 1] = os2.toString();
                }

                /** update population **/
                population = offsprings.clone();

                /** statistics **/
                avgFitness.add(totalFitness / (populationSize * 1.0));
                double CorrectBits = 0;
                for (int a = 0; a < populationSize; a++) {
                    for (int b = 0; b < numOfGens; b++) {
                        if (population[a].charAt(b) == '1') CorrectBits += 1;
                    }
                }
                avgCorrectBits.add(CorrectBits * 1.0 / populationSize);
                int bestIndex = findMax(normalizedFitness);

                bestFitness.add(fitness[bestIndex]);

                int bestBits = 0;
                for (int b = 0; b < numOfGens; b++) {
                    if (population[bestIndex].charAt(b) == '1') bestBits++;
                }
                bestCorrectBits.add(bestBits);
            }

            printToCSV();
            avgFitness.clear();
            avgCorrectBits.clear();
            bestFitness.clear();
            bestCorrectBits.clear();
        }
        try {
            bw.close();
        }catch (IOException e){}
    }

    private void printToCSV(){

        try {
            bw.write("Run # "+ currentRun + "\n");
            bw.write( "Generation #, Average Fitness, BestFitness, CorrectBits # \n");
            for (int i = 0; i < numOfGenerations; i++){
                bw.write( i+","+avgFitness.get(i)+","+bestFitness.get(i)+","+bestCorrectBits.get(i)+"\n");
            }
        }catch (IOException e){}
    }
    private double calculateFitness( String individualGenes ){
        if (numOfGens <= 32) {
            int genes = Integer.parseInt(individualGenes, 2);
            return pow((1.0 * genes / pow(2, numOfGens)), 10);
        }else{
            long genes = Long.parseLong(individualGenes, 2);
            return pow((1.0 * genes / pow(2, numOfGens)), 10);
        }
    }

    public int findMax(double [] arr){
        double max = 0;
        int index = 0;
        for (int i =0; i < arr.length; i++){
            if (arr[i] > max){
                max = arr[i];
                index = i;
            }
        }
        return index;
    }
    public static void main(String[] args) {
        if (args.length != 6){
            System.out.println("usage: java GeneticAlg [numOfGenes] [populationSize] [mutationProb] [crossoverProb] [seed] [numOfGenerations]");
            System.exit(1);
        }else{
            GeneticAlg ga = new GeneticAlg(
                    Integer.parseInt(args[0]),
                    Integer.parseInt(args[1]),
                    Double.parseDouble(args[2]),
                    Double.parseDouble(args[3]),
                    Integer.parseInt(args[4]),
                    Integer.parseInt(args[5])
            );
            ga.runExperiment();
        }
    }
}
