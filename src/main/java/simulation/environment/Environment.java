package simulation.environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.index.quadtree.Quadtree;

import javafx.util.Pair;
import simulation.core.InitialisationException;
import simulation.params.EnvironmentParams;

/**
 * Class representing the environment of the simulation
 */
public class Environment {

    // Distance threshold between road nodes for clustering
    private static final double ROAD_CLUSTER_DISTANCE = 0.0003;

    // Distance threshold for connecting buildings to the road network
    private static final double BUILDING_CONNECT_DISTANCE = 0.001;

    private EnvironmentParams parameters;
    private GISLoader gisLoader;

    // Maps of buildings by component ID for each type
    private HashMap<Integer, List<Building>> homeMap;
    private HashMap<Integer, List<Building>> schoolMap;
    private HashMap<Integer, List<Building>> universityMap;
    private HashMap<Integer, List<Hospital>> hospitalMap;
    private HashMap<Integer, List<Building>> workplaceMap;
    private HashMap<Integer, List<Building>> amenityMap;
    private HashMap<Integer, List<Building>> nonEssentialMap;

    // Thread-safe cache for routes between pairs of buildings
    private ConcurrentHashMap<Pair<Building, Building>, List<Node>> routeCache;

    /**
     * Get the environment parameters
     * 
     * @return Environment parameters
     */
    public EnvironmentParams getParameters() {
        return parameters;
    }

    /**
     * Get the GIS loader
     * 
     * @return GIS loader
     */
    public GISLoader getGISLoader() {
        return gisLoader;
    }

    /**
     * Compile a list of all residential buildings in the environment
     * 
     * @return List of residential buildings
     */
    public List<Building> getHomes() {
        List<Building> homes = new ArrayList<Building>();
        for (List<Building> homeList : homeMap.values()) {
            for (Building home : homeList) {
                homes.add(home);
            }
        }
        Collections.shuffle(homes);
        return homes;
    }

    /**
     * Compile a list of all schools in the environment
     * 
     * @return List of schools
     */
    public List<Building> getSchools() {
        List<Building> schools = new ArrayList<Building>();
        for (List<Building> schoolList : schoolMap.values()) {
            for (Building school : schoolList) {
                schools.add(school);
            }
        }
        Collections.shuffle(schools);
        return schools;
    }

    /**
     * Compile a list of all universities in the environment
     * 
     * @return List of universities
     */
    public List<Building> getUniversities() {
        List<Building> universities = new ArrayList<Building>();
        for (List<Building> universityList : universityMap.values()) {
            for (Building university : universityList) {
                universities.add(university);
            }
        }
        Collections.shuffle(universities);
        return universities;
    }

    /**
     * Compile a list of all non-essential workplaces in the environment
     * 
     * @return List of non-essential workplaces
     */
    public List<Building> getNonEssentialWorkplaces() {
        List<Building> nonEssentialWorkplaces = new ArrayList<Building>();
        for (List<Building> nonEssentialList : nonEssentialMap.values()) {
            for (Building nonEssential : nonEssentialList) {
                nonEssentialWorkplaces.add(nonEssential);
            }
        }
        Collections.shuffle(nonEssentialWorkplaces);
        return nonEssentialWorkplaces;
    }

    /**
     * Get a random school in a given component
     * 
     * @param componentID Component's ID
     * @return Random school in the component
     */
    public Building getRandomSchool(int componentID) {
        List<Building> schools = schoolMap.get(componentID);
        if (schools == null) {
            return null;
        }
        return schools.get((int) (Math.random() * schools.size()));
    }

    /**
     * Get a random university in a given component
     * 
     * @param componentID Component's ID
     * @return Random university in the component
     */
    public Building getRandomUniversity(int componentID) {
        List<Building> universities = universityMap.get(componentID);
        if (universities == null) {
            return null;
        }
        return universities.get((int) (Math.random() * universities.size()));
    }

    /**
     * Get a random hospital in a given component
     * 
     * @param componentID Component's ID
     * @return Random hospital in the component
     */
    public Hospital getRandomHospital(int componentID) {
        List<Hospital> hospitals = hospitalMap.get(componentID);
        if (hospitals == null) {
            return null;
        }
        return hospitals.get((int) (Math.random() * hospitals.size()));
    }

    /**
     * Get a random workplace in a given component
     * 
     * @param componentID Component's ID
     * @return Random workplace in the component
     */
    public Building getRandomWorkplace(int componentID) {
        List<Building> workplaces = workplaceMap.get(componentID);
        if (workplaces == null) {
            return null;
        }
        return workplaces.get((int) (Math.random() * workplaces.size()));
    }

