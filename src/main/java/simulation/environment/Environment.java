package simulation.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private HashMap<BuildingType, List<Building>> buildingMap;

    private List<Building> homes;
    private List<Building> schools;
    private List<Building> universities;
    private List<Building> hospitals;
    private List<Building> workplaces;
    private List<Building> amenities;
    private List<Building> nonEssential;

    public GISLoader getGISLoader() {
        return gisLoader;
    }

    public List<Building> getHomes() {
        return homes;
    }

    public List<Building> getSchools() {
        return schools;
    }

    public List<Building> getUniversities() {
        return universities;
    }

    public List<Building> getHospitals() {
        return hospitals;
    }

    public List<Building> getWorkplaces() {
        return workplaces;
    }

    public List<Building> getAmenities() {
        return amenities;
    }

    public List<Building> getNonEssential() {
        return nonEssential;
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

        // Pre-define useful lists of buildings
        homes = buildingMap.get(BuildingType.RESIDENTIAL);
        schools = buildingMap.get(BuildingType.SCHOOL);
        universities = buildingMap.get(BuildingType.UNIVERSITY);
        hospitals = buildingMap.get(BuildingType.HOSPITAL);

        workplaces = new ArrayList<Building>();
        workplaces.addAll(buildingMap.get(BuildingType.SCHOOL));
        workplaces.addAll(buildingMap.get(BuildingType.UNIVERSITY));
        workplaces.addAll(buildingMap.get(BuildingType.HOSPITAL));
        workplaces.addAll(buildingMap.get(BuildingType.ESSENTIAL_AMENITY));
        workplaces.addAll(buildingMap.get(BuildingType.ESSENTIAL_WORKPLACE));
        workplaces.addAll(buildingMap.get(BuildingType.NON_ESSENTIAL_AMENITY));
        workplaces.addAll(buildingMap.get(BuildingType.NON_ESSENTIAL_WORKPLACE));

        amenities = new ArrayList<Building>();
        amenities.addAll(buildingMap.get(BuildingType.ESSENTIAL_AMENITY));
        amenities.addAll(buildingMap.get(BuildingType.NON_ESSENTIAL_AMENITY));

        nonEssential = new ArrayList<Building>();
        nonEssential.addAll(buildingMap.get(BuildingType.NON_ESSENTIAL_AMENITY));
        nonEssential.addAll(buildingMap.get(BuildingType.NON_ESSENTIAL_WORKPLACE));

    }

    public void reset() {
        for (List<Building> buildingList : buildingMap.values()) {
            for (Building building : buildingList) {
                building.reset();
            }
        }
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

        buildingMap = new HashMap<>();
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
                Building building = new Building(polygon, feature.getAttribute("osm_id").toString(), type);
                building.addNeighbour(node);
                node.addNeighbour(building);
                buildingList.add(building);
            }
            iterator.close();
            buildingMap.put(type, buildingList);
        }
    }
}
