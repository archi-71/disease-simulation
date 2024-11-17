package simulation;

public class SimulationParams {

    private EnvironmentParams environmentParams;
    private PopulationParams populationParams;

    public EnvironmentParams getEnvironmentParams() {
        return environmentParams;
    }

    public PopulationParams getPopulationParams() {
        return populationParams;
    }

    public SimulationParams() {
        environmentParams = new EnvironmentParams();
        populationParams = new PopulationParams();
    };

    public SimulationParams(SimulationParams params) {
        environmentParams = params.environmentParams;
        populationParams = params.populationParams;
    }
}
