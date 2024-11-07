package simulation;

public class SimulationParams {
    private EnvironmentParams environmentParams;

    public EnvironmentParams getEnvironmentParams() {
        return environmentParams;
    }

    public SimulationParams() {
        environmentParams = new EnvironmentParams();
    };

    public SimulationParams(SimulationParams params) {
        environmentParams = params.environmentParams;
    }
}
