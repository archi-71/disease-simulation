package simulation.population;

import java.util.ArrayList;
import java.util.List;

import simulation.environment.Building;
import simulation.environment.BuildingType;
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
        List<Building> homes = environment.getBuildings(BuildingType.RESIDENTIAL);

        List<Building> workplaces = new ArrayList<Building>();
        workplaces.addAll(environment.getBuildings(BuildingType.SCHOOL));
        workplaces.addAll(environment.getBuildings(BuildingType.UNIVERSITY));
        workplaces.addAll(environment.getBuildings(BuildingType.HOSPITAL));
        workplaces.addAll(environment.getBuildings(BuildingType.ESSENTIAL_AMENITY));
        workplaces.addAll(environment.getBuildings(BuildingType.ESSENTIAL_WORKPLACE));
        workplaces.addAll(environment.getBuildings(BuildingType.NON_ESSENTIAL_AMENITY));
        workplaces.addAll(environment.getBuildings(BuildingType.NON_ESSENTIAL_WORKPLACE));

        individuals = new Individual[parameters.getPopulationSize()];
        for (int i = 0; i < individuals.length; i++) {
            Building home = homes.get((int) (Math.random() * homes.size()));
            Building work = workplaces.get((int) (Math.random() * homes.size()));
            individuals[i] = new Individual(home, work);
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
