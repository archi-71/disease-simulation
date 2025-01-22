package simulation.disease;

import java.util.ArrayList;

import simulation.core.Simulation;
import simulation.core.SimulationOutput;
import simulation.params.DiseaseParams;
import simulation.population.Individual;
import simulation.population.Population;

public class Disease {

    private DiseaseParams parameters;
    private SimulationOutput output;
    private ArrayList<Individual> individuals;

    public Disease(DiseaseParams parameters, Population population, SimulationOutput output) {
        this.parameters = parameters;
        this.output = output;
        individuals = population.getIndividuals();
        for (Individual individual : individuals) {
            Health health = new Health(parameters, output, individual);
            individual.setHealth(health);
        }
        reset();
    }

    public void reset() {
        output.setSusceptibleNum(individuals.size());
        int exposedNum = Math.min(individuals.size(), parameters.getInitialInfected().getValue());
        for (int i = 0; i < individuals.size(); i++) {
            Health health = individuals.get(i).getHealth();
            health.reset();
            if (i < exposedNum) {
                health.transition(HealthState.EXPOSED);
                output.countSusceptibleToExposed();
            }
        }
    }

    public void step(int dayTime) {
        float timeStepDays = (float) Simulation.timeStep / Simulation.dayLength;
        for (Individual individual : individuals) {
            individual.getHealth().update(timeStepDays);
        }
    }
}
