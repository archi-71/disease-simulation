package simulation.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import simulation.params.SimulationParams;

public class ParamInputs extends VBox {

    private SimulationParams parameters;

    public SimulationParams getParameters() {
        return parameters;
    }

    public ParamInputs(Stage stage) {
        parameters = new SimulationParams();

        Label environmentTitle = new Label("Environment");
        environmentTitle.setStyle("-fx-font-size: 16px;");
        EnvironmentParamInputs environmentInputs = new EnvironmentParamInputs(parameters.getEnvironmentParams(), stage);
        getChildren().addAll(environmentTitle, environmentInputs);

        Label populationTitle = new Label("Population");
        populationTitle.setStyle("-fx-font-size: 16px;");
        PopulationParamInputs populationInputs = new PopulationParamInputs(parameters.getPopulationParams());
        getChildren().addAll(populationTitle, populationInputs);

        Label diseaseTitle = new Label("Disease");
        diseaseTitle.setStyle("-fx-font-size: 16px;");
        DiseaseParamInputs diseaseInputs = new DiseaseParamInputs(parameters.getDiseaseParams());
        getChildren().addAll(diseaseTitle, diseaseInputs);
    }
}
