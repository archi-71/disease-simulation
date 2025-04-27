package simulation.population;

import java.util.ArrayList;

/**
 * Class to represent an individual's schedule
 */
public class Schedule {

    // Range in hours for randomising start and end times
    private static final double RANDOM_RANGE = 5;

    // List of events in the schedule
    private ArrayList<Event> events;

    /**
     * Construct a schedule for an individual, given their age and work status
     * 
     * @param age       Individual's age
     * @param isWorking True if the individual is working, false otherwise
     */
    public Schedule(int age, boolean isWorking) {
        events = new ArrayList<>();

        // Schedule sleep time based on age
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

        // Schedule work time if individual works
        if (isWorking) {
            events.add(new Event(Activity.WORK, getTime(9), getTime(17)));
        }
    }

    /**
     * Determine the scheduled activity at a given time of day
     * 
     * @param time Time of day
     * @return Activity type
     */
    public Activity getActivity(int time) {
        for (Event event : events) {
            // Normal events
            if (event.getStart() <= event.getEnd()) {
                if (time >= event.getStart() && time < event.getEnd()) {
                    return event.getActivity();
                }
                // Overnight events
            } else {
                if (time >= event.getStart() || time < event.getEnd()) {
                    return event.getActivity();
                }
            }
        }
        return Activity.LEISURE;
    }

    /**
     * Convert time of day in hours to seconds and add random offset
     * 
     * @param hours Time of day in hours
     * @return Time of day in seconds
     */
    private int getTime(int hours) {
        return (int) ((hours + Math.random() * RANDOM_RANGE - RANDOM_RANGE / 2) * 3600);
    }
}
