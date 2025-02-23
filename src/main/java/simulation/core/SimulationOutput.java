package simulation.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import simulation.interventions.InterventionType;
import simulation.interventions.Interventions;
import simulation.params.InterventionParam;

public class SimulationOutput {

    private final int incidentCasePeriod = 3600;

    private int runs;

    private List<List<List<Integer>>> stateDistributionData;
    private List<List<List<Integer>>> incidentCaseData;
    private List<List<List<Integer>>> prevalentCaseData;
    private List<List<List<Integer>>> cumulativeCaseData;
    private List<List<List<Integer>>> hospitalisationData;
    private List<List<List<Integer>>> deathData;
    private List<List<List<Integer>>> vaccinationData;

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
    private List<Integer> vaccinationCounts;

    public List<List<Integer>> getStateDistibutionData(int run) {
        return stateDistributionData.get(run);
    }

    public List<List<Integer>> getIncidentCaseData(int run) {
        return incidentCaseData.get(run);
    }

    public List<List<Integer>> getPrevalentCaseData(int run) {
        return prevalentCaseData.get(run);
    }

    public List<List<Integer>> getCumulativeCaseData(int run) {
        return cumulativeCaseData.get(run);
    }

    public List<List<Integer>> getHospitalisationData(int run) {
        return hospitalisationData.get(run);
    }

    public List<List<Integer>> getDeathData(int run) {
        return deathData.get(run);
    }

    public List<List<Integer>> getVaccinationData(int run) {
        return vaccinationData.get(run);
    }

    public int getVaccineNumber() {
        return vaccinationCounts.size();
    }

    public void setSusceptibleNum(int susceptibleNum) {
        this.susceptibleNum = susceptibleNum;
    }

    public synchronized void countSusceptibleToExposed() {
        susceptibleNum--;
        exposedNum++;
        incidentCases++;
        prevalentCases++;
        cumulativeCases++;
    }

    public synchronized void countExposedToInfectious() {
        exposedNum--;
        infectiousNum++;
    }

    public synchronized void countInfectiousToAsymptomatic() {
        infectiousNum--;
        asymptomaticNum++;
    }

    public synchronized void countInfectiousToSymptomaticMild() {
        infectiousNum--;
        symptomaticMildNum++;
    }

    public synchronized void countSymptomaticMildToSymptomaticSevere() {
        symptomaticMildNum--;
        symptomaticSevereNum++;
    }

    public synchronized void countSymptomaticSevereToDeceased() {
        symptomaticSevereNum--;
        deceasedNum++;
        prevalentCases--;
    }

    public synchronized void countAsymptomaticToRecovered() {
        asymptomaticNum--;
        recoveredNum++;
        prevalentCases--;
    }

    public synchronized void countSymptomaticMildToRecovered() {
        symptomaticMildNum--;
        recoveredNum++;
        prevalentCases--;
    }

    public synchronized void countSymptomaticSevereToRecovered() {
        symptomaticSevereNum--;
        recoveredNum++;
        prevalentCases--;
    }

    public synchronized void countHospitalAdmission() {
        hospitalisedNum++;
    }

    public synchronized void countHospitalDischarge() {
        hospitalisedNum--;
    }

    public synchronized void countVaccination(int vaccineNumber) {
        vaccinationCounts.set(vaccineNumber - 1, vaccinationCounts.get(vaccineNumber - 1) + 1);
    }

    public void initialise(int runs, Interventions interventions) {
        this.runs = runs;

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

    public void step(int time, int day, int run) {
        int timestamp = day * Simulation.DAY_LENGTH + time;

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

        if (timestamp % incidentCasePeriod == 0) {
            incidentCaseData.get(run).add(new ArrayList<>(Arrays.asList(timestamp, incidentCases)));
            incidentCases = 0;
        }
        prevalentCaseData.get(run).add(new ArrayList<>(Arrays.asList(timestamp, prevalentCases)));
        cumulativeCaseData.get(run).add(new ArrayList<>(Arrays.asList(timestamp, cumulativeCases)));
        hospitalisationData.get(run).add(new ArrayList<>(Arrays.asList(timestamp, hospitalisedNum)));
        deathData.get(run).add(new ArrayList<>(Arrays.asList(timestamp, deceasedNum)));

        List<Integer> vaccinationEntry = new ArrayList<Integer>();
        vaccinationEntry.add(timestamp);
        vaccinationEntry.addAll(vaccinationCounts);
        vaccinationData.get(run).add(vaccinationEntry);
    }

    public void reset() {
        stateDistributionData.clear();
        incidentCaseData.clear();
        prevalentCaseData.clear();
        cumulativeCaseData.clear();
        hospitalisationData.clear();
        deathData.clear();
        vaccinationData.clear();

        for (int i = 0; i < runs + 1; i++) {
            stateDistributionData.add(new ArrayList<>());
            incidentCaseData.add(new ArrayList<>());
            prevalentCaseData.add(new ArrayList<>());
            cumulativeCaseData.add(new ArrayList<>());
            hospitalisationData.add(new ArrayList<>());
            deathData.add(new ArrayList<>());
            vaccinationData.add(new ArrayList<>());
        }

        resetRun();
    }

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

    public void averageRuns() {
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
