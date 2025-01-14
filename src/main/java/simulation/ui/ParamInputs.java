package simulation.ui;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import simulation.params.SimulationParams;

public class ParamInputs extends VBox {

    private SimulationParams parameters;
    private EnvironmentParamInputs environmentInputs;
    private PopulationParamInputs populationInputs;
    private DiseaseParamInputs diseaseInputs;

    public ParamInputs(Stage stage) {
        parameters = new SimulationParams();

        this.environmentInputs = new EnvironmentParamInputs(parameters.getEnvironmentParams(), stage);
        environmentInputs.setPadding(new javafx.geometry.Insets(10));
        TitledPane environmentPane = new TitledPane("Environment", environmentInputs);
        getChildren().add(environmentPane);

        this.populationInputs = new PopulationParamInputs(parameters.getPopulationParams());
        populationInputs.setPadding(new javafx.geometry.Insets(10));
        TitledPane populationPane = new TitledPane("Population", populationInputs);
        getChildren().addAll(populationPane);

        this.diseaseInputs = new DiseaseParamInputs(parameters.getDiseaseParams());
        diseaseInputs.setPadding(new javafx.geometry.Insets(10));
        TitledPane diseasePane = new TitledPane("Disease", diseaseInputs);
        getChildren().add(diseasePane);
    }

    public SimulationParams getParameters() {
        return parameters;
    }

    public EnvironmentParamInputs getEnvironmentInputs() {
        return environmentInputs;
    }

    public PopulationParamInputs getPopulationInputs() {
        return populationInputs;
    }

    public DiseaseParamInputs getDiseaseInputs() {
        return diseaseInputs;
    }
}
