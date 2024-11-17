package simulation;

public class Simulation {

    private SimulationParams parameters;

    private Environment environment;
    private Population population;

    public Environment getEnvironment() {
        return environment;
    }

    public Population getPopulation() {
        return population;
    }

    public Simulation() {
        parameters = new SimulationParams();
    }

    public void initialise(SimulationParams params) {
        parameters = new SimulationParams(params);
        environment = new Environment(parameters.getEnvironmentParams());
        population = new Population(parameters.getPopulationParams(), environment);
    }
}
