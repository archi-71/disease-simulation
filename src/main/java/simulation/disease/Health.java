package simulation.disease;

import simulation.core.SimulationOutput;
import simulation.interventions.Interventions;
import simulation.params.DiseaseParams;
import simulation.population.Activity;
import simulation.population.AgeGroup;
import simulation.population.Individual;

/**
 * Class to handle an individual's health throughout the simulation.
 * One instance is associated with each individual.
 */
public class Health {

    // Required simulation components
    private DiseaseParams params;
    private Interventions interventions;
    private SimulationOutput output;
    private Individual individual;

    // Health state
    private HealthState state;
    private float timeInState;
    private float timeInHospital;
    private float infectiousness;
    private boolean diagnosed;

    // Intervention state
    private boolean isolating;
    private boolean testResultPending;
    private float timeSinceTest;
    private boolean tracePending;
    private float timeSinceContact;
    private boolean quarantining;
    private float timeInQuarantine;
    private int vaccineNumber;
    private float vaccineTransmissibilityMultiplier;
    private float vaccineSeverityMultiplier;

    // Disease parameters
    private float symptomaticProbability;
    private float severeSymptomaticProbability;
    private float mortalityProbability;
    private float exposedToInfectiousPeriod;
    private float infectiousToSymptomaticPeriod;
    private float mildToSevereSymptomaticPeriod;
    private float severeSymptomaticToDeathPeriod;
    private float asymptomaticToRecoveredPeriod;
    private float mildSymptomaticToRecoveredPeriod;
    private float severeSymptomaticToRecoveredPeriod;

    // Random values
    private float symptomMildness; // lack of severity in symptoms (0 = death, 1 = asymptomatic)
    private float interventionResistance; // non-compliance with interventions (0 = full compliance, 1 = no compliance)

    /**
     * Construct a new Health object for a given individual.
     * 
     * @param params        Disease parameters
     * @param interventions Interventions
     * @param output        Output
     * @param individual    Associated individual
     */
    public Health(DiseaseParams params, Interventions interventions, SimulationOutput output, Individual individual) {
        this.params = params;
        this.interventions = interventions;
        this.output = output;
        this.individual = individual;
        reset();
    }

    /**
     * Get the disease parameters
     * 
     * @return Disease parameters
     */
    public DiseaseParams getParams() {
        return params;
    }

    /**
     * Get the individual's current health state
     * @return Health state
     */
    public HealthState getState() {
        return state;
    }

    /**
     * Get the individual's current infectiousness
     * 
     * @return Infectiousness
     */
    public float getInfectiousness() {
        return infectiousness;
    }

    /**
     * Get whether the individual has been diagnosed with the disease
     * 
     * @return True if the individual is diagnosed
     */
    public boolean isDiagnosed() {
        return diagnosed;
    }

    /**
     * Get the individual's personal resistance to interventions
     * 
     * @return Intervention resistance
     */
    public float getInterventionResistance() {
        return interventionResistance;
    }

    /**
     * Check if the individual is isolating or quarantining
     * 
     * @return True if the individual is isolating or quarantining
     */
    public boolean isSelfIsolating() {
        return isolating || quarantining;
    }

    /**
     * Check if lockdown is active and the individual is compliant
     * 
     * @return True if the individual is in lockdown
     */
    public boolean inLockdown() {
        return interventions.isLockdownActive() && interventionResistance < interventions.getLockdownCompliance();
    }

    /**
     * Vaccinate the individual if they are compliant and not already vaccinated
     * with the current vaccine
     */
    public void vaccinate() {
        if (interventionResistance < interventions.getVaccinationCompliance()
                && vaccineNumber < interventions.getVaccineNumber()) {
            vaccineNumber = interventions.getVaccineNumber();
            vaccineTransmissibilityMultiplier = 1 - interventions.getVaccinationSusceptibilityReduction();
            vaccineSeverityMultiplier = 1 - interventions.getVaccinationSeverityReduction();
            output.countVaccination(vaccineNumber);
        }
    }

    /**
     * Transition to a new health state
     * 
     * @param newState New health state
     */
    public void transition(HealthState newState) {
        state = newState;
        timeInState = 0;
    }

