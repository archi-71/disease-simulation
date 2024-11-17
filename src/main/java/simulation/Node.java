package simulation;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Geometry;

public class Node {

    private Geometry geometry;
    private List<Node> neighbours;

    public Geometry getGeometry() {
        return geometry;
    }
    
    public List<Node> getNeighbours() {
        return neighbours;
    }

    public Node(Geometry geometry) {
        this.geometry = geometry;
        this.neighbours = new ArrayList<>();
    }

    public void addNeighbour(Node neighbour) {
        this.neighbours.add(neighbour);
    }
}
