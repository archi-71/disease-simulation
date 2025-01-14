package simulation.environment;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

public class Node {

    protected Geometry geometry;

    private List<Node> neighbours;
    private int componentID;
    
    public List<Node> getNeighbours() {
        return neighbours;
    }

    public Node(Geometry geometry) {
        this.geometry = geometry;
        this.neighbours = new ArrayList<>();
        this.componentID = -1;
    }

    public Point getCentre() {
        return geometry.getCentroid();
    }

    public Point getPoint() {
        return getCentre();
    }

    public void addNeighbour(Node neighbour) {
        this.neighbours.add(neighbour);
    }

    public void setComponentID(int componentID) {
        this.componentID = componentID;
    }

    public int getComponentID() {
        return componentID;
    }
}
