package simulation.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import simulation.core.Simulation;
import simulation.disease.Disease;

public class Data extends VBox {

    private Simulation simulation;

    private Label susceptibleNumLabel;
    private Label exposedNumLabel;
    private Label infectiousNumLabel;
    private Label asymptomaticLabel;
    private Label symptomaticMildLabel;
    private Label symptomaticSevereLabel;
    private Label recoveredNumLabel;
    private Label deceasedNumLabel;

    public Data(Simulation simulation) {
        this.simulation = simulation;

        susceptibleNumLabel = new Label();
        exposedNumLabel = new Label();
        infectiousNumLabel = new Label();
        asymptomaticLabel = new Label();
        symptomaticMildLabel = new Label();
        symptomaticSevereLabel = new Label();
        recoveredNumLabel = new Label();
        deceasedNumLabel = new Label();

        getChildren().addAll(
                susceptibleNumLabel,
                exposedNumLabel,
                infectiousNumLabel,
                asymptomaticLabel,
                symptomaticMildLabel,
                symptomaticSevereLabel,
                recoveredNumLabel,
                deceasedNumLabel);
    }

    public void update() {
        Disease disease = simulation.getDisease();
        susceptibleNumLabel.setText("Susceptible: " + disease.getSusceptibleNum());
        exposedNumLabel.setText("Exposed: " + disease.getExposedNum());
        infectiousNumLabel.setText("Infectious: " + disease.getInfectiousNum());
        asymptomaticLabel.setText("Asymptomatic: " + disease.getAsymptomaticNum());
        symptomaticMildLabel.setText("Symptomatic (Mild): " + disease.getSymptomaticMildNum());
        symptomaticSevereLabel.setText("Symptomatic (Severe): " + disease.getSymptomaticSevereNum());
        recoveredNumLabel.setText("Recovered: " + disease.getRecoveredNum());
        deceasedNumLabel.setText("Deceased: " + disease.getDeceasedNum());
    }

}
