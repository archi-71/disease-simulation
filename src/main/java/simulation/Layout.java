package simulation;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

public class Layout {

    private final double minSplit = 0.1;

    private boolean isResizing = false;
    private double verticalSplit = 0.3;
    private double horizontalSplit = 0.6;

    public Scene createScene() {
        // Default window size to full screen dimensions
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        double width = screen.getWidth();
        double height = screen.getHeight();

        // Create parameters section
        VBox parametersSection = new VBox();
        parametersSection.setStyle("-fx-background-color: lightgrey;");
        parametersSection.setMinWidth(width * minSplit);

        Label parametersTitle = new Label("Parameters");
        parametersSection.getChildren().addAll(parametersTitle);

        // Create controls section
        HBox controlsSection = new HBox();
        controlsSection.setPrefHeight(50);

        Label controlsTitle = new Label("Controls");
        controlsSection.getChildren().addAll(controlsTitle);

        // Create map section
        Pane mapSection = new Pane();
        mapSection.setMinHeight(height * minSplit);

        Label mapTitle = new Label("Map");
        mapSection.getChildren().addAll(mapTitle);

        // Create graph section
        Pane graphSection = new Pane();
        graphSection.setMinHeight(height * minSplit);

        Label graphTitle = new Label("Graph");
        graphSection.getChildren().addAll(graphTitle);

        // Combine map and graph sections into output section
        SplitPane outputSection = new SplitPane(mapSection, graphSection);
        outputSection.setOrientation(Orientation.VERTICAL);
        outputSection.setDividerPositions(horizontalSplit);

        // Combine controls and output sections into right section
        VBox rightSection = new VBox(controlsSection, outputSection);
        VBox.setVgrow(outputSection, Priority.ALWAYS);

        // Combine parameters and right sections into layout
        SplitPane layout = new SplitPane(parametersSection, rightSection);
        layout.setDividerPositions(verticalSplit);

        Scene scene = new Scene(layout, width, height);

        // Update divider positions when changed by the user
        layout.getDividers().get(0).positionProperty().addListener((obs, oldPos, newPos) -> {
            if (!isResizing) {
                verticalSplit = layout.getDividerPositions()[0];
            }
        });
        outputSection.getDividers().get(0).positionProperty().addListener((obs, oldPos, newPos) -> {
            if (!isResizing) {
                horizontalSplit = outputSection.getDividerPositions()[0];
            }
        });

        // Check for screen resize to adjust minimum dimensions and divider positions
        layout.widthProperty().addListener((obs, oldVal, newVal) -> {
            parametersSection.setMinWidth(scene.getWidth() * minSplit);
            layout.setDividerPositions(verticalSplit);
        });
        outputSection.heightProperty().addListener((obs, oldVal, newVal) -> {
            mapSection.setMinWidth(scene.getWidth() * minSplit);
            mapSection.setMinHeight(scene.getHeight() * minSplit);
            graphSection.setMinWidth(scene.getWidth() * minSplit);
            graphSection.setMinHeight(scene.getHeight() * minSplit);
            outputSection.setDividerPositions(horizontalSplit);
        });

        // Check when screen is being resized
        scene.addPreLayoutPulseListener(() -> {
            isResizing = true;
            Platform.runLater(() -> {
                isResizing = false;
            });
        });

        return scene;
    }
}