    /**
     * Update health of the individual based on disease model and interventions
     * 
     * @param timeStep Time elapsed since last update
     */
    public void step(float timeStep) {
        // Update time in current state
        timeInState += timeStep;

        // Handle isolation, testing, and quarantine if active and compliance is met
        if (interventions.isIsolationActive() && interventionResistance < interventions.getIsolationCompliance()) {
            isolationUpdate();
        }
        if (interventions.isTestingActive() && interventionResistance < interventions.getTestCompliance()) {
            testUpdate(timeStep);
        }
        if (interventions.isTracingAndQuarantineActive()
                && interventionResistance < interventions.getQuarantineCompliance()) {
            traceUpdate(timeStep);
            quarantineUpdate(timeStep);
        }

        // Update health state based on current state and time in state
        switch (state) {
            case SUSCEPTIBLE:
                // Calculate infectivity as the total infectiousness of close contacts
                float infectivity = 0;
                for (Individual contact : individual.getContacts()) {
                    // Check if contact is 'close' based on social distancing, and skip if not
                    if (interventions.isSocialDistancingActive()
                            && Math.max(interventionResistance,
                                    contact.getHealth().getInterventionResistance()) < interventions
                                            .getSocialDistancingCompliance()
                            && Math.random() < interventions.getSocialDistancingEffectiveness()) {
                        continue;
                    }
                    // Mark close contact for tracing if conditions are met
                    if (interventions.isTracingAndQuarantineActive()
                            && interventionResistance < interventions.getQuarantineCompliance()
                            && contact.getHealth().isDiagnosed()
                            && !quarantining
                            && Math.random() < interventions.getTracingEffectiveness()) {
                        tracePending = true;
                        timeSinceContact = 0;
                    }
                    // Get contact's base infectiousness
                    float infectiousness = contact.getHealth().getInfectiousness();
                    // Adjust infectiousness based on exhalation mask protection
                    if (interventions.isMaskWearingActive()) {
                        infectiousness *= contact.getHealth().getInterventionResistance() < interventions
                                .getMaskCompliance()
                                        ? 1 - interventions.getMaskExhalationProtection()
                                        : 1;
                    }
                    // Add infectiousness to total infecticity
                    infectivity += infectiousness;
                }
                // Get base transmissibility
                float transmissibility = params.getTransmissionRate().getValue() * timeStep;
                // Adjust transmissibility based on infectivity (which may be zero)
                transmissibility *= infectivity;
                // Adjust transmissibility based on inhalation mask protection
                if (interventions.isMaskWearingActive()) {
                    transmissibility *= interventionResistance < interventions.getMaskCompliance()
                            ? 1 - interventions.getMaskInhalationProtection()
                            : 1;
                }
                // Adjust transmissibility based on vaccination
                transmissibility *= vaccineTransmissibilityMultiplier;
                // Transmit disease with some probability based on calculated transmissibility
                if (Math.random() < 1 - Math.exp(-transmissibility)) {
                    transition(HealthState.EXPOSED);
                    output.countSusceptibleToExposed();
                }
                break;
            case EXPOSED:
                // Check if latency period has ended
                if (timeInState >= exposedToInfectiousPeriod) {
                    // Transition to infectious state
                    transition(HealthState.INFECTIOUS);
                    output.countExposedToInfectious();
                }
                break;
            case INFECTIOUS:
                // Calculate infectiousness based on time in state
                infectiousness = timeInState / infectiousToSymptomaticPeriod;
                // Check if incubation period has ended
                if (timeInState >= infectiousToSymptomaticPeriod) {
                    infectiousness = 1;
                    // Branch based on symptom severity and vaccine multiplier
                    if (symptomMildness < symptomaticProbability * vaccineSeverityMultiplier) {
                        diagnosed = true;
                        // Transition to mild symptomatic state
                        transition(HealthState.SYMPTOMATIC_MILD);
                        output.countInfectiousToSymptomaticMild();
                    } else {
                        // Transition to asymptomatic state
                        transition(HealthState.ASYMPTOMATIC);
                        output.countInfectiousToAsymptomatic();
                    }
                }
                break;
            case ASYMPTOMATIC:
                // Calculate infectiousness based on time in state
                infectiousness = 1 - (timeInState / asymptomaticToRecoveredPeriod);
                // Check if asymptomatic period has ended
                if (timeInState >= asymptomaticToRecoveredPeriod) {
                    infectiousness = 0;
                    // Transition to recovered state
                    transition(HealthState.RECOVERED);
                    output.countAsymptomaticToRecovered();
                }
                break;
            case SYMPTOMATIC_MILD:
                // Branch based on severe symptom probability and vaccine multiplier
                if (symptomMildness < severeSymptomaticProbability * vaccineSeverityMultiplier) {
                    // Check if symptom worsening period has ended
                    if (timeInState >= mildToSevereSymptomaticPeriod) {
                        // Transition to severe symptomatic state
                        transition(HealthState.SYMPTOMATIC_SEVERE);
                        output.countSymptomaticMildToSymptomaticSevere();
                    }
                } else {
                    // Calculate infectiousness based on time in state
                    infectiousness = 1 - (timeInState / mildSymptomaticToRecoveredPeriod);
                    // Check if recovery period has ended
                    if (timeInState >= mildSymptomaticToRecoveredPeriod) {
                        infectiousness = 0;
                        diagnosed = false;
                        // Transition to recovered state
                        transition(HealthState.RECOVERED);
                        output.countSymptomaticMildToRecovered();
                    }
                }
                break;
            case SYMPTOMATIC_SEVERE:
                // Calculate mortality multiplier based on hospitalisation time
                float mortalityMultiplier = params.getRelativeMortalityWithoutHospitalisation().getValue();
                if (individual.getActivity() == Activity.HOPSITALISATION) {
                    timeInHospital += timeStep;
                    float hospitalFactor = (float) ((timeInState - timeInHospital) / timeInState);
                    mortalityMultiplier = 1 + (mortalityMultiplier - 1) * hospitalFactor;
                }
                // Branch based on mortality probability and mortality multiplier
                if (symptomMildness < mortalityProbability * mortalityMultiplier) {
                    // Calculate infectiousness based on time in state
                    infectiousness = 1 - (timeInState / severeSymptomaticToRecoveredPeriod);
                    // Check if dying period has ended
                    if (timeInState >= severeSymptomaticToDeathPeriod) {
                        diagnosed = false;
                        // Transition to deceased state
                        transition(HealthState.DECEASED);
                        if (individual.getActivity() == Activity.HOPSITALISATION) {
                            individual.getHospital().dischargePatient(output);
                        }
                        output.countSymptomaticSevereToDeceased();
                    }
                } else {
                    // Calculate infectiousness based on time in state
                    infectiousness = 1 - (timeInState / severeSymptomaticToRecoveredPeriod);
                    // Check if recovery period has ended
                    if (timeInState >= severeSymptomaticToRecoveredPeriod) {
                        infectiousness = 0;
                        diagnosed = false;
                        // Transition to recovered state
                        transition(HealthState.RECOVERED);
                        output.countSymptomaticSevereToRecovered();
                    }
                }
                break;
            default:
        }
    }

