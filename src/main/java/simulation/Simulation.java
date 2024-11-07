package simulation;

public class Simulation {

    private SimulationParams parameters;
    private Environment environment;

    public Environment getEnvironment() {
        return environment;
    }

    public Simulation() {
        parameters = new SimulationParams();
    }

    public void initialise(SimulationParams params) {
        parameters = new SimulationParams(params);
        environment = new Environment(parameters.getEnvironmentParams());
    }
}
