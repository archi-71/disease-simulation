package simulation;

public class Building extends Node {
    private String id;
    private BuildingType type;

    public String getID() {
        return id;
    }
    
    public BuildingType getType() {
        return type;
    }

    public Building(String id, BuildingType type) {
        this.id = id;
        this.type = type;
    }

    public String toString() {
        return "Building " + id + " (" + type + ")";
    }
}
