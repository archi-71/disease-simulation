package simulation;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private List<Node> neighbours;

    public List<Node> getNeighbours() {
        return neighbours;
    }

    public Node() {
        this.neighbours = new ArrayList<>();
    }

    public void addNeighbour(Node neighbour) {
        this.neighbours.add(neighbour);
    }
}
