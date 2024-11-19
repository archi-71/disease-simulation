package simulation.disease;

import java.util.HashSet;

import simulation.params.DiseaseParams;
import simulation.population.Individual;
import simulation.population.Population;

public class Disease {

    private DiseaseParams parameters;
    private Individual[] individuals;

    private int susceptibleNum;
    private int infectiousNum;
    private int recoveredNum;
    private int deceasedNum;

    public int getSusceptibleNum() {
        return susceptibleNum;
    }

    public int getInfectiousNum() {
        return infectiousNum;
    }

    public int getRecoveredNum() {
        return recoveredNum;
    }

    public int getDeceasedNum() {
        return deceasedNum;
    }

    public Disease(DiseaseParams params, Population population) {
        parameters = params;
        individuals = population.getIndividuals();
        reset();
    }

    public void reset() {
        infectiousNum = Math.min(individuals.length, parameters.getInitialInfected());
        for (int i = 0; i < infectiousNum; i++) {
            individuals[i].setHealth(Health.INFECTIOUS);
        }
        susceptibleNum = individuals.length - infectiousNum;
        recoveredNum = 0;
        deceasedNum = 0;
    }

    public void step(int time, double deltaTime) {
        for (Individual individual : individuals) {
            if (individual.getHealth() == Health.INFECTIOUS) {
                if (Math.random() < parameters.getRecoveryRate()) {
                    individual.setHealth(Health.RECOVERED);
                    infectiousNum--;
                    recoveredNum++;
                    return;
                }
                if (Math.random() < parameters.getMortalityRate()) {
                    individual.setHealth(Health.DECEASED);
                    infectiousNum--;
                    deceasedNum++;
                    return;
                }
                HashSet<Individual> contacts = individual.getContacts();
                for (Individual contact : contacts) {
                    if (contact.getHealth() == Health.SUSCEPTIBLE) {
                        if (Math.random() < parameters.getTransmissionRate()) {
                            contact.setHealth(Health.INFECTIOUS);
                            susceptibleNum--;
                            infectiousNum++;
                        }
                    }
                }
            }
        }
    }
}
