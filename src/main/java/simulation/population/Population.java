package simulation.population;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;

import simulation.core.InitialisationException;
import simulation.core.SimulationOutput;
import simulation.environment.Building;
import simulation.environment.Environment;
import simulation.params.PopulationParams;

public class Population {

    private PopulationParams parameters;
    private ArrayList<Individual> individuals;

    public ArrayList<Individual> getIndividuals() {
        return individuals;
    }

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

        if (population < parameters.getPopulationSize().getValue()) {
            throw new InitialisationException(
                    "There are not enough residential buildings to accommodate the population");
        }

        // Populate households
        List<Future<Individual>> futures = new ArrayList<>();
        for (int h = 0; h < households.size(); h++) {
            Building home = homes.get(h);
            int componentID = home.getComponentID();
            for (int i = 0; i < households.get(h); i++) {
                futures.add(scheduler.schedule(() -> {
                    AgeGroup ageGroup = parameters.getAgeDistribution().sample();
                    int age = (int) (Math.random() * (ageGroup.getMaxAge() - ageGroup.getMinAge() + 1))
                            + ageGroup.getMinAge();
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
                    Individual individual = new Individual(environment, output, age, home, workplace);
                    return individual;
                }, 0, TimeUnit.MILLISECONDS));
            }
        }
        individuals = new ArrayList<>();
        for (Future<Individual> future : futures) {
            try {
                individuals.add(future.get());
                System.out.print("\rInitialising population: " + individuals.size() + "/" + parameters.getPopulationSize().getValue());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void step(ScheduledExecutorService scheduler, int dayTime) {
        List<Callable<Void>> tasks = new ArrayList<>();
        for (Individual individual : individuals) {
            tasks.add(() -> {
                individual.step(dayTime);
                return null;
            });
        }
        try {
            scheduler.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        for (Individual individual : individuals) {
            individual.reset();
        }
    }
}
