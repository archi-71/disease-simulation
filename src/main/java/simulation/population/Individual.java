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

import simulation.disease.Health;
import simulation.environment.Building;
import simulation.environment.Environment;
import simulation.environment.Node;

public class Individual {

    private final double speed = 0.000000001;

    private Environment environment;
    private Schedule schedule;
    private Building home;
    private Building work;
    private List<Node> homeToWork;
    private List<Node> workToHome;
    private Health health;
    private Activity activity;
    private Point position;
    private Node location;
    private List<Node> route;
    private int routeIndex;

    public Point getPosition() {
        return position;
    }

    public Health getHealth() {
        return health;
    }

    public Health setHealth(Health health) {
        return this.health = health;
    }

    public Individual(Environment environment) {
        this.environment = environment;
        home = environment.getHomes().get((int) (Math.random() * environment.getHomes().size()));
        while (homeToWork == null) {
            this.work = environment.getWorkplaces().get((int) (Math.random() * environment.getWorkplaces().size()));
            homeToWork = findRoute(home, work);
        }
        this.workToHome = new ArrayList<>(homeToWork);
        java.util.Collections.reverse(workToHome);
        schedule = new Schedule();
        reset();
    }

    public HashSet<Individual> getContacts() {
        if (location instanceof Building)
            return ((Building) location).getOccupants();
        return new HashSet<>();
    }

    public void reset() {
        health = Health.SUSCEPTIBLE;
        activity = Activity.SLEEP;
        position = home.getPoint();
        location = home;
        home.addOccupant(this);
        route = null;
    }

    public void step(int time, double deltaTime) {
        if (health == Health.DECEASED)
            return;
        Activity newActivity = schedule.getActivity(time);
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
                    if (location == home)
                        goToWork();
                }
                break;
            case LEISURE:
                if (activity != Activity.LEISURE || route == null && Math.random() < 0.01) {
                    activity = Activity.LEISURE;
                    goToLeisure();
                    break;
                }
        }
        if (route != null) {
            followRoute(deltaTime);
        }
    }

    private void goToHome() {
        if (location == work)
            route = workToHome;
        else
            route = findRoute(location, home);
        routeIndex = 0;
    }

    private void goToWork() {
        if (location == home)
            route = homeToWork;
        else
            route = findRoute(location, work);
        routeIndex = 0;
    }

    private void goToLeisure() {
        if (Math.random() < 0.5) {
            Building amenity = environment.getAmenities()
                    .get((int) (Math.random() * environment.getAmenities().size()));
            route = findRoute(location, amenity);
            route = findRoute(location, amenity);
            routeIndex = 0;
        } else {
            goToHome();
        }
    }

    private void followRoute(double deltaTime) {
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
