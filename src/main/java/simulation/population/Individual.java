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

public class Individual {

    private static final double SPEED = 0.00005;
    private static final float LEISURE_GO_OUT_PROB = 0.01f;
    private static final float LEISURE_GO_HOME_PROB = 0.5f;

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

    public Individual(Environment environment, SimulationOutput output, int age, Building home, Building workplace,
            List<Building> amenities) {
        this.environment = environment;
        this.output = output;

        this.home = home;
        this.workplace = workplace;
        this.workRoom = -1;
        this.amenities = amenities;

        ArrayList<Building> regularBuildings = new ArrayList<Building>(amenities);
        regularBuildings.add(home);
        if (workplace != null) {
            regularBuildings.add(workplace);
        }
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

    public int getAge() {
        return age;
    }

    public Activity getActivity() {
        return activity;
    }

    public Point getPosition() {
        return position;
    }

    public Health getHealth() {
        return health;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHealth(Health health) {
        this.health = health;
    }

    public HashSet<Individual> getContacts() {
        if (location instanceof Building)
            return ((Building) location).getOccupants(room);
        return new HashSet<>();
    }

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

    private void goToHome() {
        route = environment.getRoute(location, home);
        routeIndex = 0;
    }

    private void goToWork() {
        if ((!health.inLockdown() || isEssential) && !workplace.isClosed()) {
            route = environment.getRoute(location, workplace);
            routeIndex = 0;
            return;
        }
        goToHome();
    }

    private void goToLeisure() {
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

    private boolean goToHospital() {
        Hospital hospital = environment.getRandomHospital(location.getComponentID());
        if (hospital != null && hospital.admitPatient(output)) {
            this.hospital = hospital;
            route = environment.findRoute(location, hospital);
            routeIndex = 0;
            return true;
        }
        return false;
    }

    private void move() {
        if (route == null)
            return;
        float deltaTime = Simulation.TIME_STEP;
        do {
            if (routeIndex == route.size() - 1) {
                route = null;
                return;
            }
            Node next = route.get(routeIndex + 1);
            double distance = position.distance(next.getCentre());
            double timeToNext = distance / SPEED;
            if (timeToNext < deltaTime) {
                deltaTime -= timeToNext;
                routeIndex++;
                if (location instanceof Building) {
                    ((Building) location).removeOccupant(this, room);
                }
                location = next;
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
                position = location.getPoint();
            } else {
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
