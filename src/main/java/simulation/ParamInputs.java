package simulation;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ParamInputs extends VBox {

    private SimulationParams parameters;

    public SimulationParams getParameters() {
        return parameters;
    }

    public ParamInputs(Stage stage) {
        parameters = new SimulationParams();

        EnvironmentParamInputs environmentInputs = new EnvironmentParamInputs(stage);

        getChildren().addAll(environmentInputs);
    }
}