    /**
     * Reset health/intervention states and initialise personal parameters
     */
    public void reset() {
        // Reset health and intervention states
        transition(HealthState.SUSCEPTIBLE);
        timeInState = 0;
        timeInHospital = 0;
        infectiousness = 0;
        diagnosed = false;
        isolating = false;
        quarantining = false;
        testResultPending = false;
        tracePending = false;
        vaccineTransmissibilityMultiplier = 1;
        vaccineSeverityMultiplier = 1;

        // Prepare disease parameters
        AgeGroup ageGroup = AgeGroup.getAgeGroup(individual.getAge());
        symptomaticProbability = params.getSymptomaticProbability().getValue(ageGroup).getValue();
        severeSymptomaticProbability = params.getSevereSymptomaticProbability().getValue(ageGroup).getValue();
        mortalityProbability = params.getMortalityProbability().getValue(ageGroup).getValue();
        exposedToInfectiousPeriod = params.getExposedToInfectiousPeriod().sample();
        infectiousToSymptomaticPeriod = params.getInfectiousToSymptomaticPeriod().sample();
        mildToSevereSymptomaticPeriod = params.getMildToSevereSymptomaticPeriod().sample();
        severeSymptomaticToDeathPeriod = params.getSevereSymptomaticToDeathPeriod().sample();
        asymptomaticToRecoveredPeriod = params.getAsymptomaticToRecoveredPeriod().sample();
        mildSymptomaticToRecoveredPeriod = params.getMildSymptomaticToRecoveredPeriod().sample();
        severeSymptomaticToRecoveredPeriod = params.getSevereSymptomaticToRecoveredPeriod().sample();

        // Pre-generate random values for symptom mildness and intervention resistance
        symptomMildness = (float) Math.random();
        interventionResistance = (float) Math.random();
    }

    /**
     * Mark individual for isolation if they become diagnosed
     */
    private void isolationUpdate() {
        isolating = diagnosed;
    }

    /**
     * Update testing procedure for the individual
     * 
     * @param timeStep Time elapsed since last update
     */
    private void testUpdate(float timeStep) {
        if (testResultPending) {
            // Wait for test result
            timeSinceTest += timeStep;
            if (timeSinceTest >= interventions.getTestWaitTime()) {
                // Generate test result based on false positive/negative rates
                if (state.isInfected() && Math.random() > interventions.getTestFalseNegativeRate()) {
                    diagnosed = true;
                } else if (!state.isInfected() && Math.random() < interventions.getTestFalsePositiveRate()) {
                    diagnosed = true;
                } else {
                    diagnosed = false;
                }
                testResultPending = false;
            }
        } else {
            // Randomly test individual based on average frequency
            if (Math.random() < interventions.getTestFrequency() * timeStep) {
                testResultPending = true;
                timeSinceTest = 0;
            }
        }
    }

    /**
     * Update contact tracing procedure for the individual
     * 
     * @param timeStep Time elapsed since last update
     */
    private void traceUpdate(float timeStep) {
        if (tracePending) {
            // Wait for tracing to complete
            timeSinceContact += timeStep;
            if (timeSinceContact >= interventions.getTracingWaitTime()) {
                // Mark close contact for quarantining
                tracePending = false;
                quarantining = true;
                timeInQuarantine = 0;
            }
        }
    }

    /**
     * Update quarantine prodecure for the individual
     * 
     * @param timeStep Time elapsed since last update
     */
    private void quarantineUpdate(float timeStep) {
        if (quarantining) {
            // Wait for quarantine period to end
            timeInQuarantine += timeStep;
            if (timeInQuarantine >= interventions.getMinQuarantineTime()) {
                // End quarantine period
                quarantining = false;
            }
        }
    }
}
