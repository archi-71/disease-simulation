package simulation.params;

import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SimulationParams implements IParam {

    private IntegerParam simulationDuration = new IntegerParam("Simulation Duration (days)", 10);

    private EnvironmentParams environmentParams;
    private PopulationParams populationParams;
    private DiseaseParams diseaseParams;
    private InterventionParams interventionParams;

    public IntegerParam getSimulationDuration() {
        return simulationDuration;
    }

    public EnvironmentParams getEnvironmentParams() {
        return environmentParams;
    }

    public PopulationParams getPopulationParams() {
        return populationParams;
    }

    public DiseaseParams getDiseaseParams() {
        return diseaseParams;
    }

    public InterventionParams getInterventionParams() {
        return interventionParams;
    }

    public SimulationParams(Stage stage) {
        environmentParams = new EnvironmentParams(stage);
        populationParams = new PopulationParams();
        diseaseParams = new DiseaseParams();
        interventionParams = new InterventionParams();
    };

    public SimulationParams(SimulationParams params) {
        simulationDuration = params.simulationDuration;
        environmentParams = params.environmentParams;
        populationParams = params.populationParams;
        diseaseParams = params.diseaseParams;
        interventionParams = params.interventionParams;
    }

    public Region getInputUI() {
        VBox container = new VBox(
                simulationDuration.getInputUI(),
                environmentParams.getInputUI(),
                populationParams.getInputUI(),
                diseaseParams.getInputUI(),
                interventionParams.getInputUI());
        return container;
    }
}
