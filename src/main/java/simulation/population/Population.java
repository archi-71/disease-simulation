package simulation.population;

import simulation.environment.Environment;
import simulation.params.PopulationParams;

public class Population {

    private PopulationParams parameters;
    private Individual[] individuals;

    public Population(PopulationParams params, Environment environment) {
        parameters = params;
        individuals = populateEnvironment(environment);
    }

    public Individual[] getIndividuals() {
        return individuals;
    }

    private Individual[] populateEnvironment(Environment environment) {
        individuals = new Individual[parameters.getPopulationSize()];
        for (int i = 0; i < individuals.length; i++) {
            individuals[i] = new Individual(environment);
        }
        return individuals;
    }

    public void step(int time, double deltaTime) {
        for (Individual individual : individuals) {
            individual.step(time, deltaTime);
        }
    }

    public void reset() {
        for (Individual individual : individuals) {
            individual.reset();
        }
    }
}
