package simulation.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import simulation.interventions.InterventionType;
import simulation.interventions.Interventions;
import simulation.params.InterventionParam;

/**
 * Class to store, update and retrieve simulation's output data
 */
public class SimulationOutput {

    // Time period over which to count incident cases
    private static final int INCIDENT_CASE_PERIOD = 86400; // = 1 day

    // Number of simulation runs set
    private int runs;

    // Data to be collected
    // Lists (runs) of lists (steps) of lists (data)
    private List<List<List<Integer>>> stateDistributionData;
    private List<List<List<Integer>>> incidentCaseData;
    private List<List<List<Integer>>> prevalentCaseData;
    private List<List<List<Integer>>> cumulativeCaseData;
    private List<List<List<Integer>>> hospitalisationData;
    private List<List<List<Integer>>> deathData;
    private List<List<List<Integer>>> vaccinationData;

    // Auxiliary counters to keep track of various numbers
    private int susceptibleNum;
    private int exposedNum;
    private int infectiousNum;
    private int asymptomaticNum;
    private int symptomaticMildNum;
    private int symptomaticSevereNum;
    private int deceasedNum;
    private int recoveredNum;
    private int hospitalisedNum;
    private int incidentCases;
    private int prevalentCases;
    private int cumulativeCases;
    private List<Integer> vaccinationCounts; // Distinct counts for each vaccine

    /**
     * Get state distribution data for a given run
     * 
     * @param run Run number
     * @return State distribution data for the given run
     */
    public List<List<Integer>> getStateDistibutionData(int run) {
        return stateDistributionData.get(run);
    }

    /**
     * Get incident case data for a given run
     * 
     * @param run Run number
     * @return Incident case data for the given run
     */
    public List<List<Integer>> getIncidentCaseData(int run) {
        return incidentCaseData.get(run);
    }

    /**
     * Get prevalent case data for a given run
     * 
     * @param run Run number
     * @return Prevalent case data for the given run
     */
    public List<List<Integer>> getPrevalentCaseData(int run) {
        return prevalentCaseData.get(run);
    }

    /**
     * Get cumulative case data for a given run
     * 
     * @param run Run number
     * @return Cumulative case data for the given run
     */
    public List<List<Integer>> getCumulativeCaseData(int run) {
        return cumulativeCaseData.get(run);
    }

    /**
     * Get hospitalisation data for a given run
     * 
     * @param run Run number
     * @return Hospitalisation data for the given run
     */
    public List<List<Integer>> getHospitalisationData(int run) {
        return hospitalisationData.get(run);
    }

    /**
     * Get death data for a given run
     * 
     * @param run Run number
     * @return Death data for the given run
     */
    public List<List<Integer>> getDeathData(int run) {
        return deathData.get(run);
    }

    /**
     * Get vaccination data for a given run
     * 
     * @param run Run number
     * @return Vaccination data for the given run
     */
    public List<List<Integer>> getVaccinationData(int run) {
        return vaccinationData.get(run);
    }

    /**
     * Get number of distinct vaccines to be simulated
     * 
     * @return Number of vaccines
     */
    public int getVaccineNumber() {
        return vaccinationCounts.size();
    }

    /**
     * Set number of susceptible individuals (for the start of the simulation)
     * 
     * @param susceptibleNum Number of susceptible individuals
     */
    public void setSusceptibleNum(int susceptibleNum) {
        this.susceptibleNum = susceptibleNum;
    }

    /**
     * Record susceptible-to-exposed transition
     */
    public synchronized void countSusceptibleToExposed() {
        susceptibleNum--;
        exposedNum++;
        incidentCases++;
        prevalentCases++;
        cumulativeCases++;
    }

    /**
     * Record exposed-to-infectious transition
     */
    public synchronized void countExposedToInfectious() {
        exposedNum--;
        infectiousNum++;
    }

    /**
     * Record infectious-to-asymptomatic transition
     */
    public synchronized void countInfectiousToAsymptomatic() {
        infectiousNum--;
        asymptomaticNum++;
    }

    /**
     * Record infectious-to-mildly-symptomatic transition
     */
    public synchronized void countInfectiousToSymptomaticMild() {
        infectiousNum--;
        symptomaticMildNum++;
    }

    /**
     * Record mildly-symptomatic-to-severely-symptomatic transition
     */
    public synchronized void countSymptomaticMildToSymptomaticSevere() {
        symptomaticMildNum--;
        symptomaticSevereNum++;
    }

    /**
     * Record severely-symptomatic-to-deceased transition
     */
    public synchronized void countSymptomaticSevereToDeceased() {
        symptomaticSevereNum--;
        deceasedNum++;
        prevalentCases--;
    }

    /**
     * Record asymptomatic-to-recovered transition
     */
    public synchronized void countAsymptomaticToRecovered() {
        asymptomaticNum--;
        recoveredNum++;
        prevalentCases--;
    }

    /**
     * Record mildly-symptomatic-to-recovered transition
     */
    public synchronized void countSymptomaticMildToRecovered() {
        symptomaticMildNum--;
        recoveredNum++;
        prevalentCases--;
    }

