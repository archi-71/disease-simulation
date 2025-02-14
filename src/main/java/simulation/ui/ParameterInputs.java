package simulation.ui;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import simulation.params.SimulationParams;

public class ParameterInputs extends VBox {

    private SimulationParams parameters;

    public ParameterInputs(Stage stage) {
        parameters = new SimulationParams(stage);

        getChildren().addAll(parameters.getInputUI());
    }

    public SimulationParams getParameters() {
        return parameters;
    }
}
