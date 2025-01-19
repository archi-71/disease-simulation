package simulation.ui;

import javafx.scene.paint.Color;
import java.awt.Rectangle;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import simulation.core.Simulation;
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

public class Map extends Pane {

    private final int resolution = 2048;
    private final int individualSize = 10;
    private final double zoomSpeed = 0.0008;

    private Simulation simulation;

    private Canvas environmentCanvas;
    private Canvas populationCanvas;
    private MapContent mapContent;

    private double width;
    private double height;
    private double centreX;
    private double centreY;

    private double baseDragX;
    private double baseDragY;
    private double dragStartX;
    private double dragStartY;
    private double focusX = 0.5;
    private double focusY = 0.5;
    private double scaleFactor = 1.0;
    private double maxZoom = 2;
    private double minZoom;

    public Map(Simulation simulation) {
        this.simulation = simulation;
        environmentCanvas = new Canvas(resolution, resolution);
        populationCanvas = new Canvas(resolution, resolution);
        getChildren().addAll(environmentCanvas, populationCanvas);
    }

    public void initialise() {
        drawEnvironment();
        drawPopulation();
        resetMap();
        initialiseControls();
    }

    public void drawPopulation() {
        GraphicsContext graphicsContext = populationCanvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, resolution, resolution);
        for (Individual individual : simulation.getPopulation().getIndividuals()) {
            double x = resolution / 2 + resolution * (individual.getPosition().getX() - centreX) / width;
            double y = resolution / 2 - resolution * (individual.getPosition().getY() - centreY) / height;
            graphicsContext.setFill(Color.web(individual.getHealth().getState().getColour()));
            graphicsContext.fillOval(x - individualSize / 2, y - individualSize / 2, individualSize, individualSize);
            graphicsContext.setStroke(Color.BLACK);
            graphicsContext.strokeOval(x - individualSize / 2, y - individualSize / 2, individualSize, individualSize);
        }
    }

    private void drawEnvironment() {
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

        Style roadStyle = SLD.createLineStyle(java.awt.Color.GRAY, 4);
        FeatureLayer roadLayer = new FeatureLayer(gisLoader.getRoadFeatures(), roadStyle);
        mapContent.addLayer(roadLayer);

        width = mapContent.getMaxBounds().getWidth();
        height = mapContent.getMaxBounds().getHeight();
        centreX = mapContent.getMaxBounds().getCenterX();
        centreY = mapContent.getMaxBounds().getCenterY();

        GraphicsContext graphicsContext = environmentCanvas.getGraphicsContext2D();
        Rectangle mapRect = mapContent.getViewport().getScreenArea();
        mapRect.setSize(resolution, resolution);
        StreamingRenderer draw = new StreamingRenderer();
        draw.setMapContent(mapContent);
        FXGraphics2D graphics = new FXGraphics2D(graphicsContext);
        draw.paint(graphics, mapRect, mapContent.getViewport().getBounds());
    }

    private Style createBuildingStyle(String fillColour, String outlineColour) {
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();

        Fill fill = styleFactory.createFill(
                filterFactory.literal(fillColour),
                filterFactory.literal(1.0));

        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(outlineColour),
                filterFactory.literal(2));

        PolygonSymbolizer polygonSymbolizer = styleFactory.createPolygonSymbolizer(stroke, fill, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(polygonSymbolizer);

        FeatureTypeStyle featureTypeStyle = styleFactory.createFeatureTypeStyle();
        featureTypeStyle.rules().add(rule);

        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(featureTypeStyle);
        return style;
    }

    private void resetMap() {
        // Reset scale
        scaleFactor = Math.max(getWidth(), getHeight()) / resolution;
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
        environmentCanvas.setTranslateX(getWidth() / 2 - focusX * resolution);
        environmentCanvas.setTranslateY(getHeight() / 2 - focusY * resolution);
        populationCanvas.setTranslateX(getWidth() / 2 - focusX * resolution);
        populationCanvas.setTranslateY(getHeight() / 2 - focusY * resolution);

        minZoom = Math.min(getWidth(), getHeight()) / resolution;
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
                focusX = (getWidth() / 2 - environmentCanvas.getTranslateX()) / resolution;
                focusY = (getHeight() / 2 - environmentCanvas.getTranslateY()) / resolution;
            }
        });

        // Reset map on double click
        addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    resetMap();
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
                            - (environmentCanvas.getTranslateX() + resolution * (1 - scaleFactor) / 2)
                                    / scaleFactor);
                    double oldMouseY = (event.getY() / scaleFactor
                            - (environmentCanvas.getTranslateY() + resolution * (1 - scaleFactor) / 2)
                                    / scaleFactor);

                    // Scale map
                    scaleFactor = Math.max(minZoom, Math.min(maxZoom, scaleFactor * (1 +
                            zoomSpeed * deltaY)));
                    environmentCanvas.setScaleX(scaleFactor);
                    environmentCanvas.setScaleY(scaleFactor);
                    populationCanvas.setScaleX(scaleFactor);
                    populationCanvas.setScaleY(scaleFactor);

                    // Get new mouse position
                    double newMouseX = (event.getX() / scaleFactor
                            - (environmentCanvas.getTranslateX() + resolution * (1 - scaleFactor) / 2)
                                    / scaleFactor);
                    double newMouseY = (event.getY() / scaleFactor
                            - (environmentCanvas.getTranslateY() + resolution * (1 - scaleFactor) / 2)
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
                    focusX = (getWidth() / 2 - environmentCanvas.getTranslateX()) / resolution;
                    focusY = (getHeight() / 2 - environmentCanvas.getTranslateY()) / resolution;

                    event.consume();
                }
            }
        });
    }
}
