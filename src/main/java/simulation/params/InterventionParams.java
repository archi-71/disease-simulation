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

/**
 * Class to represent intervention parameters
 */
public class InterventionParams implements IParam {

    // List of intervention parameters
    private List<InterventionParam> interventions = new ArrayList<>();

    private boolean dirty;

    /**
     * Construct new interventions parameters
     */
    public InterventionParams() {
        dirty = true;
    }

    /**
     * Clone interventions parameters
     * 
     * @param params Interventions parameters to copy
     */
    public InterventionParams(InterventionParams params) {
        interventions.clear();
        for (InterventionParam intervention : params.interventions) {
            interventions.add(new InterventionParam(intervention));
        }
        dirty = params.dirty;
    }

    /**
     * Add a new intervention parameter
     * 
     * @param intervention Intervention parameter to add
     */
    public List<InterventionParam> getInterventions() {
        return interventions;
    }

    /**
     * Check if parameters have been modified
     * 
     * @return True if parameters have been modified
     */
    public boolean isDirty() {
        for (InterventionParam intervention : interventions) {
            if (intervention.isDirty()) {
                dirty = true;
            }
        }
        return dirty;
    }

    /**
     * Mark parameters as up to date
     */
    public void clean() {
        dirty = false;
        for (InterventionParam intervention : interventions) {
            intervention.clean();
        }
    }

    /**
     * Generate UI to input interventions parameters
     * 
     * @return Pane for interventions parameters input
     */
    public Region getInputUI() {
        VBox interventionInputs = new VBox();
        for (InterventionParam intervention : interventions) {
            interventionInputs.getChildren().add(intervention.getInputUI());
        }

        // Selection box for intervention type
        ChoiceBox<InterventionType> interventionChoice = new ChoiceBox<>();
        interventionChoice.getItems().addAll(InterventionType.values());
        interventionChoice.setValue(InterventionType.values()[0]);

        // Button to add selected intervention to list
        Button addButton = new Button("Add Intervention");
        addButton.setMinWidth(Region.USE_PREF_SIZE);
        addButton.setOnAction(addEvent -> {
            // Add intervention to list
            InterventionParam intervention = new InterventionParam(interventionChoice.getValue());
            interventions.add(intervention);
            dirty = true;

            // Button to remove intervention from list
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
