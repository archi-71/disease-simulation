package simulation;

import org.locationtech.jts.geom.Geometry;

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

    public String toString() {
        return "Building " + id + " (" + type + ")";
    }
}
