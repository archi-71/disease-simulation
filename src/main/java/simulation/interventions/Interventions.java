package simulation.interventions;

import java.util.List;
import java.util.PriorityQueue;

import simulation.params.InterventionParam;
import simulation.params.InterventionParams;
import simulation.core.InitialisationException;
import simulation.environment.Building;
import simulation.environment.Environment;

public class Interventions {

    private InterventionParams parameters;

    // Queue of inactive interventions which are yet to start, sorted by start day
    private PriorityQueue<InterventionParam> inactiveInterventions;

    // Queue of active interventions which are yet to end, sorted by end day
    private PriorityQueue<InterventionParam> activeInterventions;

    // Building closure parameters
    private List<Building> schools;
    private List<Building> universities;
    private List<Building> nonEssentialWorkplaces;
    private int schoolClosures;
    private int universityClosures;
    private int nonEssentialWorkplaceClosures;

    // Mask parameters
    private boolean masks;
    private float maskIncomingProtection;
    private float maskOutgoingProtection;
    private float maskCompliance;

    // Social distancing parameters
    private boolean socialDistancing;
    private float socialDistancingEffectiveness;
    private float socialDistancingCompliance;

    // Isolation parameters
    private boolean isolation;
    private float isolationCompliance;

    // Testing parameters
    private boolean testing;
    private float testFrequency;
    private float testWaitTime;
    private float testFalsePositiveRate;
    private float testFalseNegativeRate;
    private float testCompliance;

    // Tracing and quarantine parameters
    private boolean tracingAndQuarantine;
    private float tracingEffectiveness;
    private float tracingWaitTime;
    private float minQuarantineTime;
    private float quarantineCompliance;

    // Lockdown parameters
    private boolean lockdown;
    private float lockdownCompliance;

    // Vaccination parameters
    private boolean vaccination;
    private int vaccineNumber;
    private float vaccinationSusceptibilityReduction;
    private float vaccinationSeverityReduction;
    private float vaccinationRate;
    private float vaccinationCompliance;

    public InterventionParams getParameters() {
        return parameters;
    }

    public boolean isMaskWearingActive() {
        return masks;
    }

    public float getMaskIncomingProtection() {
        return maskIncomingProtection;
    }

    public float getMaskOutgoingProtection() {
        return maskOutgoingProtection;
    }

    public float getMaskCompliance() {
        return maskCompliance;
    }

    public boolean isSocialDistancingActive() {
        return socialDistancing;
    }

    public float getSocialDistancingEffectiveness() {
        return socialDistancingEffectiveness;
    }

    public float getSocialDistancingCompliance() {
        return socialDistancingCompliance;
    }

    public boolean isIsolationActive() {
        return isolation;
    }

    public float getIsolationCompliance() {
        return isolationCompliance;
    }

    public boolean isTestingActive() {
        return testing;
    }

    public float getTestFrequency() {
        return testFrequency;
    }

    public float getTestWaitTime() {
        return testWaitTime;
    }

    public float getTestFalsePositiveRate() {
        return testFalsePositiveRate;
    }

    public float getTestFalseNegativeRate() {
        return testFalseNegativeRate;
    }

    public float getTestCompliance() {
        return testCompliance;
    }

    public boolean isTracingAndQuarantineActive() {
        return tracingAndQuarantine;
    }

    public float getTracingEffectiveness() {
        return tracingEffectiveness;
    }

    public float getTracingWaitTime() {
        return tracingWaitTime;
    }

    public float getMinQuarantineTime() {
        return minQuarantineTime;
    }

    public float getQuarantineCompliance() {
        return quarantineCompliance;
    }

    public boolean isLockdownActive() {
        return lockdown;
    }

    public float getLockdownCompliance() {
        return lockdownCompliance;
    }

    public boolean isVaccinationActive() {
        return vaccination;
    }

    public int getVaccineNumber() {
        return vaccineNumber;
    }

    public float getVaccinationSusceptibilityReduction() {
        return vaccinationSusceptibilityReduction;
    }

    public float getVaccinationSeverityReduction() {
        return vaccinationSeverityReduction;
    }

    public float getVaccinationRate() {
        return vaccinationRate;
    }

    public float getVaccinationCompliance() {
        return vaccinationCompliance;
    }

    public void initialise(InterventionParams params, Environment environment) throws InitialisationException {
        // Validate intervention parameters
        for (InterventionParam intervention : params.getInterventions()) {
            if (intervention.getStart().getValue() > intervention.getEnd().getValue()) {
                throw new InitialisationException("Interventions cannot end before they start");
            }
        }

        parameters = params;
        schools = environment.getSchools();
        universities = environment.getUniversities();
        nonEssentialWorkplaces = environment.getNonEssentialWorkplaces();
        reset();
    }

    public void step(int day) {
        // End old interventions
        InterventionParam nextToEnd = activeInterventions.peek();
        while (nextToEnd != null && nextToEnd.getEnd().getValue() <= day) {
            endIntervention(nextToEnd);
            activeInterventions.poll();
            nextToEnd = activeInterventions.peek();
        }
        // Start new interventions
        InterventionParam nextToStart = inactiveInterventions.peek();
        while (nextToStart != null && nextToStart.getStart().getValue() - 1 <= day) {
            startIntervention(nextToStart);
            activeInterventions.add(inactiveInterventions.poll());
            nextToStart = inactiveInterventions.peek();
        }
    }

