package simulation.population;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

import simulation.core.Simulation;
import simulation.core.SimulationOutput;
import simulation.disease.Health;
import simulation.disease.HealthState;
import simulation.environment.Building;
import simulation.environment.Environment;
import simulation.environment.Hospital;
import simulation.environment.Node;

/**
 * Class to represent an individual of the population
 */
public class Individual {

    // Maximum distance an individual can move each time step
    private static final double SPEED = 0.00005;

    // Probability of changing location during leisure time
    private static final float LEISURE_GO_OUT_PROB = 0.01f;

    // Probability of going home when changing location during leisure time
    private static final float LEISURE_GO_HOME_PROB = 0.5f;

    // Required simulation components
    private Environment environment;
    private SimulationOutput output;

    // Characteristics
    private int age;
    private boolean isEssential;
    private Schedule schedule;

    // Locations and routes
    private Building home;
    private Building workplace;
    private int workRoom;
    private List<Building> amenities;

    // Current state
    private Activity activity;
    private Point position;
    private Node location;
    private int room;
    private List<Node> route;
    private int routeIndex;
    private Health health;
    private Hospital hospital;

    /**
     * Construct an individual
     * 
     * @param environment Environment
     * @param output      Simulation output
     * @param age         Individual's age
     * @param home        Individual's home building
     * @param workplace   Individual's workplace building
     * @param amenities   Individual's list of amenity buildings
     */
    public Individual(Environment environment, SimulationOutput output, int age, Building home, Building workplace,
            List<Building> amenities) {
        this.environment = environment;
        this.output = output;
        this.home = home;
        this.workplace = workplace;
        this.workRoom = -1;
        this.amenities = amenities;

        // Compile list of regular buildings (home, workplace and amenities)
        ArrayList<Building> regularBuildings = new ArrayList<Building>(amenities);
        regularBuildings.add(home);
        if (workplace != null) {
            regularBuildings.add(workplace);
        }

        // Pre-compute and cache routes between all pairs of regular buildings
        for (int i = 0; i < regularBuildings.size() - 1; i++) {
            for (int j = i + 1; j < regularBuildings.size(); j++) {
                environment.getRoute(regularBuildings.get(i), regularBuildings.get(j));
            }
        }

        this.age = age;
        this.isEssential = workplace != null && workplace.isEssential();
        this.schedule = new Schedule(age, workplace != null);

        reset();
    }

    /**
     * Get the individual's age
     * 
     * @return Age
     */
    public int getAge() {
        return age;
    }

    /**
     * Get the individual's current activity
     * 
     * @return Current activity
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Get the individual's current position
     * 
     * @return Current position
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Get the individual's health
     * 
     * @return Health
     */
    public Health getHealth() {
        return health;
    }

    /**
     * Get the individual's current hospital, if any
     * 
     * @return Current hospital if hospitalised, otherwise null
     */
    public Hospital getHospital() {
        return hospital;
    }

    /**
     * Set the individual's health
     * 
     * @param health Health
     */
    public void setHealth(Health health) {
        this.health = health;
    }

    /**
     * Retrieve all individuals in the same location
     * 
     * @return Set of individuals in the same location
     */
    public HashSet<Individual> getContacts() {
        if (location instanceof Building)
            return ((Building) location).getOccupants(room);
        return new HashSet<>();
    }

    /**
     * Reset the individual to their initial state for a new run
     */
    public void reset() {
        activity = Activity.SLEEP;
        position = home.getPoint();
        if (location instanceof Building) {
            ((Building) location).removeOccupant(this, room);
        }
        location = home;
        room = 0;
        home.addOccupant(this, room);
        route = null;
        routeIndex = 0;
        hospital = null;
    }

    /**
     * Run a single step of the simulation for the individual
     * 
     * @param dayTime Current time of day
     */
    public void step(int dayTime) {
        // Skip individual if deceased
        if (health.getState() == HealthState.DECEASED)
            return;

        if (route == null) {
            // Attempt to hospitalise individual if severely symptomatic
            if (health.getState() == HealthState.SYMPTOMATIC_SEVERE) {
                if (activity != Activity.HOPSITALISATION) {
                    if (goToHospital()) {
                        activity = Activity.HOPSITALISATION;
                    } else if (activity != Activity.ISOLATION) {
                        activity = Activity.ISOLATION;
                        goToHome();
                    }
                }
                // Isolate individual if needed
            } else if (health.isSelfIsolating()) {
                if (activity != Activity.ISOLATION) {
                    activity = Activity.ISOLATION;
                    goToHome();
                }
                // Otherwise follow normal schedule
            } else {
                if (activity == Activity.HOPSITALISATION) {
                    hospital.dischargePatient(output);
                }
                followSchedule(dayTime);
            }
        }

        // Move individual along route
        move();
    }

