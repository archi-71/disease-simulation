package simulation.interventions;

public enum InterventionType {
    MASKS("Mask Wearing"),
    SOCIAL_DISTANCING("Social Distancing"),
    TESTING("Testing"),
    ISOLATION("Isolation"),
    TRACING_AND_QUARANTINE("Contact Tracing & Quarantine"),
    SCHOOL_CLOSURE("School Closures"),
    UNIVERSITY_CLOSURE("University Closures"),
    WORKPLACE_CLOSURE("Workplace Closures"),
    LOCKDOWN("Lockdown"),
    VACCINATION("Vaccination");

    private final String name;

    InterventionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
