package simulation.core;

import javafx.application.Application;
import javafx.stage.Stage;
import simulation.ui.Layout;

public class App extends Application {

    private final double minWidth = 800;
    private final double minHeight = 600;

    @Override
    public void start(Stage stage) {
        stage.setTitle("A Multi-Agent Simulation for Disease Spread");
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);

        Simulation simulation = new Simulation();
        Layout layout = new Layout(simulation, stage);
        stage.setScene(layout.getScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}