    /**
     * Follow the individual's schedule, changing location if needed
     * 
     * @param dayTime Current time of day
     */
    private void followSchedule(int dayTime) {
        Activity newActivity = schedule.getActivity(dayTime);
        switch (newActivity) {
            case SLEEP:
                if (activity != Activity.SLEEP) {
                    activity = Activity.SLEEP;
                    goToHome();
                }
                break;
            case WORK:
                if (activity != Activity.WORK) {
                    activity = Activity.WORK;
                    goToWork();
                }
                break;
            case LEISURE:
                if (activity != Activity.LEISURE || Math.random() <= LEISURE_GO_OUT_PROB) {
                    activity = Activity.LEISURE;
                    goToLeisure();
                }
                break;
            default:
        }
    }

    /**
     * Send the individual home
     */
    private void goToHome() {
        route = environment.getRoute(location, home);
        routeIndex = 0;
    }

    /**
     * Send the individual to work, unless interventions interfere
     */
    private void goToWork() {
        if ((!health.inLockdown() || isEssential) && !workplace.isClosed()) {
            route = environment.getRoute(location, workplace);
            routeIndex = 0;
            return;
        }
        goToHome();
    }

    /**
     * Send the individual to their leisure location, unless interventions interfere
     */
    private void goToLeisure() {
        // Either go home or to a randomly chosen amenity
        if (!health.inLockdown() && Math.random() > LEISURE_GO_HOME_PROB) {
            if (!amenities.isEmpty()) {
                Building amenity = amenities.get((int) (Math.random() * amenities.size()));
                if (!amenity.isClosed()) {
                    route = environment.getRoute(location, amenity);
                    routeIndex = 0;
                    return;
                }
            }
        }
        goToHome();
    }

    /**
     * Send the individual to a hospital if possible
     * 
     * @return True if the individual was hospitalised, false otherwise
     */
    private boolean goToHospital() {
        // Select a random hospital
        Hospital hospital = environment.getRandomHospital(location.getComponentID());

        // Route to the hospital if it is not full
        if (hospital != null && hospital.admitPatient(output)) {
            this.hospital = hospital;
            route = environment.findRoute(location, hospital);
            routeIndex = 0;
            return true;
        }
        return false;
    }

    /**
     * Move the individual along their route
     */
    private void move() {
        if (route == null)
            return;

        float deltaTime = Simulation.TIME_STEP;

        // Move individual as far as possible within the time step
        do {
            // Finish if the destination is reached
            if (routeIndex == route.size() - 1) {
                route = null;
                return;
            }

            // Move to the next node in the route
            Node next = route.get(routeIndex + 1);
            double distance = position.distance(next.getCentre());
            double timeToNext = distance / SPEED;

            // Check if the next node can be reached
            if (timeToNext < deltaTime) {
                deltaTime -= timeToNext;
                routeIndex++;
                if (location instanceof Building) {
                    ((Building) location).removeOccupant(this, room);
                }
                location = next;

                // If reached a building, set individual as an occupant
                if (location instanceof Building) {
                    Building building = (Building) location;
                    if (activity == Activity.WORK) {
                        if (workRoom == -1) {
                            workRoom = building.getRandomRoom();
                        }
                        room = workRoom;
                    } else {
                        room = building.getRandomRoom();
                    }
                    building.addOccupant(this, room);
                }

                // Update individual's position to be inside the new node
                position = location.getPoint();
            } else {

                // Interpolate position along the road between the current and next node
                Coordinate current = position.getCoordinate();
                Coordinate target = next.getCentre().getCoordinate();
                double dx = target.x - current.x;
                double dy = target.y - current.y;
                double progress = deltaTime / timeToNext;
                position = position.getFactory()
                        .createPoint(new Coordinate(current.x + dx * progress, current.y + dy * progress));
                deltaTime = 0;
            }
        } while (deltaTime > 0);
    }
}
