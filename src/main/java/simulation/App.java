package simulation;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    private final double minWidth = 800;
    private final double minHeight = 600;

    @Override
    public void start(Stage stage) {

        stage.setTitle("A Multi-Agent Simulation for Disease Spread");
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);

        Layout layout = new Layout();
        stage.setScene(layout.createScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}