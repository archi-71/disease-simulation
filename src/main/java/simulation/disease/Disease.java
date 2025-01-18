package simulation.disease;

import java.util.ArrayList;

import simulation.params.DiseaseParams;
import simulation.population.Individual;
import simulation.population.Population;

public class Disease {

    private DiseaseParams parameters;
    private ArrayList<Individual> individuals;

    // Number of individuals in each state for data collection
    private int susceptibleNum;
    private int exposedNum;
    private int infectiousNum;
    private int asymptomaticNum;
    private int symptomaticMildNum;
    private int symptomaticSevereNum;
    private int recoveredNum;
    private int deceasedNum;

    public int getSusceptibleNum() {
        return susceptibleNum;
    }

    public int getExposedNum() {
        return exposedNum;
    }

    public int getInfectiousNum() {
        return infectiousNum;
    }

    public int getAsymptomaticNum() {
        return asymptomaticNum;
    }

    public int getSymptomaticMildNum() {
        return symptomaticMildNum;
    }

    public int getSymptomaticSevereNum() {
        return symptomaticSevereNum;
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
        for (Individual individual : individuals) {
            Health health = new Health(params, individual);
            individual.setHealth(health);
        }
        reset();
    }

    public void reset() {
        exposedNum = Math.min(individuals.size(), parameters.getInitialInfected().getValue());
        for (int i = 0; i < individuals.size(); i++) {
            Health health = individuals.get(i).getHealth();
            health.reset();
            if (i < exposedNum) {
                health.transition(HealthState.EXPOSED);
            }
        }
        susceptibleNum = individuals.size() - exposedNum;
        infectiousNum = 0;
        asymptomaticNum = 0;
        symptomaticMildNum = 0;
        symptomaticSevereNum = 0;
        recoveredNum = 0;
        deceasedNum = 0;
    }

    public void step(int time, double deltaTime) {
        double deltaTimeDays = deltaTime / 86400000;
        for (Individual individual : individuals) {
            HealthState oldState = individual.getHealth().getState();
            individual.getHealth().update(deltaTimeDays);
            HealthState newState = individual.getHealth().getState();
            if (oldState != newState) {
                switch (newState) {
                    case EXPOSED:
                        susceptibleNum--;
                        exposedNum++;
                        break;
                    case INFECTIOUS:
                        exposedNum--;
                        infectiousNum++;
                        break;
                    case ASYMPTOMATIC:
                        infectiousNum--;
                        asymptomaticNum++;
                        break;
                    case SYMPTOMATIC_MILD:
                        infectiousNum--;
                        symptomaticMildNum++;
                        break;
                    case SYMPTOMATIC_SEVERE:
                        symptomaticMildNum--;
                        symptomaticSevereNum++;
                        break;
                    case RECOVERED:
                        if (oldState == HealthState.ASYMPTOMATIC) {
                            asymptomaticNum--;
                        } else if (oldState == HealthState.SYMPTOMATIC_MILD) {
                            symptomaticMildNum--;
                        } else if (oldState == HealthState.SYMPTOMATIC_SEVERE) {
                            symptomaticSevereNum--;
                        }
                        recoveredNum++;
                        break;
                    case DECEASED:
                        symptomaticSevereNum--;
                        deceasedNum++;
                        break;
                    default:
                }
            }
        }
    }
}
