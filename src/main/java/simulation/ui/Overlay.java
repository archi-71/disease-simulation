package simulation.ui;

import javafx.scene.control.Button;

import javafx.animation.RotateTransition;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * Class for the UI's loading/error box overlay during initialisation
 */
public class Overlay extends StackPane {

    // Image for the spinner icon
    private static final Image SPINNER_IMAGE = new Image("/simulation/spinner.png");

    // Size of the spinner icon
    private static final double SPINNER_SIZE = 30;

    // Rotation speed of the spinner icon
    private static final double SPINNER_SPEED = 0.5;

    private VBox box;

    /**
     * Construct a new overlay
     * @param boxWidth Dialogue box width
     * @param boxHeight Dialogue box height
     */
    public Overlay(double boxWidth, double boxHeight) {
        VBox background = new VBox();
        background.getStyleClass().add("overlay-background");
        background.setOnMouseClicked(event -> {
            event.consume();
        });

        box = new VBox();
        box.getStyleClass().add("overlay-box");
        box.setMaxWidth(boxWidth);
        box.setMaxHeight(boxHeight);

        getChildren().addAll(background, box);
        setVisible(false);
    }

    /**
     * Show the loading box with a message
     * @param message Error message
     */
    public void showLoading(String message) {
        Label label = new Label(message);
        label.getStyleClass().add("big-text");

        // Set up spinner icon
        ImageView spinner = new ImageView(SPINNER_IMAGE);
        spinner.setSmooth(true);
        spinner.setFitWidth(SPINNER_SIZE);
        spinner.setFitHeight(SPINNER_SIZE);
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1 / SPINNER_SPEED), spinner);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
        rotateTransition.setInterpolator(javafx.animation.Interpolator.LINEAR);
        rotateTransition.play();

        box.getChildren().setAll(label, spinner);
        setVisible(true);
    }

    /**
     * Show the error box with a message
     * @param message Error message
     */
    public void showError(String message) {
        Label titleLabel = new Label("Initialisation Error");
        titleLabel.getStyleClass().add("big-text");

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setMaxWidth(box.getWidth());

        Button button = new Button("Close");
        button.setOnAction(e -> {
            setVisible(false);
        });

        box.getChildren().setAll(titleLabel, messageLabel, button);
        setVisible(true);
    }

    /**
     * Hide the overlay
     */
    public void hide() {
        setVisible(false);
    }
}