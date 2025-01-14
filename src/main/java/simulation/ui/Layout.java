package simulation.ui;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import simulation.core.Simulation;

public class Layout {

    private final double controlsHeight = 50;
    private final double minSplit = 0.1;

    private boolean isResizing = false;
    private double verticalSplit = 0.3;
    private double horizontalSplit = 0.6;

    private Simulation simulation;
    private Stage stage;
    private Scene scene;

    private ParamInputs inputParameters;
    private Controls controls;
    private Map map;
    private Data data;

    public Layout(Simulation simulation, Stage stage) {
        this.stage = stage;
        this.simulation = simulation;
        createScene();
        simulation.setScheduleCallback(() -> {
            controls.updateTime(simulation);
            data.updateData(simulation.getDisease());
            map.drawPopulation(simulation.getPopulation());
        });
    }

    public Scene getScene() {
        return scene;
    }

    private void createScene() {
        // Default window size to full screen dimensions
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        double width = screen.getWidth();
        double height = screen.getHeight();

        // Create parameters section
        VBox parametersSection = new VBox();
        parametersSection.setMinWidth(width * minSplit);

        Label parametersTitle = new Label("Parameters");
        parametersTitle.setStyle("-fx-font-size: 16px;");

        inputParameters = new ParamInputs(stage);
        ScrollPane parameters = new ScrollPane(inputParameters);
        VBox.setVgrow(parameters, Priority.ALWAYS);

        Button initialiseSimButton = new Button("Initialise Simulation");
        initialiseSimButton.setOnAction(event -> {
            simulation.initialise(inputParameters.getParameters());
            inputParameters.getPopulationInputs()
                    .updatePopulationSize(simulation.getParameters().getPopulationParams().getPopulationSize());
            map.initialise(simulation);
        });
        HBox initialiseSim = new HBox(initialiseSimButton);
        initialiseSim.setAlignment(Pos.CENTER);

        parametersSection.getChildren().addAll(parametersTitle, parameters, initialiseSim);

        // Create controls section
        controls = new Controls(simulation);

        VBox controlsSection = new VBox();
        controlsSection.setMinHeight(controlsHeight);

        Label controlsTitle = new Label("Controls");
        controlsTitle.setStyle("-fx-font-size: 16px;");
        controlsSection.getChildren().addAll(controlsTitle, controls);

        // Create map section
        map = new Map();
        Pane mapSection = new Pane(map);
        mapSection.setMinHeight(height * minSplit);
        map.prefWidthProperty().bind(mapSection.widthProperty());
        map.prefHeightProperty().bind(mapSection.heightProperty());

        Label mapTitle = new Label("Map");
        mapTitle.setStyle("-fx-font-size: 16px;");
        mapSection.getChildren().add(mapTitle);

        // Create data section
        data = new Data();

        VBox dataSection = new VBox();
        dataSection.setMinHeight(height * minSplit);

        Label dataTitle = new Label("Data");
        dataTitle.setStyle("-fx-font-size: 16px;");
        dataSection.getChildren().addAll(dataTitle, data);

        // Combine map and data sections into output section
        SplitPane outputSection = new SplitPane(mapSection, dataSection);
        outputSection.setOrientation(Orientation.VERTICAL);
        outputSection.setDividerPositions(horizontalSplit);

        // Combine controls and output sections into right section
        VBox rightSection = new VBox(controlsSection, outputSection);
        VBox.setVgrow(outputSection, Priority.ALWAYS);

        // Combine parameters and right sections into layout
        SplitPane layout = new SplitPane(parametersSection, rightSection);
        layout.setDividerPositions(verticalSplit);

        scene = new Scene(layout, width, height);

        // Update divider positions when changed by the user
        layout.getDividers().get(0).positionProperty().addListener((obs, oldPos, newPos) -> {
            if (!isResizing) {
                verticalSplit = layout.getDividerPositions()[0];
                map.resizeMap();
            }
        });
        outputSection.getDividers().get(0).positionProperty().addListener((obs, oldPos, newPos) -> {
            if (!isResizing) {
                horizontalSplit = outputSection.getDividerPositions()[0];
                map.resizeMap();
            }
        });

        // Check for screen resize to adjust minimum dimensions and divider positions
        layout.widthProperty().addListener((obs, oldVal, newVal) -> {
            parametersSection.setMinWidth(scene.getWidth() * minSplit);
            layout.setDividerPositions(verticalSplit);
            map.resizeMap();
        });
        outputSection.heightProperty().addListener((obs, oldVal, newVal) -> {
            mapSection.setMinWidth(scene.getWidth() * minSplit);
            mapSection.setMinHeight(scene.getHeight() * minSplit);
            dataSection.setMinWidth(scene.getWidth() * minSplit);
            dataSection.setMinHeight(scene.getHeight() * minSplit);
            outputSection.setDividerPositions(horizontalSplit);
            map.resizeMap();
        });

        // Check for full screen mode
        stage.fullScreenProperty().addListener((obs, oldVal, newVal) -> {
            map.resizeMap();
        });

        // Check when screen is being resized
        scene.addPreLayoutPulseListener(() -> {
            isResizing = true;
            Platform.runLater(() -> {
                isResizing = false;
            });
        });
    }
}
