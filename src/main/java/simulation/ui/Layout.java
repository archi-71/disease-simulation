package simulation.ui;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import simulation.core.InitialisationException;
import simulation.core.Simulation;
import simulation.core.SimulationState;
import simulation.params.SimulationParams;

/**
 * Class for the top-level UI layout of the application
 */
public class Layout {

    // Proportion of the screen covered by the overlay dialogue box
    private static final double OVERLAY_SIZE = 0.25;

    // Minimum proportion of the screen occupied for each panel
    private static final double MIN_SPLIT = 0.1;

    // Panel size configuration variables
    private boolean isResizing = false;
    private double verticalSplit = 0.32;
    private double horizontalSplit = 0.5;

    private Simulation simulation;
    private Stage stage;
    private Scene scene;

    // UI components
    private Overlay overlay;
    private ParameterInputs inputParameters;
    private Controls controls;
    private Map map;
    private Data data;

    private ScheduledService<Void> service;

    /**
     * Construct the UI layout
     * 
     * @param simulation Simulation
     * @param stage      Stage reference
     */
    public Layout(Simulation simulation, Stage stage) {
        this.stage = stage;
        this.simulation = simulation;
        createScene();
        scene.getRoot().requestFocus();

        // Set up the simulation state change callback function
        simulation.setStateChangeCallback(() -> {

            // Always show visualisation at the start and end of the simulation
            if (simulation.getState() == SimulationState.INITIALISED && simulation.getRun() == 0
                    || simulation.getState() == SimulationState.FINISHED) {
                Platform.runLater(() -> {
                    map.setVisualisation(true);
                    data.setVisualisation(true);
                });
            }

            // Reset data when simulation is initialised
            if (simulation.getState() == SimulationState.INITIALISED) {
                Platform.runLater(() -> {
                    data.reset();
                });
            }

            // Begin update service if simulation is played, otherwise cancel it
            if (simulation.getState() == SimulationState.PLAYING) {
                service = new ScheduledService<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                updateUI();
                                return null;
                            }
                        };
                    }
                };
                service.setPeriod(javafx.util.Duration.ZERO);
                Platform.runLater(() -> {
                    if (service.getState() == ScheduledService.State.READY) {
                        service.start();
                    }
                });
            } else if (service != null) {
                Platform.runLater(() -> {
                    if (service.isRunning()) {
                        service.cancel();
                    }
                });
            }

            // Update UI on state change
            updateUI();

            // Revert visualisation option to user preference
            map.setVisualisation(controls.getVisualisationToggle().isSelected());
            data.setVisualisation(controls.getVisualisationToggle().isSelected());
        });

        // Update live visualisation when toggled
        controls.getVisualisationToggle().selectedProperty().addListener((obs, wasVisualising, isVisualising) -> {
            map.setVisualisation(isVisualising);
            data.setVisualisation(isVisualising);
            updateUI();
        });
    }

    /**
     * Get the scene for the layout
     * 
     * @return Scene
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Update all UI components
     */
    private void updateUI() {
        Platform.runLater(() -> {
            controls.update();
            map.update();
            data.update();
        });
    }

    /**
     * Create application scene, defining the UI layout
     */
    private void createScene() {
        // Default window size to full screen dimensions
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        double width = screen.getWidth();
        double height = screen.getHeight();

        // Create overlay for displaying loading/error messages
        overlay = new Overlay(width * OVERLAY_SIZE, height * OVERLAY_SIZE);

        // Create parameters panel
        VBox parametersSection = new VBox();
        parametersSection.setMinWidth(width * MIN_SPLIT);
        inputParameters = new ParameterInputs(stage);
        ScrollPane parameters = new ScrollPane(inputParameters);
        parameters.setFitToWidth(true);
        VBox.setVgrow(parameters, Priority.ALWAYS);

        // Create button to initialise simulation
        Button initialiseSimButton = new Button("Initialise Simulation");
        initialiseSimButton.getStyleClass().add("accent");
        initialiseSimButton.setMinWidth(Region.USE_PREF_SIZE);
        initialiseSimButton.setOnAction(event -> {
            SimulationParams params = inputParameters.getParameters();
            if (params.isDirty()) {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Platform.runLater(() -> {
                            overlay.showLoading("Initialising Simulation...");
                        });
                        try {
                            simulation.initialise(params);
                            if (params.getEnvironmentParams().getBuildingsFile().isDirty()
                                    || params.getEnvironmentParams().getRoadsFile().isDirty()) {
                                map.initialise();
                            }
                            params.clean();
                            Platform.runLater(() -> {
                                map.draw();
                                overlay.hide();
                            });
                        } catch (InitialisationException e) {
                            Platform.runLater(() -> {
                                overlay.showError(e.getMessage());
                            });
                        }
                        return null;
                    }
                };
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
                thread.setUncaughtExceptionHandler((t, e) -> {
                    System.out.println("Uncaught exception: " + e.getMessage());
                    Platform.runLater(() -> {
                        overlay.showError(e.getMessage());
                    });
                });
            }
        });
        HBox initialiseSim = new HBox(initialiseSimButton);
        initialiseSim.setAlignment(Pos.CENTER);

        parametersSection.getChildren().addAll(parameters, initialiseSim);

        // Create controls panel
        controls = new Controls(simulation);
        VBox controlsSection = new VBox();
        controlsSection.setAlignment(Pos.CENTER);
        controlsSection.getChildren().add(controls);

        // Create map panel
        map = new Map(simulation);
        Pane mapSection = new Pane(map);
        mapSection.setMinHeight(height * MIN_SPLIT);
        map.prefWidthProperty().bind(mapSection.widthProperty());
        map.prefHeightProperty().bind(mapSection.heightProperty());

        // Create data panel
        data = new Data(stage, simulation);
        VBox dataSection = new VBox();
        dataSection.setMinHeight(height * MIN_SPLIT);
        dataSection.getChildren().add(data);

        // Combine map and data panels into output panel
        SplitPane outputSection = new SplitPane(mapSection, dataSection);
        outputSection.setOrientation(Orientation.VERTICAL);
        outputSection.setDividerPositions(horizontalSplit);

        // Combine controls and output panels into right panel
        VBox rightSection = new VBox(controlsSection, outputSection);
        VBox.setVgrow(outputSection, Priority.ALWAYS);

        // Combine parameters and right panels into the full layout
        SplitPane layout = new SplitPane(parametersSection, rightSection);
        layout.setDividerPositions(verticalSplit);

        // Combine layout and overlay into the root, and create the scene
        StackPane root = new StackPane(layout, overlay);
        scene = new Scene(root, width, height);

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
            parametersSection.setMinWidth(scene.getWidth() * MIN_SPLIT);
            layout.setDividerPositions(verticalSplit);
            map.resizeMap();
        });
        outputSection.heightProperty().addListener((obs, oldVal, newVal) -> {
            mapSection.setMinWidth(scene.getWidth() * MIN_SPLIT);
            mapSection.setMinHeight(scene.getHeight() * MIN_SPLIT);
            dataSection.setMinWidth(scene.getWidth() * MIN_SPLIT);
            dataSection.setMinHeight(scene.getHeight() * MIN_SPLIT);
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

        // Style the scene with CSS
        scene.getStylesheets().add("/simulation/style.css");
    }
}
