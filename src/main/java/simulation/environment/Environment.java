package simulation.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import simulation.params.EnvironmentParams;

public class Environment {

    private EnvironmentParams parameters;
    private GISLoader gisLoader;

    private List<Building> allHomes;
    private HashMap<Integer, List<Building>> homeMap;
    private HashMap<Integer, List<Building>> schoolMap;
    private HashMap<Integer, List<Building>> universityMap;
    private HashMap<Integer, List<Building>> hospitalMap;
    private HashMap<Integer, List<Building>> workplaceMap;
    private HashMap<Integer, List<Building>> amenityMap;
    private HashMap<Integer, List<Building>> nonEssentialMap;

    public GISLoader getGISLoader() {
        return gisLoader;
    }

    public List<Building> getAllHomes() {
        return allHomes;
    }

    public Building getRandomHome() {
        return allHomes.get((int) (Math.random() * allHomes.size()));
    }

    public Building getRandomHome(int componentID) {
        List<Building> homes = homeMap.get(componentID);
        if (homes == null) {
            return null;
        }
        return homes.get((int) (Math.random() * homes.size()));
    }

    public Building getRandomSchool(int componentID) {
        List<Building> schools = schoolMap.get(componentID);
        if (schools == null) {
            return null;
        }
        return schools.get((int) (Math.random() * schools.size()));
    }

    public Building getRandomUniversity(int componentID) {
        List<Building> universities = universityMap.get(componentID);
        if (universities == null) {
            return null;
        }
        return universities.get((int) (Math.random() * universities.size()));
    }

    public Building getRandomHospital(int componentID) {
        List<Building> hospitals = hospitalMap.get(componentID);
        if (hospitals == null) {
            return null;
        }
        return hospitals.get((int) (Math.random() * hospitals.size()));
    }

    public Building getRandomWorkplace(int componentID) {
        List<Building> workplaces = workplaceMap.get(componentID);
        if (workplaces == null) {
            return null;
        }
        return workplaces.get((int) (Math.random() * workplaces.size()));
    }

    public Building getRandomAmenity(int componentID) {
        List<Building> amenities = amenityMap.get(componentID);
        if (amenities == null) {
            return null;
        }
        return amenities.get((int) (Math.random() * amenities.size()));
    }

    public Building getRandomNonEssential(int componentID) {
        List<Building> nonEssentials = nonEssentialMap.get(componentID);
        if (nonEssentials == null) {
            return null;
        }
        return nonEssentials.get((int) (Math.random() * nonEssentials.size()));
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

        // Build a graph for the road network
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
                    Point point = geometryFactory.createPoint(coordinate);
                    node = new Node(point);
                    roadNodes.put(key, node);
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

        // Compute the connected components of the graph
        int components = 0;
        for (Node node : roadNodes.values()) {
            if (node.getComponentID() == -1) {
                Set<Node> visited = new HashSet<>();
                ArrayList<Node> frontier = new ArrayList<>();
                frontier.add(node);
                while (!frontier.isEmpty()) {
                    Node current = frontier.remove(0);
                    if (visited.contains(current)) {
                        continue;
                    }
                    visited.add(current);
                    current.setComponentID(components);
                    for (Node neighbour : current.getNeighbours()) {
                        frontier.add(neighbour);
                    }
                }
                components++;
            }
        }

        // Add buildings to the graph
        allHomes = new ArrayList<>();
        homeMap = new HashMap<>();
        schoolMap = new HashMap<>();
        universityMap = new HashMap<>();
        hospitalMap = new HashMap<>();
        workplaceMap = new HashMap<>();
        amenityMap = new HashMap<>();
        nonEssentialMap = new HashMap<>();
        for (BuildingType type : BuildingType.values()) {
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
                Node roadNode = roadNodes.get(key);
                Building building = new Building(polygon, feature.getAttribute("osm_id").toString(), type);
                building.addNeighbour(roadNode);
                roadNode.addNeighbour(building);
                int componentID = roadNode.getComponentID();
                building.setComponentID(componentID);
                switch (type) {
                    case RESIDENTIAL:
                        allHomes.add(building);
                        homeMap.computeIfAbsent(componentID, k -> new ArrayList<>()).add(building);
                        break;
                    case SCHOOL:
                        schoolMap.computeIfAbsent(componentID, k -> new ArrayList<>()).add(building);
                        workplaceMap.computeIfAbsent(componentID, k -> new ArrayList<>()).add(building);
                        break;
                    case UNIVERSITY:
                        universityMap.computeIfAbsent(componentID, k -> new ArrayList<>()).add(building);
                        workplaceMap.computeIfAbsent(componentID, k -> new ArrayList<>()).add(building);
                        break;
                    case HOSPITAL:
                        hospitalMap.computeIfAbsent(componentID, k -> new ArrayList<>()).add(building);
                        workplaceMap.computeIfAbsent(componentID, k -> new ArrayList<>()).add(building);
                        break;
                    case ESSENTIAL_AMENITY:
                        amenityMap.computeIfAbsent(componentID, k -> new ArrayList<>()).add(building);
                        workplaceMap.computeIfAbsent(componentID, k -> new ArrayList<>()).add(building);
                        break;
                    case ESSENTIAL_WORKPLACE:
                        workplaceMap.computeIfAbsent(componentID, k -> new ArrayList<>()).add(building);
                        break;
                    case NON_ESSENTIAL_AMENITY:
                        workplaceMap.computeIfAbsent(componentID, k -> new ArrayList<>()).add(building);
                        amenityMap.computeIfAbsent(componentID, k -> new ArrayList<>()).add(building);
                        nonEssentialMap.computeIfAbsent(componentID, k -> new ArrayList<>()).add(building);
                        break;
                    case NON_ESSENTIAL_WORKPLACE:
                        workplaceMap.computeIfAbsent(componentID, k -> new ArrayList<>()).add(building);
                        nonEssentialMap.computeIfAbsent(componentID, k -> new ArrayList<>()).add(building);
                        break;
                }
            }
            iterator.close();
        }
    }
}
