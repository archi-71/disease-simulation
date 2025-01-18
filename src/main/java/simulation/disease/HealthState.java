package simulation.disease;

public enum HealthState {
    SUSCEPTIBLE("#7EC8E3"),
    EXPOSED("#FFEB3B"),
    INFECTIOUS("#FFA500"),
    ASYMPTOMATIC("#D24DFF"),
    SYMPTOMATIC_MILD("#FF6F61"),
    SYMPTOMATIC_SEVERE("#B73A3A"),
    RECOVERED("#81C784"),
    DECEASED("#424242");

    private final String colour;

    HealthState(String colour) {
        this.colour = colour;
    }

    public String getColour() {
        return colour;
    }

    public boolean isInfectious() {
        return this == INFECTIOUS || this == ASYMPTOMATIC || this == SYMPTOMATIC_MILD || this == SYMPTOMATIC_SEVERE;
    }
}
