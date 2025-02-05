package simulation.population;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

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

    private final double speed = 0.00001;

    private Environment environment;
    private SimulationOutput output;

    // Individual characteristics
    private int age;
    private Building home;
    private Building workplace;
    private boolean isEssential;
    private Schedule schedule;
    private List<Node> homeToWorkplace;
    private List<Node> workplaceToHome;

    // Individual current state
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
        this.age = age;
        this.home = home;
        this.workplace = workplace;
        isEssential = workplace != null && workplace.isEssential();
        schedule = new Schedule(age, workplace != null);
        if (workplace != null) {
            homeToWorkplace = findRoute(home, workplace);
            workplaceToHome = new ArrayList<>(homeToWorkplace);
            java.util.Collections.reverse(workplaceToHome);
        }
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
        } else if (activity != Activity.ISOLATION && health.isSelfIsolating()) {
            activity = Activity.ISOLATION;
            goToHome();
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
                if (activity != Activity.LEISURE || route == null && Math.random() <= 0.01) {
                    activity = Activity.LEISURE;
                    goToLeisure();
                }
                break;
            default:
        }
    }

    private void goToHome() {
        if (location == workplace)
            route = workplaceToHome;
        else
            route = findRoute(location, home);
        routeIndex = 0;
    }

    private void goToWork() {
        if ((!health.inLockdown() || isEssential) && !workplace.isClosed()) {
            if (location == home)
                route = homeToWorkplace;
            else
                route = findRoute(location, workplace);
            routeIndex = 0;
            return;
        }
        goToHome();
    }

    private void goToLeisure() {
        if (!health.inLockdown() && Math.random() < 0.5) {
            Building amenity = environment.getRandomAmenity(location.getComponentID());
            if (amenity != null && !amenity.isClosed()) {
                route = findRoute(location, amenity);
                routeIndex = 0;
                return;
            }
        }
        goToHome();
    }

    private boolean goToHospital() {
        Hospital hospital = environment.getRandomHospital(location.getComponentID());
        if (hospital != null && !hospital.isFull()) {
            this.hospital = hospital;
            hospital.admitPatient(output);
            route = findRoute(location, hospital);
            routeIndex = 0;
            return true;
        }
        return false;
    }

    private void move() {
        float deltaTime = Simulation.timeStep;
        if (route == null)
            return;
        do {
            if (routeIndex == route.size() - 1) {
                route = null;
                return;
            }
            Node next = route.get(routeIndex + 1);
            double distance = position.distance(next.getCentre());
            double timeToNext = distance / speed;
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
                List<Node> route = new LinkedList<>();
                route.add(current);
                while (cameFrom.containsKey(current)) {
                    current = cameFrom.get(current);
                    route.add(0, current);
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
