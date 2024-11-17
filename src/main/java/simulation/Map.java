package simulation;

import java.awt.Color;
import java.awt.Rectangle;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import org.jfree.fx.FXGraphics2D;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.style.FeatureTypeStyle;
import org.geotools.api.style.Fill;
import org.geotools.api.style.Font;
import org.geotools.api.style.PointPlacement;
import org.geotools.api.style.PolygonSymbolizer;
import org.geotools.api.style.Rule;
import org.geotools.api.style.Stroke;
import org.geotools.api.style.Style;
import org.geotools.api.style.StyleFactory;
import org.geotools.api.style.TextSymbolizer;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;

public class Map extends Pane {

    private final int resolution = 4096;
    private final int individualSize = 10;
    private final double zoomSpeed = 0.001;

    private Canvas canvas;
    private MapContent mapContent;

    private double baseDragX;
    private double baseDragY;
    private double dragStartX;
    private double dragStartY;
    private double focusX = 0.5;
    private double focusY = 0.5;
    private double scaleFactor = 1.0;
    private double maxZoom = 2;
    private double minZoom;

    public Map() {
        canvas = new Canvas(resolution, resolution);
    }

    public void initialise(Environment environment, Population population) {
        drawEnvironment(environment);
        drawPopulation(population);
        resetMap();
        initialiseControls();
    }

    private void drawEnvironment(Environment environment) {
        GISLoader gisLoader = environment.getGISLoader();

        if (mapContent != null) {
            getChildren().clear();
            mapContent.dispose();
        }

        mapContent = new MapContent();

        for (BuildingType type : BuildingType.values()) {
            Style style = createBuildingStyle(type.getFillColour(), type.getOutlineColour());
            FeatureLayer layer = new FeatureLayer(gisLoader.getBuildingFeatures(type), style);
            mapContent.addLayer(layer);
        }

        Style roadStyle = SLD.createLineStyle(Color.gray, 4);
        FeatureLayer roadLayer = new FeatureLayer(gisLoader.getRoadFeatures(), roadStyle);
        mapContent.addLayer(roadLayer);

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        Rectangle mapRect = mapContent.getViewport().getScreenArea();
        mapRect.setSize(resolution, resolution);
        StreamingRenderer draw = new StreamingRenderer();
        draw.setMapContent(mapContent);
        FXGraphics2D graphics = new FXGraphics2D(graphicsContext);
        draw.paint(graphics, mapRect, mapContent.getViewport().getBounds());

        getChildren().add(canvas);
    }

    private Style createBuildingStyle(String fillColour, String outlineColour) {
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();

        Fill fill = styleFactory.createFill(
                filterFactory.literal(fillColour),
                filterFactory.literal(1.0));

        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(outlineColour),
                filterFactory.literal(2.0));

        PolygonSymbolizer polygonSymbolizer = styleFactory.createPolygonSymbolizer(stroke, fill, null);

        Font font = styleFactory.createFont(
                filterFactory.literal("Arial"),
                filterFactory.literal("normal"),
                filterFactory.literal(
                        "normal"),
                filterFactory.literal(8));
        Fill textFill = styleFactory.createFill(filterFactory.literal("#000000"));
        TextSymbolizer textSymbolizer = styleFactory.createTextSymbolizer(
                textFill,
                new Font[] { font },
                null,
                filterFactory.property("osm_id"),
                null,
                null);

        // textSymbolizer.setPriority(filterFactory.literal(100000));
        // PointPlacement pointPlacement = styleFactory.createPointPlacement(
        // styleFactory.createAnchorPoint(filterFactory.literal(0.5),
        // filterFactory.literal(0.5)),
        // styleFactory.createDisplacement(filterFactory.literal(0),
        // filterFactory.literal(0)),
        // filterFactory.literal(0));
        // textSymbolizer.setLabelPlacement(pointPlacement);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(polygonSymbolizer);
        // rule.symbolizers().add(textSymbolizer);

