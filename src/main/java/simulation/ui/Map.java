package simulation.ui;

import javafx.scene.paint.Color;

import java.awt.Rectangle;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import simulation.core.Simulation;
import simulation.disease.HealthState;
import simulation.environment.BuildingType;
import simulation.environment.GISLoader;
import simulation.population.Individual;

import org.jfree.fx.FXGraphics2D;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.style.FeatureTypeStyle;
import org.geotools.api.style.Fill;
import org.geotools.api.style.PolygonSymbolizer;
import org.geotools.api.style.Rule;
import org.geotools.api.style.Stroke;
import org.geotools.api.style.Style;
import org.geotools.api.style.StyleFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;

public class Map extends StackPane {

    private final int RESOLUTION = 2048;
    private final float ROAD_STROKE_WIDTH = 2f;
    private final float BUILDING_STROKE_WIDTH = 1f;
    private final float INDIVIDUAL_SIZE = 8f;
    private final double ZOOM_SPEED = 0.0008;
    private final double MAX_ZOOM = 2;

    private Simulation simulation;

    private TitledPane legend;
    private Canvas environmentCanvas;
    private Canvas populationCanvas;
    private MapContent mapContent;

    private boolean visualisation;

    private double width;
    private double height;
    private double centreX;
    private double centreY;
    private double baseDragX;
    private double baseDragY;
    private double dragStartX;
    private double dragStartY;
    private double focusX;
    private double focusY;
    private double scaleFactor;
    private double minZoom;

    public Map(Simulation simulation) {
        this.simulation = simulation;

        legend = new TitledPane();
        legend.setVisible(false);
        environmentCanvas = new Canvas(RESOLUTION, RESOLUTION);
        populationCanvas = new Canvas(RESOLUTION, RESOLUTION);
        visualisation = true;

        getChildren().addAll(new Pane(environmentCanvas, populationCanvas), legend);
    }

    public void initialise() {
        initialiseMap();
        resetMap();
        initialiseControls();
        initialiseLegend();
    }

    public void draw() {
        drawEnvironment();
        drawPopulation();
    }

    public void update() {
        drawPopulation();
    }

    public void setVisualisation(boolean visualisation) {
        this.visualisation = visualisation;
    }

    private void initialiseMap() {
        GISLoader gisLoader = simulation.getEnvironment().getGISLoader();

        if (mapContent != null) {
            mapContent.dispose();
        }

        mapContent = new MapContent();

        for (BuildingType type : BuildingType.values()) {
            Style style = createBuildingStyle(type.getFillColour(), type.getOutlineColour());
            FeatureLayer layer = new FeatureLayer(gisLoader.getBuildingFeatures(type), style);
            mapContent.addLayer(layer);
        }

        Style roadStyle = SLD.createLineStyle(java.awt.Color.GRAY, ROAD_STROKE_WIDTH);
        FeatureLayer roadLayer = new FeatureLayer(gisLoader.getRoadFeatures(), roadStyle);
        mapContent.addLayer(roadLayer);

        width = mapContent.getMaxBounds().getWidth();
        height = mapContent.getMaxBounds().getHeight();
        centreX = mapContent.getMaxBounds().getCenterX();
        centreY = mapContent.getMaxBounds().getCenterY();
    }

    private Style createBuildingStyle(String fillColour, String outlineColour) {
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();

        Fill fill = styleFactory.createFill(
                filterFactory.literal(fillColour),
                filterFactory.literal(1.0));

        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(outlineColour),
                filterFactory.literal(BUILDING_STROKE_WIDTH));

        PolygonSymbolizer polygonSymbolizer = styleFactory.createPolygonSymbolizer(stroke, fill, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(polygonSymbolizer);

        FeatureTypeStyle featureTypeStyle = styleFactory.createFeatureTypeStyle();
        featureTypeStyle.rules().add(rule);

        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(featureTypeStyle);
        return style;
    }

