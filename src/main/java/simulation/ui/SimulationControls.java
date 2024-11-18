package simulation.ui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import simulation.core.Simulation;

public class SimulationControls extends HBox {

    private Label timeDisplayLabel;

    public SimulationControls(Simulation simulation) {

        Button playPauseButton = new Button("Play");
        playPauseButton.setOnAction(event -> {
            if (simulation.isPaused()) {
                playPauseButton.setText("Pause");
                simulation.play();
            } else {
                playPauseButton.setText("Play");
                simulation.pause();
            }
        });

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(event -> {
            playPauseButton.setText("Play");
            simulation.reset();
        });

        HBox controls = new HBox();
        controls.getChildren().addAll(playPauseButton, resetButton);

        HBox timeDisplay = new HBox();
        timeDisplayLabel = new Label();
        timeDisplay.getChildren().add(timeDisplayLabel);

        setAlignment(Pos.CENTER);
        getChildren().addAll(controls, timeDisplay);
    }

    public void updateTime(int day, int milliseconds) {
        Platform.runLater(() -> {
            timeDisplayLabel.setText(formatTime(day, milliseconds));
        });
    }

    private String formatTime(int day, int milliseconds) {
        String s = "Day " + day + " - ";
        int seconds = milliseconds / 1000;
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        s += String.format("%02d:%02d", hours, minutes);
        return s;
    }
}
