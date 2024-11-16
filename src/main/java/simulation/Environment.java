package simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.index.strtree.ItemBoundable;
import org.locationtech.jts.index.strtree.ItemDistance;
import org.locationtech.jts.index.strtree.STRtree;

public class Environment {

    private EnvironmentParams parameters;
    private GISLoader gisLoader;

    private HashMap<BuildingType, List<Building>> buildings;

    public GISLoader getGISLoader() {
        return gisLoader;
    }

    public Environment(EnvironmentParams params) {
        parameters = params;
        gisLoader = new GISLoader();

        if (!gisLoader.loadBuildings(parameters.getBuildingsFile())) {
            return;
        }
        if (!gisLoader.loadRoads(parameters.getRoadsFile())) {
            return;
        }
        buildGraph();
    }

    private void buildGraph() {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        FeatureIterator<SimpleFeature> iterator = gisLoader.getRoadFeatures().features();
        HashMap<String, Node> roadNodes = new HashMap<>();
        STRtree index = new STRtree();

        while (iterator.hasNext()) {
            SimpleFeature feature = iterator.next();
            MultiLineString line = (MultiLineString) feature.getDefaultGeometry();
            Coordinate[] coordinates = line.getCoordinates();
            Node prev = null;
            for (Coordinate coordinate : coordinates) {
                String key = coordinate.x + "," + coordinate.y;
                Node node = roadNodes.get(key);
                if (node == null) {
                    node = new Node();
                    roadNodes.put(key, node);
                    Point point = geometryFactory.createPoint(coordinate);
                    index.insert(point.getEnvelopeInternal(), point);
                }
                if (prev != null) {
                    node.addNeighbour(prev);
                    prev.addNeighbour(node);
                }
                prev = node;
            }
        }
        iterator.close();

        buildings = new HashMap<>();
        for (BuildingType type : BuildingType.values()) {
            ArrayList<Building> buildingList = new ArrayList<>();
            iterator = gisLoader.getBuildingFeatures(type).features();
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                MultiPolygon polygon = (MultiPolygon) feature.getDefaultGeometry();
                Envelope envelope = polygon.getEnvelopeInternal();
                envelope.expandBy(0.0001);
                Point nearestRoad = (Point) index.nearestNeighbour(envelope, polygon, new ItemDistance() {
                    @Override
                    public double distance(ItemBoundable item1, ItemBoundable item2) {
                        Geometry g1 = (Geometry) item1.getItem();
                        Geometry g2 = (Geometry) item2.getItem();
                        return g1.distance(g2);
                    }
                });
                String key = nearestRoad.getX() + "," + nearestRoad.getY();
                Node node = roadNodes.get(key);
                Building building = new Building(feature.getAttribute("osm_id").toString(), type);
                building.addNeighbour(node);
                node.addNeighbour(building);
                buildingList.add(building);
            }
            iterator.close();
            buildings.put(type, buildingList);
        }
    }
}
