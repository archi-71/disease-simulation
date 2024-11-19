package simulation.ui;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import simulation.disease.Disease;

public class Data extends VBox {

    private Label susceptibleNumLabel;
    private Label infectiousNumLabel;
    private Label recoveredNumLabel;
    private Label deceasedNumLabel;

    public Data() {
        susceptibleNumLabel = new Label();
        infectiousNumLabel = new Label();
        recoveredNumLabel = new Label();
        deceasedNumLabel = new Label();

        getChildren().addAll(susceptibleNumLabel, infectiousNumLabel, recoveredNumLabel, deceasedNumLabel);
    }

    public void updateData(Disease disease) {
        Platform.runLater(() -> {
            susceptibleNumLabel.setText("Susceptible: " + disease.getSusceptibleNum());
            infectiousNumLabel.setText("Infectious: " + disease.getInfectiousNum());
            recoveredNumLabel.setText("Recovered: " + disease.getRecoveredNum());
            deceasedNumLabel.setText("Deceased: " + disease.getDeceasedNum());
        });
    }

}
