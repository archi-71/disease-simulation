package simulation.interventions;

import java.util.List;
import java.util.PriorityQueue;

import simulation.params.InterventionParam;
import simulation.params.InterventionParams;
import simulation.core.InitialisationException;
import simulation.environment.Building;
import simulation.environment.Environment;

/**
 * Class manage intervention strategies
 */
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
    private float maskInhalationProtection;
    private float maskExhalationProtection;
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

    /**
     * Get the parameters for the interventions
     * 
     * @return Intervention parameters
     */
    public InterventionParams getParameters() {
        return parameters;
    }

    /**
     * Get whether mask wearing is active
     * 
     * @return True if mask wearing is active
     */
    public boolean isMaskWearingActive() {
        return masks;
    }

    /**
     * Get mask inhalation protection
     * 
     * @return Mask inhalation protection
     */
    public float getMaskInhalationProtection() {
        return maskInhalationProtection;
    }

    /**
     * Get mask exhalation protection
     * 
     * @return Mask exhalation protection
     */
    public float getMaskExhalationProtection() {
        return maskExhalationProtection;
    }

    /**
     * Get the mask wearing compliance
     * 
     * @return Mask wearing compliance
     */
    public float getMaskCompliance() {
        return maskCompliance;
    }

    /**
     * Get whether social distancing is active
     * 
     * @return True if social distancing is active
     */
    public boolean isSocialDistancingActive() {
        return socialDistancing;
    }

    /**
     * Get social distancing effectiveness
     * 
     * @return Social distancing effectiveness
     */
    public float getSocialDistancingEffectiveness() {
        return socialDistancingEffectiveness;
    }

    /**
     * Get social distancing compliance
     * 
     * @return Social distancing compliance
     */
    public float getSocialDistancingCompliance() {
        return socialDistancingCompliance;
    }

    /**
     * Get whether isolation is active
     * 
     * @return True if isolation is active
     */
    public boolean isIsolationActive() {
        return isolation;
    }

    /**
     * Get isolation compliance
     * 
     * @return Isolation compliance
     */
    public float getIsolationCompliance() {
        return isolationCompliance;
    }

    /**
     * Get whether testing is active
     * 
     * @return True if testing is active
     */
    public boolean isTestingActive() {
        return testing;
    }

    /**
     * Get testing frequency
     * 
     * @return Testing frequency
     */
    public float getTestFrequency() {
        return testFrequency;
    }

    /**
     * Get testing wait time
     * 
     * @return Testing wait time
     */
    public float getTestWaitTime() {
        return testWaitTime;
    }

    /**
     * Get testing false positive rate
     * 
     * @return Testing false positive rate
     */
    public float getTestFalsePositiveRate() {
        return testFalsePositiveRate;
    }

    /**
     * Get testing false negative rate
     * 
     * @return Testing false negative rate
     */
    public float getTestFalseNegativeRate() {
        return testFalseNegativeRate;
    }

    /**
     * Get testing compliance
     * 
     * @return Testing compliance
     */
    public float getTestCompliance() {
        return testCompliance;
    }

    /**
     * Get whether contact tracing & quarantine is active
     * 
     * @return True if contact tracing & quarantine is active
     */
    public boolean isTracingAndQuarantineActive() {
        return tracingAndQuarantine;
    }

    /**
     * Get tracing effectiveness
     * 
     * @return Tracing effectiveness
     */
    public float getTracingEffectiveness() {
        return tracingEffectiveness;
    }

    /**
     * Get tracing wait time
     * 
     * @return Tracing wait time
     */
    public float getTracingWaitTime() {
        return tracingWaitTime;
    }

    /**
     * Get minimum quarantine time
     * 
     * @return Minimum quarantine time
     */
    public float getMinQuarantineTime() {
        return minQuarantineTime;
    }

    /**
     * Get quarantine compliance
     * 
     * @return Quarantine compliance
     */
    public float getQuarantineCompliance() {
        return quarantineCompliance;
    }

    /**
     * Get whether lockdown is active
     * 
     * @return True if lockdown is active
     */
    public boolean isLockdownActive() {
        return lockdown;
    }

    /**
     * Get lockdown compliance
     * 
     * @return Lockdown compliance
     */
    public float getLockdownCompliance() {
        return lockdownCompliance;
    }

    /**
     * Get whether vaccination is active
     * 
     * @return True if vaccination is active
     */
    public boolean isVaccinationActive() {
        return vaccination;
    }

    /**
     * Get the current vaccine ID number
     * 
     * @return Vaccine ID number
     */
    public int getVaccineNumber() {
        return vaccineNumber;
    }

    /**
     * Get the vaccination susceptibility reduction
     * 
     * @return Vaccination susceptibility reduction
     */
    public float getVaccinationSusceptibilityReduction() {
        return vaccinationSusceptibilityReduction;
    }

    /**
     * Get the vaccination symptom severity reduction
     * 
     * @return Vaccination symptom severity reduction
     */
    public float getVaccinationSeverityReduction() {
        return vaccinationSeverityReduction;
    }

    /**
     * Get the vaccination rate
     * 
     * @return Vaccination rate
     */
    public float getVaccinationRate() {
        return vaccinationRate;
    }

    /**
     * Get the vaccination compliance
     * 
     * @return Vaccination compliance
     */
    public float getVaccinationCompliance() {
        return vaccinationCompliance;
    }

    /**
     * Initialise intervention system
     * 
     * @param params      Intervention parameters
     * @param environment Environment
     * @throws InitialisationException If invalid intervention parameters are
     *                                 provided
     */
    public void initialise(InterventionParams params, Environment environment) throws InitialisationException {
        // Validate intervention parameters
        for (InterventionParam intervention : params.getInterventions()) {
            if (intervention.getStart().getValue() > intervention.getEnd().getValue()) {
                throw new InitialisationException("Interventions cannot end before they start");
            }
        }

        parameters = params;

        // Retrieve building references for closure interventions
        schools = environment.getSchools();
        universities = environment.getUniversities();
        nonEssentialWorkplaces = environment.getNonEssentialWorkplaces();

        reset();
    }

    /**
     * Enable/disable interventions for the next given day
     * 
     * @param day Day of the simulation
     */
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

    /**
     * Reset intervention system for a new simulation run
     */
    public void reset() {
        // Reset intervention queues
        inactiveInterventions = new PriorityQueue<>((a, b) -> a.getStart().getValue() - b.getStart().getValue());
        activeInterventions = new PriorityQueue<>((a, b) -> a.getEnd().getValue() - b.getEnd().getValue());
        for (InterventionParam intervention : parameters.getInterventions()) {
            inactiveInterventions.add(intervention);
        }

        // Disable all interventions
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

        // Enable day 0 interventions (if any)
        step(0);
    }

    /**
     * Enbable a given intervention
     * 
     * @param intervention Intervention to enable
     */
    private void startIntervention(InterventionParam intervention) {
        switch (intervention.getType()) {
            case MASKS:
                masks = true;
                maskInhalationProtection = intervention.getParams().getValue("inhalationProtection").getValue();
                maskExhalationProtection = intervention.getParams().getValue("exhalationProtection").getValue();
                maskCompliance = intervention.getParams().getValue("compliance").getValue();
                break;
            case SOCIAL_DISTANCING:
                socialDistancing = true;
                socialDistancingEffectiveness = intervention.getParams().getValue("effectiveness").getValue();
                socialDistancingCompliance = intervention.getParams().getValue("compliance").getValue();
                break;
            case ISOLATION:
                isolation = true;
                isolationCompliance = intervention.getParams().getValue("compliance").getValue();
                break;
            case TESTING:
                testing = true;
                testFrequency = intervention.getParams().getValue("frequency").getValue();
                testWaitTime = intervention.getParams().getValue("wait").getValue();
                testFalsePositiveRate = intervention.getParams().getValue("falsePositive").getValue();
                testFalseNegativeRate = intervention.getParams().getValue("falseNegative").getValue();
                testCompliance = intervention.getParams().getValue("compliance").getValue();
                break;
            case TRACING_AND_QUARANTINE:
                tracingAndQuarantine = true;
                tracingEffectiveness = intervention.getParams().getValue("effectiveness").getValue();
                tracingWaitTime = intervention.getParams().getValue("wait").getValue();
                minQuarantineTime = intervention.getParams().getValue("time").getValue();
                quarantineCompliance = intervention.getParams().getValue("compliance").getValue();
                break;
            case SCHOOL_CLOSURE:
                // Close a proportion of school buildings
                schoolClosures = (int) (schools.size() * intervention.getParams().getValue("closures").getValue());
                for (int i = 0; i < schoolClosures; i++) {
                    schools.get(i).setClosed(true);
                }
                break;
            case UNIVERSITY_CLOSURE:
                // Close a proportion of university buildings
                universityClosures = (int) (universities.size()
                        * intervention.getParams().getValue("closures").getValue());
                for (int i = 0; i < universityClosures; i++) {
                    universities.get(i).setClosed(true);
                }
                break;
            case WORKPLACE_CLOSURE:
                // Close a proportion of non-essential workplace buildings
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

    /**
     * Disable a given intervention
     * 
     * @param intervention Intervention to disable
     */
    private void endIntervention(InterventionParam intervention) {
        switch (intervention.getType()) {
            case MASKS:
                masks = false;
                break;
            case SOCIAL_DISTANCING:
                socialDistancing = false;
                break;
            case ISOLATION:
                isolation = false;
                break;
            case TESTING:
                testing = false;
                break;
            case TRACING_AND_QUARANTINE:
                tracingAndQuarantine = false;
                break;
            case SCHOOL_CLOSURE:
                // Re-open a proportion of school buildings
                for (int i = 0; i < schoolClosures; i++) {
                    schools.get(i).setClosed(false);
                }
                break;
            case UNIVERSITY_CLOSURE:
                // Re-open a proportion of university buildings
                for (int i = 0; i < universityClosures; i++) {
                    universities.get(i).setClosed(false);
                }
                break;
            case WORKPLACE_CLOSURE:
                // Re-open a proportion of non-essential workplace buildings
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
