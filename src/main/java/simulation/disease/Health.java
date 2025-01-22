package simulation.disease;

import java.util.HashSet;

import simulation.core.SimulationOutput;
import simulation.params.DiseaseParams;
import simulation.population.Activity;
import simulation.population.AgeGroup;
import simulation.population.Individual;

public class Health {

    private DiseaseParams params;
    private SimulationOutput output;
    private Individual individual;
    private HealthState state;
    private float daysInState;
    private float daysInHospital;

    private float symptomaticProbability;
    private float severeSymptomaticProbability;
    private float mortalityProbability;
    private float exposedToInfectiousPeriod;
    private float infectiousToSymptomaticPeriod;
    private float mildToSevereSymptomaticPeriod;
    private float severeSymptomaticToDeathPeriod;
    private float asymptomaticToRecoveredPeriod;
    private float mildSymptomaticToRecoveredPeriod;
    private float severeSymptomaticToRecoveredPeriod;
    private float randomVal;

    public Health(DiseaseParams params, SimulationOutput output, Individual individual) {
        this.output = output;
        this.params = params;
        this.individual = individual;
        reset();
    }

    public DiseaseParams getParams() {
        return params;
    }

    public HealthState getState() {
        return state;
    }

    public void transition(HealthState newState) {
        state = newState;
        daysInState = 0;

    }

    public void update(float timeStepDays) {
        daysInState += timeStepDays;
        switch (state) {
            case SUSCEPTIBLE:
                HashSet<Individual> contacts = individual.getContacts();
                for (Individual contact : contacts) {
                    if (contact.getHealth().getState().isInfectious()) {
                        if (Math.random() < 1
                                - Math.exp(-params.getTransmissionRate().getValue() * timeStepDays * 24)) {
                            transition(HealthState.EXPOSED);
                            output.countSusceptibleToExposed();
                            break;
                        }
                    }
                }
                break;
            case EXPOSED:
                if (daysInState >= exposedToInfectiousPeriod) {
                    transition(HealthState.INFECTIOUS);
                    output.countExposedToInfectious();
                }
                break;
            case INFECTIOUS:
                if (daysInState >= infectiousToSymptomaticPeriod) {
                    if (randomVal < symptomaticProbability) {
                        transition(HealthState.SYMPTOMATIC_MILD);
                        output.countInfectiousToSymptomaticMild();
                    } else {
                        transition(HealthState.ASYMPTOMATIC);
                        output.countInfectiousToAsymptomatic();
                    }
                }
                break;
            case ASYMPTOMATIC:
                if (daysInState >= asymptomaticToRecoveredPeriod) {
                    transition(HealthState.RECOVERED);
                    output.countAsymptomaticToRecovered();
                }
                break;
            case SYMPTOMATIC_MILD:
                if (randomVal < severeSymptomaticProbability) {
                    if (daysInState >= mildToSevereSymptomaticPeriod) {
                        transition(HealthState.SYMPTOMATIC_SEVERE);
                        output.countSymptomaticMildToSymptomaticSevere();
                    }
                } else {
                    if (daysInState >= mildSymptomaticToRecoveredPeriod) {
                        transition(HealthState.RECOVERED);
                        output.countSymptomaticMildToRecovered();
                    }
                }
                break;
            case SYMPTOMATIC_SEVERE:
                float relativeMortality = params.getRelativeMortalityWithoutHospitalisation().getValue();
                if (individual.getActivity() == Activity.HOPSITALISATION) {
                    daysInHospital += timeStepDays;
                    float hospitalFactor = (float) ((daysInState - daysInHospital) / daysInState);
                    relativeMortality = 1
                            + (params.getRelativeMortalityWithoutHospitalisation().getValue() - 1) * hospitalFactor;
                }
                if (randomVal < mortalityProbability * relativeMortality) {
                    if (daysInState >= severeSymptomaticToDeathPeriod) {
                        transition(HealthState.DECEASED);
                        if (individual.getActivity() == Activity.HOPSITALISATION) {
                            individual.getHospital().dischargePatient(output);
                        }
                        output.countSymptomaticSevereToDeceased();
                    }
                } else {
                    if (daysInState >= severeSymptomaticToRecoveredPeriod) {
                        transition(HealthState.RECOVERED);
                        output.countSymptomaticSevereToRecovered();
                    }
                }
                break;
            default:
        }
    }

    public void reset() {
        transition(HealthState.SUSCEPTIBLE);
        daysInHospital = 0;

        // Prepare disease parameters for the individual
        AgeGroup ageGroup = AgeGroup.getAgeGroup(individual.getAge());
        symptomaticProbability = params.getSymptomaticProbability().getValue(ageGroup).getValue();
        severeSymptomaticProbability = params.getSevereSymptomaticProbability().getValue(ageGroup).getValue();
        mortalityProbability = params.getMortalityProbability().getValue(ageGroup).getValue();
        exposedToInfectiousPeriod = params.getExposedToInfectiousPeriod().sample();
        infectiousToSymptomaticPeriod = params.getInfectiousToSymptomaticPeriod().sample();
        mildToSevereSymptomaticPeriod = params.getMildToSevereSymptomaticPeriod().sample();
        severeSymptomaticToDeathPeriod = params.getSevereSymptomaticToDeathPeriod().sample();
        asymptomaticToRecoveredPeriod = params.getAsymptomaticToRecoveredPeriod().sample();
        mildSymptomaticToRecoveredPeriod = params.getMildSymptomaticToRecoveredPeriod().sample();
        severeSymptomaticToRecoveredPeriod = params.getSevereSymptomaticToRecoveredPeriod().sample();
        randomVal = (float) Math.random();
    }
}
