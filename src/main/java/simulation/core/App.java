package simulation.core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import simulation.ui.Layout;

public class App extends Application {

    private final double minWidth = 800;
    private final double minHeight = 600;

    @Override
    public void start(Stage stage) {
        stage.setTitle("A Multi-Agent Simulation of Disease Spread");
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);

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