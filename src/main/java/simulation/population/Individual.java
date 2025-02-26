package simulation.population;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

import javafx.util.Pair;
import simulation.core.Simulation;
import simulation.core.SimulationOutput;
import simulation.disease.Health;
import simulation.disease.HealthState;
import simulation.environment.Building;
import simulation.environment.Environment;
import simulation.environment.Hospital;
import simulation.environment.Node;

public class Individual {

    private final double SPEED = 0.00001;
    private final int AMENITY_NUM = 5;
    private final float LEISURE_GO_OUT_PROB = 0.05f;
    private final float LEISURE_GO_HOME_PROB = 0.5f;

    private Environment environment;
    private SimulationOutput output;

    // Characteristics
    private int age;
    private boolean isEssential;
    private Schedule schedule;

    // Locations and routes
    private Building home;
    private Building workplace;
    private List<Building> amenities;
    private HashMap<Pair<Building, Building>, List<Node>> routeCache;

    // Current state
    private Activity activity;
    private Point position;
    private Node location;
    private List<Node> route;
    private int routeIndex;
    private Health health;
    private Hospital hospital;

    public Individual(Environment environment, SimulationOutput output, int age, Building home, Building workplace) {
        this.environment = environment;
        this.output = output;

        this.home = home;
        this.workplace = workplace;
        this.amenities = new ArrayList<Building>();
        for (int i = 0; i < AMENITY_NUM; i++) {
            Building amenity = environment.getRandomAmenity(home.getComponentID());
            if (amenity != null) {
                amenities.add(amenity);
            }
        }

        this.routeCache = new HashMap<Pair<Building, Building>, List<Node>>();
        ArrayList<Building> regularBuildings = new ArrayList<Building>(amenities);
        regularBuildings.add(home);
        if (workplace != null) {
            regularBuildings.add(workplace);
        }
        for (int i = 0; i < regularBuildings.size() - 1; i++) {
            for (int j = i + 1; j < regularBuildings.size(); j++) {
                Building b1 = regularBuildings.get(i);
                Building b2 = regularBuildings.get(j);
                List<Node> route = findRoute(b1, b2);
                List<Node> reverseRoute = new ArrayList<Node>(route);
                Collections.reverse(reverseRoute);
                routeCache.put(new Pair<Building, Building>(b1, b2), route);
                routeCache.put(new Pair<Building, Building>(b2, b1), reverseRoute);
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
            return ((Building) location).getOccupants();
        return new HashSet<>();
    }

    public void reset() {
        activity = Activity.SLEEP;
        position = home.getPoint();
        if (location instanceof Building) {
            ((Building) location).removeOccupant(this);
        }
        location = home;
        home.addOccupant(this);
        route = null;
        hospital = null;
    }

    public void step(int dayTime) {
        // Skip individual if deceased
        if (health.getState() == HealthState.DECEASED)
            return;
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
        // Move individual along route
        move();
    }

    private void followSchedule(int dayTime) {
        if (route != null) {
            return;
        }
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
        if (location instanceof Building) {
            route = routeCache.get(new Pair<Building, Building>((Building) location, home));
        } else {
            route = findRoute(location, home);
        }
        routeIndex = 0;
    }

    private void goToWork() {
        if ((!health.inLockdown() || isEssential) && !workplace.isClosed()) {
            if (location instanceof Building) {
                route = routeCache.get(new Pair<Building, Building>((Building) location, workplace));
            } else {
                route = findRoute(location, workplace);
            }
            routeIndex = 0;
            return;
        }
        goToHome();
    }

    private void goToLeisure() {
        if (!health.inLockdown() && Math.random() > LEISURE_GO_HOME_PROB) {
            Building amenity = amenities.get((int) (Math.random() * amenities.size()));
            if (!amenity.isClosed()) {
                if (location instanceof Building) {
                    route = routeCache.get(new Pair<Building, Building>((Building) location, amenity));
                } else {
                    route = findRoute(location, amenity);
                }
                routeIndex = 0;
                return;
            }
        }
        goToHome();
    }

    private boolean goToHospital() {
        Hospital hospital = environment.getRandomHospital(location.getComponentID());
        if (hospital != null && hospital.admitPatient(output)) {
            this.hospital = hospital;
            route = findRoute(location, hospital);
            routeIndex = 0;
            return true;
        }
        return false;
    }

    private void move() {
        float deltaTime = Simulation.TIME_STEP;
        if (route == null)
            return;
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
                    ((Building) location).removeOccupant(this);
                }
                location = next;
                if (location instanceof Building) {
                    ((Building) location).addOccupant(this);
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

    private List<Node> findRoute(Node start, Node end) {
        Set<Node> visited = new HashSet<>();
        HashMap<Node, Node> cameFrom = new HashMap<>();
        HashMap<Node, Double> gScore = new HashMap<>();
        HashMap<Node, Double> fScore = new HashMap<>();
        PriorityQueue<Node> frontier = new PriorityQueue<>(
                Comparator.comparingDouble(node -> fScore.getOrDefault(node, Double.POSITIVE_INFINITY)));

        gScore.put(start, 0.0);
        fScore.put(start, distance(start, end));
        frontier.add(start);

        while (!frontier.isEmpty()) {
            Node current = frontier.poll();

            if (current.equals(end)) {
                LinkedList<Node> route = new LinkedList<>();
                route.addFirst(current);
                while (cameFrom.containsKey(current)) {
                    current = cameFrom.get(current);
                    route.addFirst(current);
                }
                return route;
            }

            visited.add(current);

            for (Node neighbour : current.getNeighbours()) {
                if (visited.contains(neighbour))
                    continue;

                double g = gScore.getOrDefault(current, Double.POSITIVE_INFINITY)
                        + distance(current, neighbour);

                if (!frontier.contains(neighbour))
                    frontier.add(neighbour);
                else if (g >= gScore.getOrDefault(neighbour, Double.POSITIVE_INFINITY))
                    continue;

                double f = g + distance(neighbour, end);

                cameFrom.put(neighbour, current);
                gScore.put(neighbour, g);
                fScore.put(neighbour, f);
            }
        }

        return null;
    }

    private double distance(Node a, Node b) {
        return a.getCentre().distance(b.getCentre());
    }
}
