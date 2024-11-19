package simulation.ui;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import simulation.params.DiseaseParams;

public class DiseaseParamInputs extends VBox {

    public DiseaseParamInputs(DiseaseParams parameters) {

        Label initialInfectedLabel = new Label("Initial number infected: ");
        TextField initialInfectedField = new TextField();
        initialInfectedField.setText(String.valueOf(parameters.getInitialInfected()));
        initialInfectedField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int initialInfected = Integer.parseInt(newValue);
                parameters.setInitialInfected(initialInfected);
            } catch (NumberFormatException e) {
                initialInfectedField.setText(oldValue);
            }
        });
        HBox initialInfectedInput = new HBox(initialInfectedLabel, initialInfectedField);

        Label transmissionRateLabel = new Label("Transmission rate: ");
        TextField transmissionRateField = new TextField();
        transmissionRateField.setText(String.valueOf(parameters.getTransmissionRate()));
        transmissionRateField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                float transmissionRate = Float.parseFloat(newValue);
                parameters.setTransmissionRate(transmissionRate);
            } catch (NumberFormatException e) {
                transmissionRateField.setText(oldValue);
            }
        });
        HBox transmissionRateInput = new HBox(transmissionRateLabel, transmissionRateField);

        Label recoveryRateLabel = new Label("Recovery rate: ");
        TextField recoveryRateField = new TextField();
        recoveryRateField.setText(String.valueOf(parameters.getRecoveryRate()));
        recoveryRateField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                float recoveryRate = Float.parseFloat(newValue);
                parameters.setRecoveryRate(recoveryRate);
            } catch (NumberFormatException e) {
                recoveryRateField.setText(oldValue);
            }
        });
        HBox recoveryRateInput = new HBox(recoveryRateLabel, recoveryRateField);

        Label mortalityRateLabel = new Label("Mortality rate: ");
        TextField mortalityRateField = new TextField();
        mortalityRateField.setText(String.valueOf(parameters.getMortalityRate()));
        mortalityRateField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                float mortalityRate = Float.parseFloat(newValue);
                parameters.setMortalityRate(mortalityRate);
            } catch (NumberFormatException e) {
                mortalityRateField.setText(oldValue);
            }
        });
        HBox mortalityRateInput = new HBox(mortalityRateLabel, mortalityRateField);

        getChildren().addAll(initialInfectedInput, transmissionRateInput, recoveryRateInput, mortalityRateInput);
    }
}
