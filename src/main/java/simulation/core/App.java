package simulation.core;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import simulation.ui.Layout;

/**
 * Main class for the JavaFX application
 */
public class App extends Application {

    // Minimum window dimensions
    private static final double MIN_WIDTH = 800;
    private static final double MIN_HEIGHT = 600;

    /**
     * Entry point of JavaFX app.
     * Set up window and initialise simulation & interface.
     */
    @Override
    public void start(Stage stage) {
        // Set up window
        stage.setTitle("A Multi-Agent Simulation of Disease Spread");
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);

        // Set theme from AtlantaFX styles
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        // Initialise simulation and UI
        Simulation simulation = new Simulation();
        Layout layout = new Layout(simulation, stage);
        stage.setScene(layout.getScene());
        stage.show();

        // Stop simulation on window closure
        stage.onCloseRequestProperty().set(e -> {
            if (simulation.getState() == SimulationState.PLAYING) {
                simulation.pause();
            }
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * Launch JavaFX app
     * 
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }
}