package simulation.environment;

import java.util.HashSet;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import simulation.population.Individual;

public class Building extends Node {

    private String id;
    private BuildingType type;
    private HashSet<Individual> occupants;
    private boolean closed;

    public String getID() {
        return id;
    }

    public BuildingType getType() {
        return type;
    }

    public HashSet<Individual> getOccupants() {
        return occupants;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Building(Geometry geometry, String id, BuildingType type) {
        super(geometry);
        this.id = id;
        this.type = type;
        occupants = new HashSet<>();
    }

    public void addOccupant(Individual individual) {
        occupants.add(individual);
    }

    public void removeOccupant(Individual individual) {
        occupants.remove(individual);
    }

    public boolean isEssential() {
        return type == BuildingType.ESSENTIAL_AMENITY || type == BuildingType.ESSENTIAL_WORKPLACE;
    }

    public void reset() {
        occupants.clear();
        closed = false;
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
}
