package simulation;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PopulationParamInputs extends VBox {

    public PopulationParamInputs(PopulationParams parameters) {

        Label populationSizeLabel = new Label("Population Size: ");
        TextField populationSizeField = new TextField();
        populationSizeField.setText(String.valueOf(parameters.getPopulationSize()));
        populationSizeField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int populationSize = Integer.parseInt(newValue);
                parameters.setPopulationSize(populationSize);
            } catch (NumberFormatException e) {
                populationSizeField.setText(oldValue);
            }
        });
        HBox populationSizeInput = new HBox(populationSizeLabel, populationSizeField);

        getChildren().add(populationSizeInput);
    }
}
