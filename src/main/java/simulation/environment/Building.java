package simulation.environment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import simulation.population.Individual;

/**
 * Class to represent building nodes in the environment's graph
 */
public class Building extends Node {

    private BuildingType type;

    // Whether the building is closed for some intervention
    private boolean closed;

    // The occupants of the building, partitioned into 'rooms'
    private List<HashSet<Individual>> rooms;

    /**
     * Construct a building node
     * 
     * @param geometry Geometry of the building
     * @param type     Type of the building
     */
    public Building(Geometry geometry, BuildingType type) {
        super(geometry);
        this.type = type;

        // Create one room by default
        rooms = new ArrayList<>();
        rooms.add(new HashSet<>());
    }

    /**
     * Get the type of the building
     * 
     * @return Type of the building
     */
    public BuildingType getType() {
        return type;
    }

    /**
     * Get whether the building is closed
     * 
     * @return True if the building is closed
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Set whether the building is closed
     * 
     * @param closed True if the building is closed
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    /**
     * Set the number of rooms in the building
     * 
     * @param roomNum Number of rooms
     */
    public void setRooms(int roomNum) {
        rooms.clear();
        for (int i = 0; i < roomNum; i++) {
            rooms.add(new HashSet<>());
        }
    }

    /**
     * Get the index of a random room in the building
     * 
     * @return Index of a random room
     */
    public int getRandomRoom() {
        return (int) (Math.random() * rooms.size());
    }

    /**
     * Get the occupants of a given room in the building
     * 
     * @param room Index of the room
     * @return Set of occupants in the room
     */
    public HashSet<Individual> getOccupants(int room) {
        return rooms.get(room);
    }

    /**
     * Add an occupant to a given room in the building
     * 
     * @param individual Individual to add
     * @param room       Index of the room
     */
    public synchronized void addOccupant(Individual individual, int room) {
        rooms.get(room).add(individual);
    }

    /**
     * Remove an occupant from a given room in the building
     * 
     * @param individual Individual to remove
     * @param room       Index of the room
     */
    public synchronized void removeOccupant(Individual individual, int room) {
        rooms.get(room).remove(individual);
    }

    /**
     * Check if the building is essential (for intervention simulation)
     * 
     * @return True if the building is essential
     */
    public boolean isEssential() {
        return type == BuildingType.ESSENTIAL_AMENITY || type == BuildingType.ESSENTIAL_WORKPLACE;
    }

    /**
     * Reset the building for a new run
     */
    public void reset() {
        closed = false;
        for (HashSet<Individual> room : rooms) {
            room.clear();
        }
    }

    /**
     * Get a random point within the building for an individual to be positioned at
     */
    @Override
    public Point getPoint() {
        Geometry envelope = geometry.getEnvelope();
        double minX = envelope.getCoordinates()[0].x;
        double maxX = envelope.getCoordinates()[2].x;
        double minY = envelope.getCoordinates()[0].y;
        double maxY = envelope.getCoordinates()[2].y;

        // Generate random coordinates until a point is found within the building
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
