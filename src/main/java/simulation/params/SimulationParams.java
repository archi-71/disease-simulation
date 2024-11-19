package simulation.params;

public class SimulationParams {

    private EnvironmentParams environmentParams;
    private PopulationParams populationParams;
    private DiseaseParams diseaseParams;

    public EnvironmentParams getEnvironmentParams() {
        return environmentParams;
    }

    public PopulationParams getPopulationParams() {
        return populationParams;
    }

    public DiseaseParams getDiseaseParams() {
        return diseaseParams;
    }

    public SimulationParams() {
        environmentParams = new EnvironmentParams();
        populationParams = new PopulationParams();
        diseaseParams = new DiseaseParams();
    };

    public SimulationParams(SimulationParams params) {
        environmentParams = params.environmentParams;
        populationParams = params.populationParams;
        diseaseParams = params.diseaseParams;
    }
}
