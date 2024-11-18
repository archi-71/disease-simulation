package simulation.environment;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

public class Building extends Node {

    private String id;
    private BuildingType type;

    public String getID() {
        return id;
    }

    public BuildingType getType() {
        return type;
    }

    public Building(Geometry geometry, String id, BuildingType type) {
        super(geometry);
        this.id = id;
        this.type = type;
    }

    @Override
    public Point getPoint() {
        Geometry envelope = geometry.getEnvelope();
        double minX = envelope.getCoordinates()[0].x;
        double maxX = envelope.getCoordinates()[2].x;
        double minY = envelope.getCoordinates()[0].y;
        double maxY = envelope.getCoordinates()[2].y;

        double randomX, randomY;
        Point randomPoint;
        do {
            randomX = minX + Math.random() * (maxX - minX);
            randomY = minY + Math.random() * (maxY - minY);
            randomPoint = geometry.getFactory().createPoint(new Coordinate(randomX, randomY));
        } while (!geometry.contains(randomPoint));

        return randomPoint;
    }

    public String toString() {
        return "Building " + id + " (" + type + ")";
    }
}
