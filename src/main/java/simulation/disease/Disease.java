package simulation.disease;

import java.util.ArrayList;
import java.util.Collections;

import simulation.core.Simulation;
import simulation.core.SimulationOutput;
import simulation.interventions.Interventions;
import simulation.params.DiseaseParams;
import simulation.population.Individual;
import simulation.population.Population;

public class Disease {

    private DiseaseParams parameters;
    private Interventions interventions;
    private SimulationOutput output;
    private ArrayList<Individual> individuals;

    private int vaccineNumber;
    private float vaccinatedProportion;

    public Disease(DiseaseParams parameters, Population population, Interventions interventions,
            SimulationOutput output) {
        this.parameters = parameters;
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

    public void step(int dayTime) {
        float timeStepDays = (float) Simulation.timeStep / Simulation.dayLength;
        // Progress disease across individuals
        for (Individual individual : individuals) {
            individual.getHealth().step(timeStepDays);
        }
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
    }
}
