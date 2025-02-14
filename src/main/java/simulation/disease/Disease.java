package simulation.disease;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;

import simulation.core.InitialisationException;
import simulation.core.Simulation;
import simulation.core.SimulationOutput;
import simulation.interventions.Interventions;
import simulation.params.DiseaseParams;
import simulation.population.AgeGroup;
import simulation.population.Individual;
import simulation.population.Population;

public class Disease {

    private DiseaseParams parameters;
    private Interventions interventions;
    private SimulationOutput output;
    private ArrayList<Individual> individuals;

    private int vaccineNumber;
    private float vaccinatedProportion;

    public void initialise(DiseaseParams params, Population population, Interventions interventions,
            SimulationOutput output) throws InitialisationException {
        // Validate disease parameters
        for (AgeGroup ageGroup : AgeGroup.values()) {
            float symptomaticProbability = params.getSymptomaticProbability().getValue(ageGroup).getValue();
            float severeSymptomaticProbability = params.getSevereSymptomaticProbability().getValue(ageGroup).getValue();
            float mortalityProbability = params.getMortalityProbability().getValue(ageGroup).getValue();
            if (symptomaticProbability < severeSymptomaticProbability) {
                throw new InitialisationException(
                        "The symptomatic probability cannot be less than the severe symptomatic probability for each age group");
            }
            if (severeSymptomaticProbability < mortalityProbability) {
                throw new InitialisationException(
                        "The severe symptomatic probability cannot be less than the mortality probability for each age group");
            }
        }

        this.parameters = params;
        this.interventions = interventions;
        this.output = output;
        individuals = population.getIndividuals();
        for (Individual individual : individuals) {
            Health health = new Health(parameters, interventions, output, individual);
            individual.setHealth(health);
        }
        reset();
    }

    public void reset() {
        output.setSusceptibleNum(individuals.size());
        int exposedNum = Math.min(individuals.size(), parameters.getInitialInfected().getValue());
        Collections.shuffle(individuals);
        for (int i = 0; i < individuals.size(); i++) {
            Health health = individuals.get(i).getHealth();
            health.reset();
            if (i < exposedNum) {
                health.transition(HealthState.EXPOSED);
                output.countSusceptibleToExposed();
            }
        }
        Collections.shuffle(individuals);
        vaccineNumber = 0;
        vaccinatedProportion = 0;
    }

    public void step(ScheduledExecutorService scheduler, int dayTime) {
        float timeStepDays = (float) Simulation.TIME_STEP / Simulation.DAY_LENGTH;

        // Administer vaccinations if applicable
        if (interventions.isVaccinationActive()) {
            if (vaccineNumber < interventions.getVaccineNumber()) {
                vaccineNumber = interventions.getVaccineNumber();
                vaccinatedProportion = 0;
            }
            float newProportion = Math.min(1, vaccinatedProportion
                    + interventions.getVaccinationRate() / interventions.getVaccinationCompliance()
                            * timeStepDays);
            for (int i = (int) (vaccinatedProportion * individuals.size()); i < (int) (newProportion
                    * individuals.size()); i++) {
                if (i < individuals.size()) {
                    individuals.get(i).getHealth().vaccinate();
                }
            }
            vaccinatedProportion = newProportion;
        }

        // Progress disease for each individual
        List<Callable<Void>> tasks = new ArrayList<>();
        for (Individual individual : individuals) {
            tasks.add(() -> {
                individual.getHealth().step(timeStepDays);
                return null;
            });
        }
        try {
            scheduler.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
