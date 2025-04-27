package simulation.interventions;

/**
 * Enum class for types of intervention strategies
 */
public enum InterventionType {
    MASKS("Mask Wearing"),
    SOCIAL_DISTANCING("Social Distancing"),
    ISOLATION("Isolation"),
    TESTING("Testing"),
    TRACING_AND_QUARANTINE("Contact Tracing & Quarantine"),
    SCHOOL_CLOSURE("School Closures"),
    UNIVERSITY_CLOSURE("University Closures"),
    WORKPLACE_CLOSURE("Workplace Closures"),
    LOCKDOWN("Lockdown"),
    VACCINATION("Vaccination");

    private final String name;

    /**
     * Construct a new intervention type
     * @param name Intervention name
     */
    InterventionType(String name) {
        this.name = name;
    }

    /**
     * Get the name of the intervention type
     * @return Intervention name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the name of the intervention type
     * @return Intervention name
     */
    @Override
    public String toString() {
        return name;
    }
}
