package simulation.params;

import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SimulationParams implements IParam {

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

    public SimulationParams(Stage stage) {
        environmentParams = new EnvironmentParams(stage);
        populationParams = new PopulationParams();
        diseaseParams = new DiseaseParams();
    };

    public SimulationParams(SimulationParams params) {
        environmentParams = params.environmentParams;
        populationParams = params.populationParams;
        diseaseParams = params.diseaseParams;
    }

    public Region getInputUI() {
        VBox container = new VBox(
                environmentParams.getInputUI(),
                populationParams.getInputUI(),
                diseaseParams.getInputUI());
        return container;
    }
}
