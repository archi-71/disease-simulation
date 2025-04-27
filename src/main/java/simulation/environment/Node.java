package simulation.environment;

import java.util.HashSet;
import java.util.Set;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

/**
 * Class to represent nodes in the environment's graph
 */
public class Node {

    protected Geometry geometry;

    // The node's neighbouring set in the graph
    private Set<Node> neighbours;

    // Identifier for the node's connected component
    private int componentID;

    /**
     * Construct a new node
     */
    public Node() {
        this.neighbours = new HashSet<>();
        this.componentID = -1;
    }

    /**
     * Construct a new node with a given geometry
     * 
     * @param geometry Geometry of the node
     */
    public Node(Geometry geometry) {
        this();
        this.geometry = geometry;
    }

    /**
     * Set the geometry of the node
     * 
     * @param geometry Geometry of the node
     */
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * Get the centre point of the node's geometry
     * 
     * @return Central point of the node
     */
    public Point getCentre() {
        return geometry.getCentroid();
    }

    /**
     * Get a point inside the node's geometry
     * 
     * @return Point inside the node
     */
    public Point getPoint() {
        return getCentre();
    }

    /**
     * Get the neighbours of the node
     * 
     * @return Set of neighbouring nodes
     */
    public Set<Node> getNeighbours() {
        return neighbours;
    }

    /**
     * Add a neighbour to the node
     * 
     * @param neighbour Neighbouring node
     */
    public void addNeighbour(Node neighbour) {
        this.neighbours.add(neighbour);
    }

    /**
     * Set the node's connected component
     * 
     * @param componentID ID of the connected component
     */
    public void setComponentID(int componentID) {
        this.componentID = componentID;
    }

    /**
     * Get the node's connected component
     * 
     * @return ID of the connected component
     */
    public int getComponentID() {
        return componentID;
    }
}
