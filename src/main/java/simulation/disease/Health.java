package simulation.disease;

import simulation.core.SimulationOutput;
import simulation.interventions.Interventions;
import simulation.params.DiseaseParams;
import simulation.population.Activity;
import simulation.population.AgeGroup;
import simulation.population.Individual;

public class Health {

    private DiseaseParams params;
    private Interventions interventions;
    private SimulationOutput output;
    private Individual individual;

    // Health state
    private HealthState state;
    private float timeInState;
    private float timeInHospital;
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
    private float severity; // extent to which symptoms will progress (0 = no symptoms, 1 = death)
    private float awareness; // compliance with interventions (0 = no compliance, 1 = full compliance)

    public Health(DiseaseParams params, Interventions interventions, SimulationOutput output, Individual individual) {
        this.params = params;
        this.interventions = interventions;
        this.output = output;
        this.individual = individual;
        reset();
    }

    public DiseaseParams getParams() {
        return params;
    }

    public HealthState getState() {
        return state;
    }

    public boolean isDiagnosed() {
        return diagnosed;
    }

    public float getAwareness() {
        return awareness;
    }

    public boolean isSelfIsolating() {
        return isolating || quarantining;
    }

    public boolean inLockdown() {
        return interventions.isLockdownActive() && awareness > 1 - interventions.getLockdownCompliance();
    }

    public void vaccinate() {
        if (awareness > 1 - interventions.getVaccinationCompliance()
                && vaccineNumber < interventions.getVaccineNumber()) {
            vaccineNumber = interventions.getVaccineNumber();
            vaccineTransmissibilityMultiplier = 1 - interventions.getVaccinationSusceptibilityReduction();
            vaccineSeverityMultiplier = 1 - interventions.getVaccinationSeverityReduction();
            output.countVaccination(vaccineNumber);
        }
    }

    public void transition(HealthState newState) {
        state = newState;
        timeInState = 0;
    }

