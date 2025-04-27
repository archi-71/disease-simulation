package simulation.population;

/**
 * Class to represent an event of an individual's schedule
 */
public class Event {

    private Activity activity;
    private int start;
    private int end;

    /**
     * Construct an event
     * @param activity Activity type
     * @param start Start time
     * @param end End time
     */
    public Event(Activity activity, int start, int end) {
        this.activity = activity;
        this.start = start;
        this.end = end;
    }

    /**
     * Get the activity of the event
     * @return Activity type
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Get the start time of the event
     * @return Start time
     */
    public int getStart() {
        return start;
    }

    /**
     * Get the end time of the event
     * @return End time
     */
    public int getEnd() {
        return end;
    }
}
