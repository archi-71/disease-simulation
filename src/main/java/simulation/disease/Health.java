package simulation.disease;

public enum Health {
    SUSCEPTIBLE("#4EC1E1"),
    INFECTIOUS("#FF6B6B"),
    RECOVERED("#6DFF6D"),
    DECEASED("#555555");

    private final String colour;

    Health(String colour) {
        this.colour = colour;
    }

    public String getColour() {
        return colour;
    }
}
