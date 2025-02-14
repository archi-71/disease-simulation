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
    private boolean dirty;

    public InterventionParams() {
        dirty = true;
    }

    public InterventionParams(InterventionParams params) {
        interventions.clear();
        for (InterventionParam intervention : params.interventions) {
            interventions.add(new InterventionParam(intervention));
        }
        dirty = params.dirty;
    }

    public List<InterventionParam> getInterventions() {
        return interventions;
    }

    public boolean isDirty() {
        for (InterventionParam intervention : interventions) {
            if (intervention.isDirty()) {
                dirty = true;
            }
        }
        return dirty;
    }

    public void clean() {
        dirty = false;
        for (InterventionParam intervention : interventions) {
            intervention.clean();
        }
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
        addButton.setMinWidth(Region.USE_PREF_SIZE);
        addButton.setOnAction(addEvent -> {
            InterventionParam intervention = new InterventionParam(interventionChoice.getValue());
            interventions.add(intervention);
            dirty = true;
            Button removeButton = new Button("Remove");
            removeButton.setMinWidth(Region.USE_PREF_SIZE);
            removeButton.setAlignment(Pos.CENTER);
            removeButton.setOnAction(removeEvent -> {
                int index = interventions.indexOf(intervention);
                interventions.remove(index);
                interventionInputs.getChildren().remove(index);
                dirty = true;
            });
            TitledPane titledPane = (TitledPane) intervention.getInputUI();
            VBox interventionBox = new VBox(titledPane.getContent(), removeButton);
            interventionBox.setAlignment(Pos.CENTER);
            titledPane.setContent(interventionBox);
            interventionInputs.getChildren().add(titledPane);
        });

        HBox addInterventionInput = new HBox(interventionChoice, addButton);
        addInterventionInput.getStyleClass().add("add-intervention");
        addInterventionInput.setAlignment(Pos.CENTER);

        TitledPane titledPane = new TitledPane("Interventions", new VBox(interventionInputs, addInterventionInput));
        titledPane.getStyleClass().add("big-titled-pane");
        return titledPane;
    }
}