        FeatureTypeStyle featureTypeStyle = styleFactory.createFeatureTypeStyle();
        featureTypeStyle.rules().add(rule);

        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(featureTypeStyle);
        return style;
    }

    private void drawPopulation(Population population) {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(javafx.scene.paint.Color.BLACK);
        double centreX = mapContent.getMaxBounds().getCenterX();
        double centreY = mapContent.getMaxBounds().getCenterY();
        double width = mapContent.getMaxBounds().getWidth();
        double height = mapContent.getMaxBounds().getHeight();
        for (Individual individual : population.getIndividuals()) {
            double x = resolution / 2 + resolution * (individual.getPosition().getX() - centreX) / width;
            double y = resolution / 2 - resolution * (individual.getPosition().getY() - centreY) / height;
            graphicsContext.fillOval(x - individualSize / 2, y - individualSize / 2, individualSize, individualSize);
        }
    }

    private void resetMap() {
        // Reset scale
        scaleFactor = Math.max(getWidth(), getHeight()) / resolution;
        canvas.setScaleX(scaleFactor);
        canvas.setScaleY(scaleFactor);

        // Reset focus to map centre
        focusX = 0.5;
        focusY = 0.5;
        resizeMap();
    }

    // Update map to keep focus centred when resizing
    public void resizeMap() {
        canvas.setTranslateX(getWidth() / 2 - focusX * resolution);
        canvas.setTranslateY(getHeight() / 2 - focusY * resolution);
        minZoom = Math.min(getWidth(), getHeight()) / resolution;
        scaleFactor = Math.max(minZoom, scaleFactor);
        canvas.setScaleX(scaleFactor);
        canvas.setScaleY(scaleFactor);
    }

    private void initialiseControls() {

        // Record start position on mouse press down
        addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                baseDragX = event.getSceneX();
                baseDragY = event.getSceneY();
                dragStartX = canvas.getTranslateX();
                dragStartY = canvas.getTranslateY();
                event.consume();
            }
        });

        // Pan map on mouse drag
        addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double difX = event.getSceneX() - baseDragX;
                double difY = event.getSceneY() - baseDragY;
                canvas.setTranslateX(dragStartX + difX);
                canvas.setTranslateY(dragStartY + difY);
                focusX = (getWidth() / 2 - canvas.getTranslateX()) / resolution;
                focusY = (getHeight() / 2 - canvas.getTranslateY()) / resolution;
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
                            - (canvas.getTranslateX() + resolution * (1 - scaleFactor) / 2)
                                    / scaleFactor);
                    double oldMouseY = (event.getY() / scaleFactor
                            - (canvas.getTranslateY() + resolution * (1 - scaleFactor) / 2)
                                    / scaleFactor);

                    // Scale map
                    scaleFactor = Math.max(minZoom, Math.min(maxZoom, scaleFactor * (1 +
                            zoomSpeed * deltaY)));
                    canvas.setScaleX(scaleFactor);
                    canvas.setScaleY(scaleFactor);

                    // Get new mouse position
                    double newMouseX = (event.getX() / scaleFactor
                            - (canvas.getTranslateX() + resolution * (1 - scaleFactor) / 2)
                                    / scaleFactor);
                    double newMouseY = (event.getY() / scaleFactor
                            - (canvas.getTranslateY() + resolution * (1 - scaleFactor) / 2)
                                    / scaleFactor);

                    // Translate canvas to centre the scaling on the mouse position
                    canvas.setTranslateX(scaleFactor * (newMouseX - oldMouseX) + canvas.getTranslateX());
                    canvas.setTranslateY(scaleFactor * (newMouseY - oldMouseY) + canvas.getTranslateY());
                    focusX = (getWidth() / 2 - canvas.getTranslateX()) / resolution;
                    focusY = (getHeight() / 2 - canvas.getTranslateY()) / resolution;

                    event.consume();
                }
            }
        });
    }
}
