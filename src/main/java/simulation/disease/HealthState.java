package simulation.disease;

/**
 * Enum class for the states of the disease model
 */
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

    /**
     * Construct a new health state
     * @param name State name
     * @param colour Visualisation colour
     */
    HealthState(String name, String colour) {
        this.name = name;
        this.colour = colour;
    }

    /**
     * Get the name of the health state
     * @return State name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the colour of the health state for visualisation
     * @return Visualisation colour
     */
    public String getColour() {
        return colour;
    }

    /**
     * Check if the individual is carrying the disease
     * @return True if the individual is carrying the disease
     */
    public boolean isInfected() {
        return this == EXPOSED || isInfectious();
    }

    /**
     * Check if the individual can spread the disease
     * @return True if the individual can spread the disease
     */
    public boolean isInfectious() {
        return this == INFECTIOUS || this == ASYMPTOMATIC || this == SYMPTOMATIC_MILD || this == SYMPTOMATIC_SEVERE;
    }
}
