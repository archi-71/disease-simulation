package simulation.population;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;

import simulation.core.InitialisationException;
import simulation.core.SimulationOutput;
import simulation.environment.Building;
import simulation.environment.Environment;
import simulation.params.PopulationParams;

/**
 * Class to represent a population of individuals in the simulation
 */
public class Population {

    // Number of distinct amenities assigned to each individual
    private static final int AMENITY_NUM = 5;

    // Maximum number of individuals simultaneously allowed in a 'room'
    private static final int ROOM_SIZE = 8;

    // Required simulation components
    private PopulationParams parameters;
    private List<Individual> individuals;

    /**
     * Get the population parameters
     * 
     * @return Population parameters
     */
    public List<Individual> getIndividuals() {
        return individuals;
    }

    /**
     * Initialise the population
     * 
     * @param params      Population parameters
     * @param environment Environment
     * @param output      Simulation output
     * @param scheduler   Scheduler for multi-threading
     * @throws InitialisationException If parameters are invalid
     */
    public void initialise(PopulationParams params, Environment environment, SimulationOutput output,
            ScheduledExecutorService scheduler)
            throws InitialisationException {
        parameters = params;

        List<Building> homes = environment.getHomes();

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

        // Validate population parameters
        if (population < parameters.getPopulationSize().getValue()) {
            throw new InitialisationException(
                    "There are not enough residential buildings to accommodate the population");
        }

        // Populate households
        ConcurrentHashMap<Building, Integer> publicMaxOccupancies = new ConcurrentHashMap<>();
        List<Future<Individual>> futures = new ArrayList<>();
        for (int h = 0; h < households.size(); h++) {

            // Assign a home to the household
            Building home = homes.get(h);
            int componentID = home.getComponentID();

            for (int i = 0; i < households.get(h); i++) {
                futures.add(scheduler.schedule(() -> {

                    // Generate age of the individual
                    AgeGroup ageGroup = parameters.getAgeDistribution().sample();
                    int age = (int) (Math.random() * (ageGroup.getMaxAge() - ageGroup.getMinAge() + 1))
                            + ageGroup.getMinAge();

                    // Generate occupation and assign a workplace for the individual, if any
                    Building workplace;
                    if (age < 5 || age > 65 ||
                            (age >= 18 && Math.random() < parameters.getUnemploymentRate().getValue()) ||
                            (age < 18 && Math.random() > parameters.getSchoolEntryRate().getValue())) {
                        workplace = null;
                    } else if (age < 18) {
                        workplace = environment.getRandomSchool(componentID);
                    } else if (age < 25 && Math.random() < parameters.getUniversityEntryRate().getValue()) {
                        workplace = environment.getRandomUniversity(componentID);
                    } else {
                        workplace = environment.getRandomWorkplace(componentID);
                    }
                    if (workplace != null) {
                        publicMaxOccupancies.put(workplace, publicMaxOccupancies.getOrDefault(workplace, 0) + 1);
                    }

                    // Assign amenities to the individual to visit in their free time
                    List<Building> amenities = new ArrayList<Building>();
                    for (int j = 0; j < AMENITY_NUM; j++) {
                        Building amenity = environment.getRandomAmenity(componentID);
                        if (amenity != null) {
                            amenities.add(amenity);
                            publicMaxOccupancies.put(amenity, publicMaxOccupancies.getOrDefault(amenity, 0) + 1);
                        }
                    }

                    // Initialise the individual
                    Individual individual = new Individual(environment, output, age, home, workplace, amenities);
                    return individual;

                }, 0, TimeUnit.MILLISECONDS));
            }
        }

        // Wait for all individuals to be created in parallel
        individuals = new ArrayList<>();
        for (Future<Individual> future : futures) {
            try {
                individuals.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Assign rooms to buildings based on their maximum occupancy
        for (Building building : publicMaxOccupancies.keySet()) {
            int roomNum = (int) Math.ceil(publicMaxOccupancies.get(building) / (float) ROOM_SIZE);
            building.setRooms(roomNum);
        }
        publicMaxOccupancies.clear();
    }

    /**
     * Run a single step of the population simulation
     * 
     * @param scheduler Scheduled executor service for multithreading
     * @param dayTime   Current time of day
     */
    public void step(ScheduledExecutorService scheduler, int dayTime) {
        // Initialise a task for each individual
        List<Callable<Void>> tasks = new ArrayList<>();
        for (Individual individual : individuals) {
            tasks.add(() -> {
                individual.step(dayTime);
                return null;
            });
        }

        // Delegate tasks to the scheduler
        try {
            scheduler.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reset the population to its initial state for a new run
     */
    public void reset() {
        for (Individual individual : individuals) {
            individual.reset();
        }
    }
}
