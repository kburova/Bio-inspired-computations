import java.util.Random;

import static java.lang.Math.pow;

/** Project 5. Genetic algorithms **/

public class GeneticAlg {

    int numOfGens;
    int populationSize;
    double mutationProb;
    double crossoverProbability;
    int seed;
    int numOfGenerations;
    String [] population;
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
    }


    String getRandomBitString(int size){
        Random r = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0;i < size; i++){
            sb.append( r.nextInt(2));
        }
        return sb.toString();
    }

    void runExperiment(){
        for (int i = 0; i < numberOfRuns; i++){

            /** generate population **/
            for ( int p = 0; p < populationSize; p++ ){
                population[p] = getRandomBitString(numOfGens);

                calculateFitness(population[p]);
                /** calculate fitness for each individual **/
            }
        }
    }

    void calculateFitness( String individualGenes ){
        int genes = Integer.parseInt(individualGenes,2);
        System.out.println(genes + " " + individualGenes);
        double fitness = pow( (genes / pow(2, numOfGens) ), 10);
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
