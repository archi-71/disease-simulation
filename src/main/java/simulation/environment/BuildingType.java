package simulation.environment;

/**
 * Enum class for building types
 */
public enum BuildingType {

    RESIDENTIAL("Residential", "#FF6F6F", "#B13C3C"),

    SCHOOL("Schools", "#FFA500", "#CC7A00"),

    UNIVERSITY("Universities", "#FFEB3B", "#C8A900"),

    HOSPITAL("Hospitals", "#9C27B0", "#6A1B9A"),

    ESSENTIAL_AMENITY("Essential Amenities", "#1976D2", "#0D47A1"),

    NON_ESSENTIAL_AMENITY("Non-Essential Amenities", "#4FC3F7", "#0288D1"),

    ESSENTIAL_WORKPLACE("Essential Workplaces", "#388E3C", "#1B5E20"),

    NON_ESSENTIAL_WORKPLACE("Non-Essential Workplaces", "#8BC34A", "#5A8C2B");

    private final String name;
    private final String fillColour;
    private final String outlineColour;

    /**
     * Construct a new building type
     * 
     * @param name          Name of building type
     * @param fillColour    Visualisation fill colour
     * @param outlineColour Visualisation outline colour
     */
    BuildingType(String name, String fillColour, String outlineColour) {
        this.name = name;
        this.fillColour = fillColour;
        this.outlineColour = outlineColour;
    }

    /**
     * Get the name of the building type
     * 
     * @return Name of building type
     */
    public String getName() {
        return name;
    }

    /**
     * Get the fill colour of the building type for visualisation
     * 
     * @return Visualisation fill colour
     */
    public String getFillColour() {
        return fillColour;
    }

    /**
     * Get the outline colour of the building type for visualisation
     * 
     * @return Visualisation outline colour
     */
    public String getOutlineColour() {
        return outlineColour;
    }
}