    public void reset() {
        inactiveInterventions = new PriorityQueue<>((a, b) -> a.getStart().getValue() - b.getStart().getValue());
        activeInterventions = new PriorityQueue<>((a, b) -> a.getEnd().getValue() - b.getEnd().getValue());
        for (InterventionParam intervention : parameters.getInterventions()) {
            inactiveInterventions.add(intervention);
        }
        schoolClosures = 0;
        universityClosures = 0;
        nonEssentialWorkplaceClosures = 0;
        masks = false;
        socialDistancing = false;
        isolation = false;
        testing = false;
        tracingAndQuarantine = false;
        lockdown = false;
        vaccination = false;
        vaccineNumber = 0;
        step(0);
    }

    private void startIntervention(InterventionParam intervention) {
        switch (intervention.getType()) {
            case MASKS:
                masks = true;
                maskIncomingProtection = intervention.getParams().getValue("incomingProtection").getValue();
                maskOutgoingProtection = intervention.getParams().getValue("outgoingProtection").getValue();
                maskCompliance = intervention.getParams().getValue("compliance").getValue();
                break;
            case SOCIAL_DISTANCING:
                socialDistancing = true;
                socialDistancingEffectiveness = intervention.getParams().getValue("effectiveness").getValue();
                socialDistancingCompliance = intervention.getParams().getValue("compliance").getValue();
                break;
            case TESTING:
                testing = true;
                testFrequency = intervention.getParams().getValue("frequency").getValue();
                testWaitTime = intervention.getParams().getValue("wait").getValue();
                testFalsePositiveRate = intervention.getParams().getValue("falsePositive").getValue();
                testFalseNegativeRate = intervention.getParams().getValue("falseNegative").getValue();
                testCompliance = intervention.getParams().getValue("compliance").getValue();
                break;
            case ISOLATION:
                isolation = true;
                isolationCompliance = intervention.getParams().getValue("compliance").getValue();
                break;
            case TRACING_AND_QUARANTINE:
                tracingAndQuarantine = true;
                tracingEffectiveness = intervention.getParams().getValue("effectiveness").getValue();
                tracingWaitTime = intervention.getParams().getValue("wait").getValue();
                quarantineCompliance = intervention.getParams().getValue("coverage").getValue();
                break;
            case SCHOOL_CLOSURE:
                schoolClosures = (int) (schools.size() * intervention.getParams().getValue("closures").getValue());
                for (int i = 0; i < schoolClosures; i++) {
                    schools.get(i).setClosed(true);
                }
                break;
            case UNIVERSITY_CLOSURE:
                universityClosures = (int) (universities.size()
                        * intervention.getParams().getValue("closures").getValue());
                for (int i = 0; i < universityClosures; i++) {
                    universities.get(i).setClosed(true);
                }
                break;
            case WORKPLACE_CLOSURE:
                nonEssentialWorkplaceClosures = (int) (nonEssentialWorkplaces.size()
                        * intervention.getParams().getValue("closures").getValue());
                for (int i = 0; i < nonEssentialWorkplaceClosures; i++) {
                    nonEssentialWorkplaces.get(i).setClosed(true);
                }
                break;
            case LOCKDOWN:
                lockdown = true;
                lockdownCompliance = intervention.getParams().getValue("compliance").getValue();
                break;
            case VACCINATION:
                vaccination = true;
                vaccineNumber++;
                vaccinationSusceptibilityReduction = intervention.getParams().getValue("susceptibilityReduction")
                        .getValue();
                vaccinationSeverityReduction = intervention.getParams().getValue("severityReduction").getValue();
                vaccinationRate = intervention.getParams().getValue("rate").getValue();
                vaccinationCompliance = intervention.getParams().getValue("compliance").getValue();
                break;
        }
    }

    private void endIntervention(InterventionParam intervention) {
        switch (intervention.getType()) {
            case MASKS:
                masks = false;
                break;
            case SOCIAL_DISTANCING:
                socialDistancing = false;
                break;
            case TESTING:
                testing = false;
                break;
            case ISOLATION:
                isolation = false;
                break;
            case TRACING_AND_QUARANTINE:
                tracingAndQuarantine = false;
                break;
            case SCHOOL_CLOSURE:
                for (int i = 0; i < schoolClosures; i++) {
                    schools.get(i).setClosed(false);
                }
                break;
            case UNIVERSITY_CLOSURE:
                for (int i = 0; i < universityClosures; i++) {
                    universities.get(i).setClosed(false);
                }
                break;
            case WORKPLACE_CLOSURE:
                for (int i = 0; i < nonEssentialWorkplaceClosures; i++) {
                    nonEssentialWorkplaces.get(i).setClosed(false);
                }
                break;
            case LOCKDOWN:
                lockdown = false;
                break;
            case VACCINATION:
                vaccination = false;
                break;
        }
    }
}
