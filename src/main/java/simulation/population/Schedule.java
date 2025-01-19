package simulation.population;

import java.util.ArrayList;

public class Schedule {

    private final double range = 2;

    private ArrayList<Event> events;

    public Schedule(int age, boolean isWorking) {
        events = new ArrayList<>();

        if (age < 5) {
            events.add(new Event(Activity.SLEEP, getTime(19), getTime(7)));
        } else if (age < 13) {
            events.add(new Event(Activity.SLEEP, getTime(21), getTime(7)));
        } else if (age < 18) {
            events.add(new Event(Activity.SLEEP, getTime(22), getTime(8)));
        } else if (age < 25) {
            events.add(new Event(Activity.SLEEP, getTime(24), getTime(8)));
        } else if (age < 65) {
            events.add(new Event(Activity.SLEEP, getTime(23), getTime(7)));
        } else {
            events.add(new Event(Activity.SLEEP, getTime(22), getTime(6)));
        }

        if (isWorking) {
            events.add(new Event(Activity.WORK, getTime(9), getTime(17)));
        }
    }

    public Activity getActivity(int time) {
        for (Event event : events) {
            if (event.getStart() <= event.getEnd()) {
                if (time >= event.getStart() && time < event.getEnd()) {
                    return event.getActivity();
                }
            } else {
                if (time >= event.getStart() || time < event.getEnd()) {
                    return event.getActivity();
                }
            }
        }
        return Activity.LEISURE;
    }

    private int getTime(int hours) {
        return (int) ((hours + Math.random() * range - range / 2) * 3600);
    }
}
