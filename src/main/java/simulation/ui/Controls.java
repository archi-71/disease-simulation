package simulation.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import simulation.core.Simulation;
import simulation.core.SimulationState;

public class Controls extends HBox {

    private Simulation simulation;

    private Button playPauseButton;
    private Button resetButton;
    private Label speedValueLabel;
    private Slider speedSlider;
    private Label timeDisplayLabel;

    public Controls(Simulation simulation) {
        this.simulation = simulation;
        playPauseButton = new Button("Play");
        playPauseButton.setDisable(true);
        playPauseButton.setOnAction(event -> {
            if (simulation.getState() == SimulationState.INITIALISED
                    || simulation.getState() == SimulationState.PAUSED) {
                simulation.play();
            } else if (simulation.getState() == SimulationState.PLAYING) {
                simulation.pause();
            }
        });

        resetButton = new Button("Reset");
        resetButton.setDisable(true);
        resetButton.setOnAction(event -> {
            playPauseButton.setText("Play");
            simulation.reset();
        });

        Label speedLabel = new Label("Speed: ");
        speedSlider = new Slider(1, 46, 1);
        speedSlider.setMajorTickUnit(1);
        speedSlider.setSnapToTicks(true);
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int val = newValue.intValue();
            speedSlider.setValue(val);
            simulation.setSpeed(getSpeed(val));
            speedValueLabel.setText("x" + simulation.getSpeed());
        });
        speedValueLabel = new Label("x" + simulation.getSpeed());
        HBox speedControls = new HBox(speedLabel, speedSlider, speedValueLabel);

        HBox controls = new HBox(10);
        controls.getChildren().addAll(playPauseButton, resetButton, speedControls);

        HBox timeDisplay = new HBox();
        timeDisplayLabel = new Label();
        timeDisplay.setStyle("-fx-font-size: 20px; -fx-padding: 0 10 0 0;");
        timeDisplay.setAlignment(Pos.CENTER_RIGHT);
        timeDisplay.getChildren().add(timeDisplayLabel);

        HBox.setHgrow(timeDisplay, Priority.ALWAYS);

        getChildren().addAll(controls, timeDisplay);
    }

    public void update() {
        String timeString = formatTime(
                simulation.getDay(),
                simulation.getParameters().getSimulationDuration().getValue(),
                simulation.getTime());

        timeDisplayLabel.setText(timeString);

        switch (simulation.getState()) {
            case UNINITIALISED:
                playPauseButton.setDisable(true);
                resetButton.setDisable(true);
                break;
            case INITIALISED:
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
    }

    private String formatTime(int dayNumber, int totalDays, int dayTime) {
        String s = "Day " + dayNumber + " / " + totalDays + " - ";
        int hours = dayTime / 3600;
        int minutes = (dayTime % 3600) / 60;
        String period = hours >= 12 ? "pm" : "am";
        hours = hours % 12;
        if (hours == 0) {
            hours = 12;
        }
        s += String.format("%02d:%02d %s", hours, minutes, period);
        return s;
    }

    // Get speed using an exponential scale for better control of small speed values
    private int getSpeed(int sliderValue) {
        return (int) (((sliderValue - 1) % 9 + 1) * Math.pow(10, (sliderValue - 1) / 9));
    }
}
