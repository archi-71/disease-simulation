package simulation.params;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import simulation.interventions.InterventionType;

public class InterventionParams implements IParam {

    private List<InterventionParam> interventions = new ArrayList<>();

    public List<InterventionParam> getInterventions() {
        return interventions;
    }

    public InterventionParams() {

    }

    public InterventionParams(InterventionParams params) {
        interventions = params.getInterventions();
    }

    public Region getInputUI() {
        VBox interventionInputs = new VBox();
        for (InterventionParam intervention : interventions) {
            interventionInputs.getChildren().add(intervention.getInputUI());
        }

        ChoiceBox<InterventionType> interventionChoice = new ChoiceBox<>();
        interventionChoice.getItems().addAll(InterventionType.values());
        interventionChoice.setValue(InterventionType.values()[0]);
        Button addButton = new Button("Add Intervention");
        addButton.setOnAction(addEvent -> {
            InterventionParam intervention = new InterventionParam(interventionChoice.getValue());
            interventions.add(intervention);
            Button removeButton = new Button("Remove");
            removeButton.setOnAction(removeEvent -> {
                int index = interventions.indexOf(intervention);
                interventions.remove(index);
                interventionInputs.getChildren().remove(index);
            });
            interventionInputs.getChildren().add(new VBox(removeButton, intervention.getInputUI()));
        });

        HBox addInterventionInput = new HBox(interventionChoice, addButton);
        addInterventionInput.setAlignment(Pos.CENTER);

        TitledPane titledPane = new TitledPane("Interventions", new VBox(interventionInputs, addInterventionInput));
        return titledPane;
    }
}