    public void step(float timeStep) {
        timeInState += timeStep;
        if (interventions.isIsolationActive() && awareness > 1 - interventions.getIsolationCompliance()) {
            isolationUpdate();
        }
        if (interventions.isTestingActive() && awareness > 1 - interventions.getTestCompliance()) {
            testUpdate(timeStep);
        }
        if (interventions.isTracingAndQuarantineActive() && awareness > 1 - interventions.getQuarantineCompliance()) {
            traceUpdate(timeStep);
            quarantineUpdate(timeStep);
        }
        switch (state) {
            case SUSCEPTIBLE:
                for (Individual contact : individual.getContacts()) {
                    // Check if contact is 'close' based on social distancing, and skip if not
                    if (interventions.isSocialDistancingActive()
                            && Math.max(awareness, contact.getHealth().getAwareness()) > 1 - interventions
                                    .getSocialDistancingCompliance()
                            && Math.random() < interventions.getSocialDistancingEffectiveness()) {
                        continue;
                    }
                    // Mark close contact for tracing if conditions are met
                    if (interventions.isTracingAndQuarantineActive()
                            && awareness > 1 - interventions.getQuarantineCompliance()
                            && contact.getHealth().isDiagnosed()
                            && !quarantining
                            && Math.random() < interventions.getTracingEffectiveness()) {
                        tracePending = true;
                        timeSinceContact = 0;
                    }
                    // Check for transmission from infectious contacts
                    if (contact.getHealth().getState().isInfectious()) {
                        // Get base transmissibility
                        float transmissibility = params.getTransmissionRate().getValue() * timeStep;
                        // Adjust transmissibility based on mask use
                        if (interventions.isMaskWearingActive()) {
                            transmissibility *= awareness > 1 - interventions.getMaskCompliance()
                                    ? 1 - interventions.getMaskIncomingProtection()
                                    : 1;
                            transmissibility *= contact.getHealth().getAwareness() > 1
                                    - interventions.getMaskCompliance()
                                            ? 1 - interventions.getMaskOutgoingProtection()
                                            : 1;
                        }
                        // Adjust transmissibility based on vaccination
                        transmissibility *= vaccineTransmissibilityMultiplier;
                        // Transmit disease with some probability
                        if (Math.random() < 1 - Math.exp(-transmissibility)) {
                            transition(HealthState.EXPOSED);
                            output.countSusceptibleToExposed();
                            break;
                        }
                    }
                }
                break;
            case EXPOSED:
                if (timeInState >= exposedToInfectiousPeriod) {
                    transition(HealthState.INFECTIOUS);
                    output.countExposedToInfectious();
                }
                break;
            case INFECTIOUS:
                if (timeInState >= infectiousToSymptomaticPeriod) {
                    if (severity * vaccineSeverityMultiplier > 1 - symptomaticProbability) {
                        diagnosed = true;
                        transition(HealthState.SYMPTOMATIC_MILD);
                        output.countInfectiousToSymptomaticMild();
                    } else {
                        transition(HealthState.ASYMPTOMATIC);
                        output.countInfectiousToAsymptomatic();
                    }
                }
                break;
            case ASYMPTOMATIC:
                if (timeInState >= asymptomaticToRecoveredPeriod) {
                    transition(HealthState.RECOVERED);
                    output.countAsymptomaticToRecovered();
                }
                break;
            case SYMPTOMATIC_MILD:
                if (severity * vaccineSeverityMultiplier > 1 - severeSymptomaticProbability) {
                    if (timeInState >= mildToSevereSymptomaticPeriod) {
                        transition(HealthState.SYMPTOMATIC_SEVERE);
                        output.countSymptomaticMildToSymptomaticSevere();
                    }
                } else {
                    if (timeInState >= mildSymptomaticToRecoveredPeriod) {
                        diagnosed = false;
                        transition(HealthState.RECOVERED);
                        output.countSymptomaticMildToRecovered();
                    }
                }
                break;
            case SYMPTOMATIC_SEVERE:
                float mortalityMultiplier = params.getRelativeMortalityWithoutHospitalisation().getValue();
                if (individual.getActivity() == Activity.HOPSITALISATION) {
                    timeInHospital += timeStep;
                    float hospitalFactor = (float) ((timeInState - timeInHospital) / timeInState);
                    mortalityMultiplier = 1 + (mortalityMultiplier - 1) * hospitalFactor;
                }
                if (severity * vaccineSeverityMultiplier > 1 - mortalityProbability * mortalityMultiplier) {
                    if (timeInState >= severeSymptomaticToDeathPeriod) {
                        diagnosed = false;
                        transition(HealthState.DECEASED);
                        if (individual.getActivity() == Activity.HOPSITALISATION) {
                            individual.getHospital().dischargePatient(output);
                        }
                        output.countSymptomaticSevereToDeceased();
                    }
                } else {
                    if (timeInState >= severeSymptomaticToRecoveredPeriod) {
                        diagnosed = false;
                        transition(HealthState.RECOVERED);
                        output.countSymptomaticSevereToRecovered();
                    }
                }
                break;
            default:
        }
    }

    public void reset() {
        transition(HealthState.SUSCEPTIBLE);
        timeInHospital = 0;
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

        // Pre-generate random values for severity and awareness
        severity = (float) Math.random();
        awareness = (float) Math.random();
    }

    private void isolationUpdate() {
        isolating = diagnosed;
    }

    private void testUpdate(float timeStep) {
        if (testResultPending) {
            timeSinceTest += timeStep;
            if (timeSinceTest >= interventions.getTestWaitTime()) {
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
            if (Math.random() < interventions.getTestFrequency() * timeStep) {
                testResultPending = true;
                timeSinceTest = 0;
            }
        }
    }

    private void traceUpdate(float timeStep) {
        if (tracePending) {
            timeSinceContact += timeStep;
            if (timeSinceContact >= interventions.getTracingWaitTime()) {
                tracePending = false;
                quarantining = true;
                timeInQuarantine = 0;
            }
        }
    }

    private void quarantineUpdate(float timeStep) {
        if (quarantining) {
            timeInQuarantine += timeStep;
            if (timeInQuarantine >= interventions.getMinQuarantineTime()) {
                quarantining = false;
            }
        }
    }
}
