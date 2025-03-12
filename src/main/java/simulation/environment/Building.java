package simulation.environment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import simulation.population.Individual;

public class Building extends Node {

    private BuildingType type;
    private boolean closed;
    private List<HashSet<Individual>> rooms;

    public Building(Geometry geometry, BuildingType type) {
        super(geometry);
        this.type = type;
        rooms = new ArrayList<>();
        rooms.add(new HashSet<>());
    }

    public BuildingType getType() {
        return type;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public void setRooms(int roomNum) {
        rooms.clear();
        for (int i = 0; i < roomNum; i++) {
            rooms.add(new HashSet<>());
        }
    }

    public int getRandomRoom() {
        return (int) (Math.random() * rooms.size());
    }   

    public HashSet<Individual> getOccupants(int room) {
        return rooms.get(room);
    }

    public synchronized void addOccupant(Individual individual, int room) {
        rooms.get(room).add(individual);
    }

    public synchronized void removeOccupant(Individual individual, int room) {
        rooms.get(room).remove(individual);
    }

    public boolean isEssential() {
        return type == BuildingType.ESSENTIAL_AMENITY || type == BuildingType.ESSENTIAL_WORKPLACE;
    }

    public void reset() {
        closed = false;
        for (HashSet<Individual> room : rooms) {
            room.clear();
        }
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
