package simulation.population;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

import simulation.environment.Building;
import simulation.environment.Node;

public class Individual {

    private final double speed = 0.000000001;

    private Schedule schedule;
    private Building home;
    private Building work;
    private List<Node> homeToWork;
    private List<Node> workToHome;

    private Activity activity;
    private Point position;
    private Node location;
    private List<Node> route;
    private int routeIndex;

    public Point getPosition() {
        return position;
    }

    public Individual(Building home, Building work) {
        schedule = new Schedule();
        this.home = home;
        this.work = work;
        homeToWork = findRoute(home, work);
        if (homeToWork != null) {
            this.workToHome = new ArrayList<>(homeToWork);
            java.util.Collections.reverse(workToHome);
        }

        activity = Activity.SLEEP;
        position = home.getPoint();
        location = home;
    }

    public void reset() {
        activity = Activity.SLEEP;
        position = location.getPoint();
        location = home;
        route = null;
    }

    public void step(int time, double deltaTime) {
        if (route == null) {
            Activity newActivity = schedule.getActivity(time);
            switch (newActivity) {
                case SLEEP:
                    if (activity != Activity.SLEEP) {
                        activity = Activity.SLEEP;
                        if (location == work)
                            route = workToHome;
                        else
                            route = findRoute(location, home);
                    }
                    break;
                case WORK:
                    if (activity != Activity.WORK) {
                        activity = Activity.WORK;
                        if (location == home)
                            route = homeToWork;
                        else
                            route = findRoute(location, work);
                    }
                    break;
                case LEISURE:
                    activity = Activity.LEISURE;
                    List<Node> neighbours = location.getNeighbours();
                    route = Arrays.asList(location, neighbours.get((int) (Math.random() *
                            neighbours.size())));
                    break;
            }
            routeIndex = 0;
        }
        if (route != null) {
            followRoute(deltaTime);
        }
    }

    private void followRoute(double deltaTime) {
        do {
            Node next = route.get(routeIndex + 1);
            double distance = position.distance(next.getCentre());
            double timeToNext = distance / speed;
            if (timeToNext < deltaTime) {
                deltaTime -= timeToNext;
                routeIndex++;
                location = next;
                position = location.getPoint();
                if (routeIndex == route.size() - 1) {
                    route = null;
                    return;
                }
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