    /**
     * Record severely-symptomatic-to-recovered transition
     */
    public synchronized void countSymptomaticSevereToRecovered() {
        symptomaticSevereNum--;
        recoveredNum++;
        prevalentCases--;
    }

    /**
     * Record hospital admission
     */
    public synchronized void countHospitalAdmission() {
        hospitalisedNum++;
    }

    /**
     * Record hospital discharge
     */
    public synchronized void countHospitalDischarge() {
        hospitalisedNum--;
    }

    /**
     * Record vaccination of a given vaccine
     * 
     * @param vaccineNumber Vaccine ID number
     */
    public synchronized void countVaccination(int vaccineNumber) {
        vaccinationCounts.set(vaccineNumber - 1, vaccinationCounts.get(vaccineNumber - 1) + 1);
    }

    /**
     * Initialise simulation output data
     * 
     * @param runs          Number of simulation runs
     * @param interventions Interventions to be simulated
     */
    public void initialise(int runs, Interventions interventions) {
        this.runs = runs;

        // Initialise data lists
        stateDistributionData = new ArrayList<>();
        incidentCaseData = new ArrayList<>();
        prevalentCaseData = new ArrayList<>();
        cumulativeCaseData = new ArrayList<>();
        hospitalisationData = new ArrayList<>();
        deathData = new ArrayList<>();
        vaccinationData = new ArrayList<>();

        // Initialise count for each vaccine
        vaccinationCounts = new ArrayList<Integer>();
        for (InterventionParam intervention : interventions.getParameters().getInterventions()) {
            if (intervention.getType() == InterventionType.VACCINATION) {
                vaccinationCounts.add(0);
            }
        }

        reset();
    }

    /**
     * Update output data
     * 
     * @param time Current time of day
     * @param day  Current day
     * @param run  Current run
     */
    public void step(int time, int day, int run) {
        // Calculate timestamp since start of run
        int timestamp = day * Simulation.DAY_LENGTH + time;

        // Update state distribution data
        stateDistributionData.get(run).add(new ArrayList<>(Arrays.asList(
                timestamp,
                susceptibleNum,
                exposedNum,
                infectiousNum,
                asymptomaticNum,
                symptomaticMildNum,
                symptomaticSevereNum,
                deceasedNum,
                recoveredNum)));

        // Update incident case data
        if (timestamp % INCIDENT_CASE_PERIOD == 0) {
            incidentCaseData.get(run).add(new ArrayList<>(Arrays.asList(timestamp, incidentCases)));
            incidentCases = 0;
        }

        // Update prevalent case data
        prevalentCaseData.get(run).add(new ArrayList<>(Arrays.asList(timestamp, prevalentCases)));

        // Update cumulative case data
        cumulativeCaseData.get(run).add(new ArrayList<>(Arrays.asList(timestamp, cumulativeCases)));

        // Update hospitalisation data
        hospitalisationData.get(run).add(new ArrayList<>(Arrays.asList(timestamp, hospitalisedNum)));

        // Update death data
        deathData.get(run).add(new ArrayList<>(Arrays.asList(timestamp, deceasedNum)));

        // Update vaccination data
        List<Integer> vaccinationEntry = new ArrayList<Integer>();
        vaccinationEntry.add(timestamp);
        vaccinationEntry.addAll(vaccinationCounts);
        vaccinationData.get(run).add(vaccinationEntry);
    }

    /**
     * Reset output data for new simulation
     */
    public void reset() {
        // Clear all data
        stateDistributionData.clear();
        incidentCaseData.clear();
        prevalentCaseData.clear();
        cumulativeCaseData.clear();
        hospitalisationData.clear();
        deathData.clear();
        vaccinationData.clear();

        // Re-initialise data lists
        for (int i = 0; i < runs + 1; i++) {
            stateDistributionData.add(new ArrayList<>());
            incidentCaseData.add(new ArrayList<>());
            prevalentCaseData.add(new ArrayList<>());
            cumulativeCaseData.add(new ArrayList<>());
            hospitalisationData.add(new ArrayList<>());
            deathData.add(new ArrayList<>());
            vaccinationData.add(new ArrayList<>());
        }

        // Reset counters
        resetRun();
    }

    /**
     * Reset counters for a new run
     */
    public void resetRun() {
        susceptibleNum = 0;
        exposedNum = 0;
        infectiousNum = 0;
        asymptomaticNum = 0;
        symptomaticMildNum = 0;
        symptomaticSevereNum = 0;
        deceasedNum = 0;
        recoveredNum = 0;
        hospitalisedNum = 0;
        incidentCases = 0;
        prevalentCases = 0;
        cumulativeCases = 0;
        for (int i = 0; i < vaccinationCounts.size(); i++) {
            vaccinationCounts.set(i, 0);
        }
    }

