package simulation.disease;

public enum HealthState {
    SUSCEPTIBLE("Susceptible", "#7EC8E3"),
    EXPOSED("Exposed", "#FFEB3B"),
    INFECTIOUS("Infectious", "#FFA500"),
    ASYMPTOMATIC("Asymptomatic", "#D24DFF"),
    SYMPTOMATIC_MILD("Mildly Symptomatic", "#FF6F61"),
    SYMPTOMATIC_SEVERE("Severely Symptomatic", "#B73A3A"),
    DECEASED("Deceased", "#424242"),
    RECOVERED("Recovered", "#81C784");

    private final String name;
    private final String colour;

    HealthState(String name, String colour) {
        this.name = name;
        this.colour = colour;
    }

    public String getName() {
        return name;
    }

    public String getColour() {
        return colour;
    }

    public boolean isInfectious() {
        return this == INFECTIOUS || this == ASYMPTOMATIC || this == SYMPTOMATIC_MILD || this == SYMPTOMATIC_SEVERE;
    }
}
