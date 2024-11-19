package simulation.ui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import simulation.core.Simulation;

public class Controls extends HBox {

    private Label timeDisplayLabel;

    public Controls(Simulation simulation) {

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

        HBox controls = new HBox(10);
        controls.getChildren().addAll(playPauseButton, resetButton);

        HBox timeDisplay = new HBox();
        timeDisplayLabel = new Label();
        timeDisplay.setStyle("-fx-font-size: 20px; -fx-padding: 0 10 0 0;");
        timeDisplay.setAlignment(Pos.CENTER_RIGHT);
        timeDisplay.getChildren().add(timeDisplayLabel);

        HBox.setHgrow(timeDisplay, Priority.ALWAYS);

        getChildren().addAll(controls, timeDisplay);
    }

    public void updateTime(Simulation simulation) {
        Platform.runLater(() -> {
            timeDisplayLabel.setText(formatTime(simulation.getDay(), simulation.getTime()));
        });
    }

    private String formatTime(int day, int milliseconds) {
        String s = "Day " + day + " - ";
        int seconds = milliseconds / 1000;
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        String period = hours >= 12 ? "pm" : "am";
        hours = hours % 12;
        if (hours == 0) {
            hours = 12;
        }
        s += String.format("%02d:%02d %s", hours, minutes, period);
        return s;
    }
}
