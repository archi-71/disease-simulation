package simulation.population;

import java.util.ArrayList;
import java.util.List;

import simulation.environment.Building;
import simulation.environment.Environment;
import simulation.params.PopulationParams;

public class Population {

    private PopulationParams parameters;
    private ArrayList<Individual> individuals;

    public Population(PopulationParams params, Environment environment) {
        parameters = params;
        populateEnvironment(environment);
    }

    public ArrayList<Individual> getIndividuals() {
        return individuals;
    }

    private void populateEnvironment(Environment environment) {
        List<Building> homes = environment.getAllHomes();
        java.util.Collections.shuffle(homes);

        // Generate household sizes
        ArrayList<Integer> households = new ArrayList<>();
        int population = 0;
        while (population < parameters.getPopulationSize().getValue() && households.size() < homes.size()) {
            int householdSize = parameters.getHouseholdSizeDistribution().sample();
            if (population + householdSize > parameters.getPopulationSize().getValue()) {
                householdSize = parameters.getPopulationSize().getValue() - population;
            }
            households.add(householdSize);
            population += householdSize;
        }

        // Populate households
        individuals = new ArrayList<>();
        for (int h = 0; h < households.size(); h++) {
            Building home = homes.get(h);
            int componentID = home.getComponentID();
            for (int i = 0; i < households.get(h); i++) {
                AgeGroup ageGroup = parameters.getAgeDistribution().sample();
                int age = (int) (Math.random() * (ageGroup.getMaxAge() - ageGroup.getMinAge() + 1))
                        + ageGroup.getMinAge();
                Building workplace;
                if (age < 5 || age > 65 || (age >= 18 && Math.random() < parameters.getUnemploymentRate().getValue())) {
                    workplace = null;
                } else if (age < 18) {
                    workplace = environment.getRandomSchool(componentID);
                } else if (age < 25 && Math.random() < parameters.getUniversityEntryRate().getValue()) {
                    workplace = environment.getRandomUniversity(componentID);
                } else {
                    workplace = environment.getRandomWorkplace(componentID);
                }
                individuals.add(new Individual(environment, age, home, workplace));
            }
        }

        java.util.Collections.shuffle(individuals);
    }

    public void step(int dayTime) {
        for (Individual individual : individuals) {
            individual.step(dayTime);
        }
    }

    public void reset() {
        for (Individual individual : individuals) {
            individual.reset();
        }
    }
}
