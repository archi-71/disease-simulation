package simulation.environment;

import java.util.HashSet;
import java.util.Set;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

public class Node {

    protected Geometry geometry;

    private HashSet<Node> neighbours;
    private int componentID;
    
    public Set<Node> getNeighbours() {
        return neighbours;
    }

    public Node() {
        this.neighbours = new HashSet<>();
        this.componentID = -1;
    }

    public Node(Geometry geometry) {
        this();
        this.geometry = geometry;
    }

    public Point getCentre() {
        return geometry.getCentroid();
    }

    public Point getPoint() {
        return getCentre();
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
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
