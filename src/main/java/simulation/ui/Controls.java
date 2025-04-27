package simulation.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import simulation.core.Simulation;
import simulation.core.SimulationState;

/**
 * Class for the UI's top control panel
 */
public class Controls extends HBox {

    private Simulation simulation;

    // UI elements
    private Button playPauseButton;
    private Button resetButton;
    private Label speedValueLabel;
    private Slider speedSlider;
    private CheckBox visualisationToggle;
    private Label runLabel;
    private Label dayLabel;
    private Label timeLabel;

    /**
     * Construct the control panel
     * 
     * @param simulation Simulation
     */
    public Controls(Simulation simulation) {
        this.simulation = simulation;

        // Play/pause button
        playPauseButton = new Button("Play");
        playPauseButton.getStyleClass().add("accent");
        playPauseButton.setMinWidth(Region.USE_PREF_SIZE);
        playPauseButton.setDisable(true);
        playPauseButton.setOnAction(event -> {
            if (simulation.getState() == SimulationState.INITIALISED
                    || simulation.getState() == SimulationState.PAUSED) {
                simulation.play();
            } else if (simulation.getState() == SimulationState.PLAYING) {
                simulation.pause();
            }
            playPauseButton.getParent().requestFocus();
        });

        // Reset button
        resetButton = new Button("Reset");
        resetButton.getStyleClass().add("accent");
        resetButton.setMinWidth(Region.USE_PREF_SIZE);
        resetButton.setDisable(true);
        resetButton.setOnAction(event -> {
            playPauseButton.setText("Play");
            simulation.reset();
        });

        HBox controlButtons = new HBox(playPauseButton, resetButton);
        controlButtons.setAlignment(Pos.CENTER);

        // Speed slider
        Label speedLabel = new Label("Speed");
        speedSlider = new Slider(1, 37, 1);
        speedSlider.setMajorTickUnit(1);
        speedSlider.setSnapToTicks(true);
        speedSlider.setDisable(true);
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int val = newValue.intValue();
            speedSlider.setValue(val);
            simulation.setSpeed(getSpeed(val));
            speedValueLabel.setText("x" + simulation.getSpeed());
        });
        speedValueLabel = new Label("x" + simulation.getSpeed());
        HBox speedControls = new HBox(10, speedLabel, speedSlider, speedValueLabel);
        speedControls.setAlignment(Pos.CENTER);

        // Live visualisation toggle
        visualisationToggle = new CheckBox("Live Visualisation");
        visualisationToggle.setSelected(true);
        visualisationToggle.setDisable(true);

        // Control box
        HBox controls = new HBox(20, controlButtons, speedControls, visualisationToggle);
        controls.setMinWidth(Region.USE_PREF_SIZE);
        controls.setAlignment(Pos.CENTER);

        // Run counter
        runLabel = new Label();
        runLabel.setMinWidth(50);

        // Day counter
        dayLabel = new Label();
        dayLabel.setMinWidth(50);

        // Clock
        timeLabel = new Label();
        timeLabel.setMinWidth(50);

        // Progess display
        HBox progress = new HBox(20, runLabel, dayLabel, timeLabel);
        progress.getStyleClass().add("big-text");
        progress.setMinWidth(Region.USE_PREF_SIZE);
        progress.setAlignment(Pos.CENTER_RIGHT);

        // Control panel
        HBox.setHgrow(progress, Priority.ALWAYS);
        setAlignment(Pos.CENTER);
        getChildren().addAll(controls, progress);
    }

    /**
     * Get the live visualisation toggle
     * 
     * @return Live visualisation checkbox
     */
    public CheckBox getVisualisationToggle() {
        return visualisationToggle;
    }

    /**
     * Update the control panel on simulation state change
     */
    public void update() {
        // Disable/enable controls depending on state
        switch (simulation.getState()) {
            case UNINITIALISED:
                playPauseButton.setDisable(true);
                resetButton.setDisable(true);
                speedSlider.setDisable(true);
                visualisationToggle.setDisable(true);
                break;
            case INITIALISED:
                playPauseButton.setText("Play");
                playPauseButton.setDisable(false);
                resetButton.setDisable(true);
                speedSlider.setDisable(false);
                visualisationToggle.setDisable(false);
            case PAUSED:
                playPauseButton.setText("Play");
                playPauseButton.setDisable(false);
                resetButton.setDisable(false);
                break;
            case PLAYING:
                playPauseButton.setText("Pause");
                playPauseButton.setDisable(false);
                resetButton.setDisable(false);
                break;
            case FINISHED:
                playPauseButton.setText("Play");
                playPauseButton.setDisable(true);
                resetButton.setDisable(false);
                break;
        }

        // Update run label
        int runs = simulation.getParameters().getRuns().getValue();
        runLabel.setText("Run " + Math.min(simulation.getRun() + 1, runs) + " / " + runs);

        // Update day label
        int days = simulation.getParameters().getDuration().getValue();
        dayLabel.setText("Day " + Math.min(simulation.getDay() + 1, days) + " / " + days);

        // Update time label
        timeLabel.setText(formatTime(simulation.getTime()));
    }

    /**
     * Format time in HH:MM am/pm format
     * 
     * @param time Time in seconds since midnight
     * @return Formatted time string
     */
    private String formatTime(int time) {
        int hours = time / 3600;
        int minutes = (time % 3600) / 60;
        String period = hours >= 12 ? "pm" : "am";
        hours = hours % 12;
        if (hours == 0) {
            hours = 12;
        }
        return String.format("%02d:%02d %s", hours, minutes, period);
    }

    /**
     * Get the speed from the slider value
     * Uses an exponential scale for better control of small speed values
     * 
     * @param sliderValue Slider value
     * @return Speed value
     */
    private int getSpeed(int sliderValue) {
        return (int) (((sliderValue - 1) % 9 + 1) * Math.pow(10, (sliderValue - 1) / 9));
    }
}
