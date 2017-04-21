import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.pow;

/** Project 5. Genetic algorithms **/

public class GeneticAlg {

    double parent1, parent2;
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
    Random r = new Random();
    int numberOfRuns = 1;

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

        population = new String[populationSize];
        offsprings = new String[populationSize];
        fitness = new double[populationSize];
        normalizedFitness = new double[populationSize];
        runningFitness = new double[populationSize];
    }


    String getRandomBitString(int size){
        StringBuilder sb = new StringBuilder();

        for (int i = 0;i < size; i++){
            sb.append( r.nextInt(2));
        }
        return sb.toString();
    }

    void runExperiment(){
        for (int i = 0; i < numberOfRuns; i++){
            double totalFitness = 0.0;
            double totalNormFitness = 0.0;
            /** generate population **/
            for ( int p = 0; p < populationSize; p++ ){
                population[p] = getRandomBitString(numOfGens);
                fitness[p] = calculateFitness(population[p]);
                totalFitness += fitness[p];
            }
            for ( int p = 0; p < populationSize; p++ ){
               normalizedFitness[p] = fitness[p] / totalFitness;
               totalNormFitness += normalizedFitness[p];
               runningFitness[p] = totalNormFitness;
               System.out.printf("%d %.8f ",p,totalNormFitness);
            }
            System.out.println();
            for (int j = 0; j < populationSize/2; j++){

                /** select 2 parents **/
                double r1 = r.nextDouble();
                double r2 = r.nextDouble();
                parent1 = parent2 = (Arrays.binarySearch( runningFitness, r1 ) + 1) * -1 ;
                while (parent1 == parent2) {
                    parent2 = (Arrays.binarySearch(runningFitness, r2) + 1) * -1;
                }
//                System.out.println( r1+" "+ r2+" "+ parent1+" "+parent2);
                double crossover = r.nextDouble();
                if  (crossover <= crossoverProbability){
                    
                }else{
                    offsprings = population.clone();
                }
            }
        }

    }

    private double calculateFitness( String individualGenes ){
        int genes = Integer.parseInt(individualGenes,2);
        return pow( ( 1.0*genes / pow(2, numOfGens) ), 10);
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
