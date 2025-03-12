package simulation.core;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import simulation.ui.Layout;

public class App extends Application {

    private static final double MIN_WIDTH = 800;
    private static final double MIN_HEIGHT = 600;

    @Override
    public void start(Stage stage) {
        stage.setTitle("A Multi-Agent Simulation of Disease Spread");
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);

        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        Simulation simulation = new Simulation();
        Layout layout = new Layout(simulation, stage);
        stage.setScene(layout.getScene());
        stage.show();

        stage.onCloseRequestProperty().set(e -> {
            if (simulation.getState() == SimulationState.PLAYING) {
                simulation.pause();
            }
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}