    /**
     * Get a random amenity in a given component
     * 
     * @param componentID Component's ID
     * @return Random amenity in the component
     */
    public Building getRandomAmenity(int componentID) {
        List<Building> amenities = amenityMap.get(componentID);
        if (amenities == null) {
            return null;
        }
        return amenities.get((int) (Math.random() * amenities.size()));
    }

    /**
     * Initialise the environment, building the graph representation
     * 
     * @param params The environment parameters
     * @throws InitialisationException If the GIS data cannot be loaded
     */
    public void initialise(EnvironmentParams params) throws InitialisationException {
        this.parameters = params;

        if (parameters.getBuildingsFile().isDirty() || parameters.getRoadsFile().isDirty()) {
            // Load GIS data
            gisLoader = new GISLoader();
            if (!gisLoader.loadBuildings(parameters.getBuildingsFile().getFile())) {
                throw new InitialisationException("Buildings shapefile could not be loaded");
            }
            if (!gisLoader.loadRoads(parameters.getRoadsFile().getFile())) {
                throw new InitialisationException("Roads shapefile could not be loaded");
            }

            // Create graph of buildings connected by the road network
            buildGraph();
        }

        // Assign hospital capacities, weighted by their area
        double totalArea = 0;
        for (List<Hospital> hospitals : hospitalMap.values()) {
            for (Hospital hospital : hospitals) {
                totalArea += hospital.getArea();
            }
        }
        for (List<Hospital> hospitals : hospitalMap.values()) {
            for (Hospital hospital : hospitals) {
                int capacity = (int) (hospital.getArea() / totalArea * parameters.getHospitalCapacity().getValue());
                hospital.setCapacity(capacity);
            }
        }

        // Initialise the route cache
        routeCache = new ConcurrentHashMap<>();
    }

    /**
     * Get the route between two nodes in the network
     * 
     * @param start Start node
     * @param end   End node
     * @return
     */
    public List<Node> getRoute(Node start, Node end) {
        if (start instanceof Building && end instanceof Building) {
            // Check if the route is cached, and return it if so
            Pair<Building, Building> key = new Pair<Building, Building>((Building) start, (Building) end);
            if (routeCache.containsKey(key)) {
                return routeCache.get(key);
            }

            // Check if the reverse route is cached, and return the reverse if so
            Pair<Building, Building> reverseKey = new Pair<Building, Building>((Building) end, (Building) start);
            if (routeCache.containsKey(reverseKey)) {
                List<Node> route = new ArrayList<>(routeCache.get(reverseKey));
                Collections.reverse(route);
                return route;
            }

            // Otherwise, compute the route and add it to the cache
            List<Node> route = findRoute(start, end);
            routeCache.put(key, route);
            return route;
        }
        return findRoute(start, end);
    }

    /**
     * Compute the route between two nodes using the A* search algorithm
     * 
     * @param start Start node
     * @param end   End node
     * @return
     */
    public List<Node> findRoute(Node start, Node end) {
        // Initialise data structures
        Set<Node> visited = new HashSet<>();
        HashMap<Node, Node> cameFrom = new HashMap<>();
        HashMap<Node, Double> gScore = new HashMap<>();
        HashMap<Node, Double> fScore = new HashMap<>();
        PriorityQueue<Node> frontier = new PriorityQueue<>(
                Comparator.comparingDouble(node -> fScore.getOrDefault(node, Double.POSITIVE_INFINITY)));
        gScore.put(start, 0.0);
        fScore.put(start, distance(start, end));
        frontier.add(start);

        while (!frontier.isEmpty()) {
            // Visit the node which minimises f-score
            Node current = frontier.poll();
            visited.add(current);

            // Retrace path if destination is reached
            if (current.equals(end)) {
                LinkedList<Node> route = new LinkedList<>();
                route.addFirst(current);
                while (cameFrom.containsKey(current)) {
                    current = cameFrom.get(current);
                    route.addFirst(current);
                }
                return route;
            }

            // Iterate over all neighbours
            for (Node neighbour : current.getNeighbours()) {
                if (visited.contains(neighbour))
                    continue;

                // Calculate g-score
                double g = gScore.getOrDefault(current, Double.POSITIVE_INFINITY)
                        + distance(current, neighbour);

                // Ensure g-score is better if already in frontier
                if (!frontier.contains(neighbour))
                    frontier.add(neighbour);
                else if (g >= gScore.getOrDefault(neighbour, Double.POSITIVE_INFINITY))
                    continue;

                // Calculate f-score
                double f = g + distance(neighbour, end);

                // Update path
                cameFrom.put(neighbour, current);
                gScore.put(neighbour, g);
                fScore.put(neighbour, f);
            }
        }
        return null;
    }

    /**
     * Calculate the Euclidean distance between two nodes a and b
     * 
     * @param a First node
     * @param b Second node
     * @return
     */
    private double distance(Node a, Node b) {
        return a.getCentre().distance(b.getCentre());
    }

