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

    private final int resolution = 4096;
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

    public void initialise(Environment environment) {
        loadMapContent(environment.getGISLoader());
        drawMap();
        resetMap();
        initialiseControls();
    }

    private void loadMapContent(GISLoader gisLoader) {
        if (mapContent != null) {
            getChildren().clear();
            mapContent.dispose();
        }

        mapContent = new MapContent();

        FeatureLayer residentialLayer = new FeatureLayer(gisLoader.getResidentialFeatures(),
                createBuildingStyle("#FF6F6F", "#B13C3C"));
        mapContent.addLayer(residentialLayer);

        FeatureLayer schoolLayer = new FeatureLayer(gisLoader.getSchoolFeatures(),
                createBuildingStyle("#FFA500", "#CC7A00"));
        mapContent.addLayer(schoolLayer);

        FeatureLayer universityLayer = new FeatureLayer(gisLoader.getUniversityFeatures(),
                createBuildingStyle("#FFEB3B", "#C8A900"));
        mapContent.addLayer(universityLayer);

        FeatureLayer hospitalLayer = new FeatureLayer(gisLoader.getHospitalFeatures(),
                createBuildingStyle("#9C27B0", "#6A1B9A"));
        mapContent.addLayer(hospitalLayer);

        FeatureLayer essentialAmenitiesLayer = new FeatureLayer(gisLoader.getEssentialAmenitiesFeatures(),
                createBuildingStyle("#1976D2", "#0D47A1"));
        mapContent.addLayer(essentialAmenitiesLayer);

        FeatureLayer essentialWorkplacesLayer = new FeatureLayer(gisLoader.getEssentialWorkplacesFeatures(),
                createBuildingStyle("#388E3C", "#1B5E20"));
        mapContent.addLayer(essentialWorkplacesLayer);

        FeatureLayer nonEssentialAmenitiesLayer = new FeatureLayer(gisLoader.getNonEssentialAmenitiesFeatures(),
                createBuildingStyle("#4FC3F7", "#0288D1"));
        mapContent.addLayer(nonEssentialAmenitiesLayer);

        FeatureLayer nonEssentialWorkplacesLayer = new FeatureLayer(gisLoader.getNonEssentialWorkplacesFeatures(),
                createBuildingStyle("#8BC34A", "#5A8C2B"));
        mapContent.addLayer(nonEssentialWorkplacesLayer);

        Style roadStyle = SLD.createLineStyle(Color.gray, 4);
        FeatureLayer roadLayer = new FeatureLayer(gisLoader.getRoadFeatures(), roadStyle);
        mapContent.addLayer(roadLayer);
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

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(polygonSymbolizer);

        FeatureTypeStyle featureTypeStyle = styleFactory.createFeatureTypeStyle();
        featureTypeStyle.rules().add(rule);

        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(featureTypeStyle);
        return style;
    }

    private void drawMap() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        Rectangle mapRect = mapContent.getViewport().getScreenArea();
        mapRect.setSize(resolution, resolution);
        StreamingRenderer draw = new StreamingRenderer();
        draw.setMapContent(mapContent);
        FXGraphics2D graphics = new FXGraphics2D(graphicsContext);
        draw.paint(graphics, mapRect, mapContent.getViewport().getBounds());

        getChildren().add(canvas);
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
}
