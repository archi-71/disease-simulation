package simulation;

import org.locationtech.jts.geom.Point;

public class Individual {

    private Point position;
    private Node location;

    private Building home;
    private Building workplace;

    public Point getPosition() {
        return position;
    }

    public void setHome(Building home) {
        this.home = home;
    }

    public void setWorkplace(Building workplace) {
        this.workplace = workplace;
    }

    public Individual(Node startLocation) {
        location = startLocation;
        position = location.getGeometry().getCentroid();
    }
}
