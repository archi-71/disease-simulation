package simulation.environment;

import org.locationtech.jts.geom.Geometry;

import simulation.core.SimulationOutput;

/**
 * Class to represent hospital nodes, a special subtype of the Building class
 */
public class Hospital extends Building {

    // Maximum number of patients the hospital can accommodate simultaneously
    private int capacity;

    // Number of patients currently in the hospital
    private volatile int occupancy;

    /**
     * Constructs a hospital node
     * @param geometry Geometry of the hospital
     */
    public Hospital(Geometry geometry) {
        super(geometry, BuildingType.HOSPITAL);
        this.capacity = 0;
        this.occupancy = 0;
    }

    /**
     * Set the maximum capacity of the hospital
     * @param capacity Capacity of the hospital
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Add a patient to the hospital
     * @param output Simulation output
     * @return True if the patient was admitted, false if the hospital was full
     */
    public synchronized boolean admitPatient(SimulationOutput output) {
        if (occupancy < capacity) {
            occupancy++;
            output.countHospitalAdmission();
            return true;
        }
        return false;
    }

    /**
     * Discharge a patient from the hospital
     * @param output Simulation output
     * @return True if the patient was discharged, false if the hospital was empty
     */
    public synchronized boolean dischargePatient(SimulationOutput output) {
        if (occupancy > 0) {
            occupancy--;
            output.countHospitalDischarge();
            return true;
        }
        return false;
    }

    /**
     * Get the area of the hospital (for estimating capacity)
     * @return Area of the hospital
     */
    public double getArea() {
        return geometry.getArea();
    }

    /**
     * Reset the hospital for a new run
     */
    @Override
    public void reset() {
        super.reset();
        occupancy = 0;
    }

}
