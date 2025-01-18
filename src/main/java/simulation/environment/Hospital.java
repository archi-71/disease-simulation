package simulation.environment;

import org.locationtech.jts.geom.Geometry;

public class Hospital extends Building {

    private int capacity;
    private int occupancy;

    public Hospital(Geometry geometry, String id) {
        super(geometry, id, BuildingType.HOSPITAL);
        this.capacity = 0;
        this.occupancy = 0;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void admitPatient() {
        occupancy++;
    }

    public void dischargePatient() {
        occupancy--;
    }

    public boolean isFull() {
        return occupancy >= capacity;
    }

    public double getArea() {
        return geometry.getArea();
    }

    @Override
    public void reset() {
        super.reset();
        occupancy = 0;
    }

}