    private void drawEnvironment() {
        GraphicsContext gc = environmentCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, RESOLUTION, RESOLUTION);
        Rectangle mapRect = mapContent.getViewport().getScreenArea();
        mapRect.setSize(RESOLUTION, RESOLUTION);
        StreamingRenderer draw = new StreamingRenderer();
        draw.setMapContent(mapContent);
        FXGraphics2D graphics = new FXGraphics2D(gc);
        draw.paint(graphics, mapRect, mapContent.getViewport().getBounds());
    }

    private void drawPopulation() {
        if (!visualisation) {
            return;
        }
        float size = INDIVIDUAL_SIZE / (float) scaleFactor;
        GraphicsContext gc = populationCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, RESOLUTION, RESOLUTION);
        for (Individual individual : simulation.getPopulation().getIndividuals()) {
            if (individual.getHealth() == null) {
                continue;
            }
            double x = RESOLUTION / 2 + RESOLUTION * (individual.getPosition().getX() - centreX) / width;
            double y = RESOLUTION / 2 - RESOLUTION * (individual.getPosition().getY() - centreY) / height;
            gc.setFill(Color.web(individual.getHealth().getState().getColour()));
            gc.fillOval(x - size / 2, y - size / 2, size, size);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(size / 10);
            gc.strokeOval(x - size / 2, y - size / 2, size, size);
        }
    }

    private void resetMap() {
        // Reset scale
        scaleFactor = Math.max(getWidth(), getHeight()) / RESOLUTION;
        environmentCanvas.setScaleX(scaleFactor);
        environmentCanvas.setScaleY(scaleFactor);
        populationCanvas.setScaleX(scaleFactor);
        populationCanvas.setScaleY(scaleFactor);

        // Reset focus to map centre
        focusX = 0.5;
        focusY = 0.5;
        resizeMap();
    }

    // Update map to keep focus centred when resizing
    public void resizeMap() {
        environmentCanvas.setTranslateX(getWidth() / 2 - focusX * RESOLUTION);
        environmentCanvas.setTranslateY(getHeight() / 2 - focusY * RESOLUTION);
        populationCanvas.setTranslateX(getWidth() / 2 - focusX * RESOLUTION);
        populationCanvas.setTranslateY(getHeight() / 2 - focusY * RESOLUTION);

        minZoom = Math.min(getWidth(), getHeight()) / RESOLUTION;
        scaleFactor = Math.max(minZoom, scaleFactor);

        environmentCanvas.setScaleX(scaleFactor);
        environmentCanvas.setScaleY(scaleFactor);
        populationCanvas.setScaleX(scaleFactor);
        populationCanvas.setScaleY(scaleFactor);
    }

    private void initialiseControls() {

        // Record start position on mouse press down
        addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                baseDragX = event.getSceneX();
                baseDragY = event.getSceneY();
                dragStartX = environmentCanvas.getTranslateX();
                dragStartY = environmentCanvas.getTranslateY();
                event.consume();
            }
        });

        // Pan map on mouse drag
        addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double difX = event.getSceneX() - baseDragX;
                double difY = event.getSceneY() - baseDragY;
                environmentCanvas.setTranslateX(dragStartX + difX);
                environmentCanvas.setTranslateY(dragStartY + difY);
                populationCanvas.setTranslateX(dragStartX + difX);
                populationCanvas.setTranslateY(dragStartY + difY);
                focusX = (getWidth() / 2 - environmentCanvas.getTranslateX()) / RESOLUTION;
                focusY = (getHeight() / 2 - environmentCanvas.getTranslateY()) / RESOLUTION;
            }
        });

        // Reset map on double click
        addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    resetMap();
                    drawPopulation();
                }
                event.consume();
            }
        });

        // Scale map on mouse scroll
        addEventHandler(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double deltaY = event.getDeltaY();
                if (deltaY != 0) {
                    // Get old mouse position
                    double oldMouseX = (event.getX() / scaleFactor
                            - (environmentCanvas.getTranslateX() + RESOLUTION * (1 - scaleFactor) / 2)
                                    / scaleFactor);
                    double oldMouseY = (event.getY() / scaleFactor
                            - (environmentCanvas.getTranslateY() + RESOLUTION * (1 - scaleFactor) / 2)
                                    / scaleFactor);

                    // Scale map
                    scaleFactor = Math.max(minZoom, Math.min(MAX_ZOOM, scaleFactor * (1 +
                            ZOOM_SPEED * deltaY)));
                    environmentCanvas.setScaleX(scaleFactor);
                    environmentCanvas.setScaleY(scaleFactor);
                    populationCanvas.setScaleX(scaleFactor);
                    populationCanvas.setScaleY(scaleFactor);

                    // Get new mouse position
                    double newMouseX = (event.getX() / scaleFactor
                            - (environmentCanvas.getTranslateX() + RESOLUTION * (1 - scaleFactor) / 2)
                                    / scaleFactor);
                    double newMouseY = (event.getY() / scaleFactor
                            - (environmentCanvas.getTranslateY() + RESOLUTION * (1 - scaleFactor) / 2)
                                    / scaleFactor);

                    // Translate canvas to centre the scaling on the mouse position
                    environmentCanvas
                            .setTranslateX(scaleFactor * (newMouseX - oldMouseX) + environmentCanvas.getTranslateX());
                    environmentCanvas
                            .setTranslateY(scaleFactor * (newMouseY - oldMouseY) + environmentCanvas.getTranslateY());
                    populationCanvas
                            .setTranslateX(scaleFactor * (newMouseX - oldMouseX) + populationCanvas.getTranslateX());
                    populationCanvas
                            .setTranslateY(scaleFactor * (newMouseY - oldMouseY) + populationCanvas.getTranslateY());
                    focusX = (getWidth() / 2 - environmentCanvas.getTranslateX()) / RESOLUTION;
                    focusY = (getHeight() / 2 - environmentCanvas.getTranslateY()) / RESOLUTION;

                    drawPopulation();

                    event.consume();
                }
            }
        });
    }

    private void initialiseLegend() {
        Platform.runLater(() -> {
            setAlignment(legend, Pos.TOP_RIGHT);
            legend.setText("Legend");
            legend.expandedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    legend.setMaxWidth(Region.USE_PREF_SIZE);
                } else {
                    legend.setMaxWidth(80);
                }
            });

            VBox buildings = new VBox();
            Label buildingsLabel = new Label("Buildings");
            buildingsLabel.getStyleClass().add("bold");
            buildings.getChildren().add(buildingsLabel);
            for (BuildingType type : BuildingType.values()) {
                Label label = new Label(type.getName());

                Canvas symbol = new Canvas(10, 10);
                GraphicsContext gc = symbol.getGraphicsContext2D();
                gc.setFill(Color.web(type.getFillColour()));
                gc.fillRect(0, 0, 10, 10);
                gc.setStroke(Color.web(type.getOutlineColour()));
                gc.setLineWidth(2);
                gc.strokeRect(0, 0, 10, 10);

                HBox key = new HBox(5, symbol, label);
                key.setAlignment(Pos.CENTER_LEFT);
                buildings.getChildren().add(key);
            }

            VBox individuals = new VBox();
            Label individualsLabel = new Label("Individuals");
            individualsLabel.getStyleClass().add("bold");
            individuals.getChildren().add(individualsLabel);
            for (HealthState type : HealthState.values()) {
                Label label = new Label(type.getName());

                Canvas symbol = new Canvas(12, 12);
                GraphicsContext gc = symbol.getGraphicsContext2D();
                gc.setFill(Color.web(type.getColour()));
                gc.fillOval(1, 1, 10, 10);
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                gc.strokeOval(1, 1, 10, 10);

                HBox key = new HBox(5, symbol, label);
                key.setAlignment(Pos.CENTER_LEFT);
                individuals.getChildren().add(key);
            }

            legend.setContent(new HBox(20, buildings, individuals));
            legend.setExpanded(false);
            legend.setVisible(true);
        });
    }
}