    /**
     * Average output data across all runs at the end of the simulation
     */
    public void averageRuns() {
        // Average state distribution data
        for (int i = 0; i < stateDistributionData.get(0).size(); i++) {
            int susceptibleTotal = 0;
            int exposedTotal = 0;
            int infectiousTotal = 0;
            int asymptomaticTotal = 0;
            int symptomaticMildTotal = 0;
            int symptomaticSevereTotal = 0;
            int deceasedTotal = 0;
            int recoveredTotal = 0;
            for (int r = 0; r < runs; r++) {
                List<Integer> record = stateDistributionData.get(r).get(i);
                susceptibleTotal += record.get(1);
                exposedTotal += record.get(2);
                infectiousTotal += record.get(3);
                asymptomaticTotal += record.get(4);
                symptomaticMildTotal += record.get(5);
                symptomaticSevereTotal += record.get(6);
                deceasedTotal += record.get(7);
                recoveredTotal += record.get(8);
            }
            List<Integer> averageRecord = new ArrayList<>();
            averageRecord.add(stateDistributionData.get(0).get(i).get(0));
            averageRecord.add(Math.round((float) susceptibleTotal / runs));
            averageRecord.add(Math.round((float) exposedTotal / runs));
            averageRecord.add(Math.round((float) infectiousTotal / runs));
            averageRecord.add(Math.round((float) asymptomaticTotal / runs));
            averageRecord.add(Math.round((float) symptomaticMildTotal / runs));
            averageRecord.add(Math.round((float) symptomaticSevereTotal / runs));
            averageRecord.add(Math.round((float) deceasedTotal / runs));
            averageRecord.add(Math.round((float) recoveredTotal / runs));
            stateDistributionData.get(runs).add(averageRecord);
        }

        // Average incident case data
        for (int i = 0; i < incidentCaseData.get(0).size(); i++) {
            int incidentTotal = 0;
            for (int r = 0; r < runs; r++) {
                incidentTotal += incidentCaseData.get(r).get(i).get(1);
            }
            List<Integer> averageRecord = new ArrayList<>();
            averageRecord.add(incidentCaseData.get(0).get(i).get(0));
            averageRecord.add(Math.round((float) incidentTotal / runs));
            incidentCaseData.get(runs).add(averageRecord);
        }

        // Average prevalent case data
        for (int i = 0; i < prevalentCaseData.get(0).size(); i++) {
            int prevalentTotal = 0;
            for (int r = 0; r < runs; r++) {
                prevalentTotal += prevalentCaseData.get(r).get(i).get(1);
            }
            List<Integer> averageRecord = new ArrayList<>();
            averageRecord.add(prevalentCaseData.get(0).get(i).get(0));
            averageRecord.add(Math.round((float) prevalentTotal / runs));
            prevalentCaseData.get(runs).add(averageRecord);
        }

        // Average cumulative case data
        for (int i = 0; i < cumulativeCaseData.get(0).size(); i++) {
            int cumulativeTotal = 0;
            for (int r = 0; r < runs; r++) {
                cumulativeTotal += cumulativeCaseData.get(r).get(i).get(1);
            }
            List<Integer> averageRecord = new ArrayList<>();
            averageRecord.add(cumulativeCaseData.get(0).get(i).get(0));
            averageRecord.add(Math.round((float) cumulativeTotal / runs));
            cumulativeCaseData.get(runs).add(averageRecord);
        }

        // Average hospitalisation data
        for (int i = 0; i < hospitalisationData.get(0).size(); i++) {
            int hospitalisationTotal = 0;
            for (int r = 0; r < runs; r++) {
                hospitalisationTotal += hospitalisationData.get(r).get(i).get(1);
            }
            List<Integer> averageRecord = new ArrayList<>();
            averageRecord.add(hospitalisationData.get(0).get(i).get(0));
            averageRecord.add(Math.round((float) hospitalisationTotal / runs));
            hospitalisationData.get(runs).add(averageRecord);
        }

        // Average death data
        for (int i = 0; i < deathData.get(0).size(); i++) {
            int deathTotal = 0;
            for (int r = 0; r < runs; r++) {
                deathTotal += deathData.get(r).get(i).get(1);
            }
            List<Integer> averageRecord = new ArrayList<>();
            averageRecord.add(deathData.get(0).get(i).get(0));
            averageRecord.add(Math.round((float) deathTotal / runs));
            deathData.get(runs).add(averageRecord);
        }

        // Average vaccination data
        for (int i = 0; i < vaccinationData.get(0).size(); i++) {
            List<Integer> vaccineAverages = new ArrayList<Integer>();
            for (int j = 0; j < vaccinationCounts.size(); j++) {
                int vaccineTotal = 0;
                for (int r = 0; r < runs; r++) {
                    vaccineTotal += vaccinationData.get(r).get(i).get(j + 1);
                }
                vaccineAverages.add(Math.round((float) vaccineTotal / runs));
            }
            List<Integer> averageRecord = new ArrayList<>();
            averageRecord.add(vaccinationData.get(0).get(i).get(0));
            averageRecord.addAll(vaccineAverages);
            vaccinationData.get(runs).add(averageRecord);
        }
    }
}
