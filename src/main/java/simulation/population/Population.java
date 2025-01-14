package simulation.population;

import java.util.ArrayList;
import java.util.List;

import simulation.environment.Building;
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
        List<Building> homes = environment.getAllHomes();
        java.util.Collections.shuffle(homes);

        // Generate household sizes
        ArrayList<Integer> households = new ArrayList<>();
        int population = 0;
        while (population < parameters.getPopulationSize() && households.size() < homes.size()) {
            int householdSize = parameters.getHouseholdSizeDistribution().randomSample();
            if (population + householdSize > parameters.getPopulationSize()) {
                householdSize = parameters.getPopulationSize() - population;
            }
            households.add(householdSize);
            population += householdSize;
        }

        // Override population size if there is limited housing
        if (households.size() == homes.size()) {
            parameters.setPopulationSize(population);
        }

        // Populate households
        individuals = new Individual[parameters.getPopulationSize()];
        int index = 0;
        for (int h = 0; h < households.size(); h++) {
            Building home = homes.get(h);
            int componentID = home.getComponentID();
            for (int i = 0; i < households.get(h); i++) {
                AgeGroup ageGroup = parameters.getAgeDistribution().randomSample();
                int age = (int) (Math.random() * (ageGroup.getMaxAge() - ageGroup.getMinAge() + 1))
                        + ageGroup.getMinAge();
                Building workplace;
                if (age < 5 || age > 65 || (age >= 18 && Math.random() < parameters.getUnemploymentRate())) {
                    workplace = null;
                } else if (age < 18) {
                    workplace = environment.getRandomSchool(componentID);
                } else if (age < 25 && Math.random() < parameters.getUniversityEntryRate()) {
                    workplace = environment.getRandomUniversity(componentID);
                } else {
                    workplace = environment.getRandomWorkplace(componentID);
                }
                individuals[index] = new Individual(environment, age, home, workplace);
                index++;
            }
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