    private void buildGraph() {
        // Load road features from GIS
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        FeatureIterator<SimpleFeature> iterator = gisLoader.getRoadFeatures().features();

        // Initialise auxillary data structures
        HashMap<Point, Node> pointNodeMap = new HashMap<>();
        HashMap<Node, Integer> nodeCountMap = new HashMap<>();
        Quadtree quadtree = new Quadtree();

        // Iterate over all road segments
        while (iterator.hasNext()) {
            SimpleFeature feature = iterator.next();
            Geometry line = (Geometry) feature.getDefaultGeometry();
            Coordinate[] coordinates = line.getCoordinates();
            Node prev = null;

            // Iterate over all coordinates along the road
            for (Coordinate coordinate : coordinates) {

                // Compute an envelope centred on the coordinate
                Envelope envelope = new Envelope(coordinate);
                envelope.expandBy(ROAD_CLUSTER_DISTANCE);

                // Query the quadtree for points within the envelope
                @SuppressWarnings("unchecked")
                List<Point> points = (List<Point>) quadtree.query(envelope);

                // Find the closest point to the coordinate
                Node node = null;
                double minDistance = ROAD_CLUSTER_DISTANCE;
                for (Point point : points) {
                    double dist = point.getCoordinate().distance(coordinate);
                    if (dist < minDistance) {
                        node = pointNodeMap.get(point);
                        minDistance = dist;
                    }
                }

                // If no point is found, create a new cluster, otherwise update existing cluster
                if (node == null) {
                    // Create new cluster
                    Point point = geometryFactory.createPoint(coordinate);
                    node = new Node(point);

                    // Update quadtree and maps
                    quadtree.insert(point.getEnvelopeInternal(), point);
                    pointNodeMap.put(point, node);
                    nodeCountMap.put(node, 1);
                } else {
                    // Set cluster position to the average of its representative points
                    int count = nodeCountMap.getOrDefault(node, 1);
                    Point oldPoint = node.getPoint();
                    double avgX = (oldPoint.getX() * count + coordinate.x) / (count + 1);
                    double avgY = (oldPoint.getY() * count + coordinate.y) / (count + 1);
                    Point newPoint = geometryFactory.createPoint(new Coordinate(avgX, avgY));
                    node.setGeometry(newPoint);

                    // Update quadtree and maps
                    quadtree.remove(oldPoint.getEnvelopeInternal(), oldPoint);
                    quadtree.insert(newPoint.getEnvelopeInternal(), newPoint);
                    pointNodeMap.remove(oldPoint);
                    pointNodeMap.put(newPoint, node);
                    nodeCountMap.put(node, count + 1);
                }

                // Set consecutive nodes along the road as neighbours
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
        for (Node node : pointNodeMap.values()) {
            // Check if node has been assigned a component
            if (node.getComponentID() == -1) {
                // Perform BFS to find all nodes of the component
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

        // Initialise building maps
        homeMap = new HashMap<>();
        schoolMap = new HashMap<>();
        universityMap = new HashMap<>();
        hospitalMap = new HashMap<>();
        workplaceMap = new HashMap<>();
        amenityMap = new HashMap<>();
        nonEssentialMap = new HashMap<>();

        // Iterate over each building type
        for (BuildingType type : BuildingType.values()) {
            iterator = gisLoader.getBuildingFeatures(type).features();

            // Iterate over all building features of the type
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                Geometry polygon = (Geometry) feature.getDefaultGeometry();

                // Compute an envelope centred on the building
                Envelope envelope = polygon.getEnvelopeInternal();
                envelope.expandBy(BUILDING_CONNECT_DISTANCE);

                // Query the quadtree for road nodes within the envelope
                @SuppressWarnings("unchecked")
                List<Point> points = (List<Point>) quadtree.query(envelope);

                // Find the closest road node to the building
                Node roadNode = null;
                double minDistance = BUILDING_CONNECT_DISTANCE;
                for (Point point : points) {
                    double dist = point.distance(polygon);
                    if (dist < minDistance) {
                        roadNode = pointNodeMap.get(point);
                        minDistance = dist;
                    }
                }

                // If no road node is found, ignore the building
                if (roadNode == null) {
                    continue;
                }

                // Create a new building node and add it as a neighbour to the road node
                Building building;
                if (type == BuildingType.HOSPITAL) {
                    building = new Hospital(polygon);
                } else {
                    building = new Building(polygon, type);
                }
                building.addNeighbour(roadNode);
                roadNode.addNeighbour(building);
                int componentID = roadNode.getComponentID();
                building.setComponentID(componentID);

                // Add the building to the appropriate map based on its type
                switch (type) {
                    case RESIDENTIAL:
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
                        hospitalMap.computeIfAbsent(componentID, k -> new ArrayList<>()).add((Hospital) building);
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
