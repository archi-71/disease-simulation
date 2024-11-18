package simulation;

public class Event {

    private Activity activity;
    private int start;
    private int end;

    public Event(Activity activity, int start, int end) {
        this.activity = activity;
        this.start = start;
        this.end = end;
    }

    public Activity getActivity() {
        return activity;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
