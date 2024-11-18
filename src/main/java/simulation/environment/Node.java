package simulation.environment;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

public class Node {

    protected Geometry geometry;
    private List<Node> neighbours;
    
    public List<Node> getNeighbours() {
        return neighbours;
    }

    public Node(Geometry geometry) {
        this.geometry = geometry;
        this.neighbours = new ArrayList<>();
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
}
