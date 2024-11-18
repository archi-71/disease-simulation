package simulation.population;

public class Schedule {

    private Event[] events;

    public Schedule() {
        createStandardSchedule();
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

    private void createStandardSchedule() {
        events = new Event[2];
        events[0] = new Event(Activity.SLEEP, hToMS(23), hToMS(7));
        events[1] = new Event(Activity.WORK, hToMS(9), hToMS(17));
    }

    private int hToMS(int hours) {
        return hours * 60 * 60 * 1000;
    }
}
