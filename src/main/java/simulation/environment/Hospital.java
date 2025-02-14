package simulation.environment;

import org.locationtech.jts.geom.Geometry;

import simulation.core.SimulationOutput;

public class Hospital extends Building {

    private int capacity;
    private volatile int occupancy;

    public Hospital(Geometry geometry, String id) {
        super(geometry, id, BuildingType.HOSPITAL);
        this.capacity = 0;
        this.occupancy = 0;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public synchronized boolean admitPatient(SimulationOutput output) {
        if (occupancy < capacity) {
            occupancy++;
            output.countHospitalAdmission();
            return true;
        }
        return false;
    }

    public synchronized boolean dischargePatient(SimulationOutput output) {
        if (occupancy > 0) {
            occupancy--;
            output.countHospitalDischarge();
            return true;
        }
        return false;
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
