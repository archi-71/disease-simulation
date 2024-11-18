package simulation.environment;

public enum BuildingType {

    RESIDENTIAL("#FF6F6F", "#B13C3C"),

    SCHOOL("#FFA500", "#CC7A00"),

    UNIVERSITY("#FFEB3B", "#C8A900"),

    HOSPITAL("#9C27B0", "#6A1B9A"),

    ESSENTIAL_AMENITY("#1976D2", "#0D47A1"),

    ESSENTIAL_WORKPLACE("#388E3C", "#1B5E20"),

    NON_ESSENTIAL_AMENITY("#4FC3F7", "#0288D1"),

    NON_ESSENTIAL_WORKPLACE("#8BC34A", "#5A8C2B");

    private final String fillColour;
    private final String outlineColour;

    BuildingType(String fillColour, String outlineColour) {
        this.fillColour = fillColour;
        this.outlineColour = outlineColour;
    }

    public String getFillColour() {
        return fillColour;
    }

    public String getOutlineColour() {
        return outlineColour;
    }
}
