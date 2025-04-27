package simulation.ui;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import simulation.params.SimulationParams;

/**
 * Class for the UI's left parameter input panel
 */
public class ParameterInputs extends VBox {

    // Parameters as shown in the UI, distinct from current simulation parameters
    private SimulationParams parameters;

    /**
     * Construct the parameter input panel
     * 
     * @param stage Stage reference
     */
    public ParameterInputs(Stage stage) {
        parameters = new SimulationParams(stage);

        getChildren().addAll(parameters.getInputUI());
    }

    /**
     * Get the simulation parameters
     * 
     * @return Simulation parameters
     */
    public SimulationParams getParameters() {
        return parameters;
    }
}
