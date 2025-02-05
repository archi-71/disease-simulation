package simulation.core;

import java.util.ArrayList;
import java.util.List;

import simulation.interventions.InterventionType;
import simulation.interventions.Interventions;
import simulation.params.InterventionParam;

public class SimulationOutput {

    private final int incidentCasePeriod = 3600;

    private List<List<Integer>> stateDistributionData;
    private List<List<Integer>> incidentCaseData;
    private List<List<Integer>> prevalentCaseData;
    private List<List<Integer>> cumulativeCaseData;
    private List<List<Integer>> hospitalisationData;
    private List<List<Integer>> deathData;
    private List<List<Integer>> vaccinationData;

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

    public List<List<Integer>> getStateDistibutionData() {
        return stateDistributionData;
    }

    public List<List<Integer>> getIncidentCaseData() {
        return incidentCaseData;
    }

    public List<List<Integer>> getPrevalentCaseData() {
        return prevalentCaseData;
    }

    public List<List<Integer>> getCumulativeCaseData() {
        return cumulativeCaseData;
    }

    public List<List<Integer>> getHospitalisationData() {
        return hospitalisationData;
    }

    public List<List<Integer>> getDeathData() {
        return deathData;
    }

    public List<List<Integer>> getVaccinationData() {
        return vaccinationData;
    }

    public int getVaccineNumber() {
        return vaccinationCounts.size();
    }

    public void setSusceptibleNum(int susceptibleNum) {
        this.susceptibleNum = susceptibleNum;
    }

    public SimulationOutput(Interventions interventions) {
        // Initialise count for each vaccine
        vaccinationCounts = new ArrayList<Integer>();
        for (InterventionParam intervention : interventions.getParameters().getInterventions()) {
            if (intervention.getType() == InterventionType.VACCINATION) {
                vaccinationCounts.add(0);
            }
        }
        reset();
    }

    public void countSusceptibleToExposed() {
        susceptibleNum--;
        exposedNum++;
        incidentCases++;
        prevalentCases++;
        cumulativeCases++;
    }

    public void countExposedToInfectious() {
        exposedNum--;
        infectiousNum++;
    }

    public void countInfectiousToAsymptomatic() {
        infectiousNum--;
        asymptomaticNum++;
    }

    public void countInfectiousToSymptomaticMild() {
        infectiousNum--;
        symptomaticMildNum++;
    }

    public void countSymptomaticMildToSymptomaticSevere() {
        symptomaticMildNum--;
        symptomaticSevereNum++;
    }

    public void countSymptomaticSevereToDeceased() {
        symptomaticSevereNum--;
        deceasedNum++;
        prevalentCases--;
    }

    public void countAsymptomaticToRecovered() {
        asymptomaticNum--;
        recoveredNum++;
        prevalentCases--;
    }

    public void countSymptomaticMildToRecovered() {
        symptomaticMildNum--;
        recoveredNum++;
        prevalentCases--;
    }

    public void countSymptomaticSevereToRecovered() {
        symptomaticSevereNum--;
        recoveredNum++;
        prevalentCases--;
    }

    public void countHospitalAdmission() {
        hospitalisedNum++;
    }

    public void countHospitalDischarge() {
        hospitalisedNum--;
    }

    public void countVaccination(int vaccineNumber) {
        vaccinationCounts.set(vaccineNumber - 1, vaccinationCounts.get(vaccineNumber - 1) + 1);
    }

    public void step(int time, int day) {
        int timestamp = day * Simulation.dayLength + time;

        stateDistributionData.add(new ArrayList<>(List.of(
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
            incidentCaseData.add(new ArrayList<>(List.of(timestamp, incidentCases)));
            incidentCases = 0;
        }
        prevalentCaseData.add(new ArrayList<>(List.of(timestamp, prevalentCases)));
        cumulativeCaseData.add(new ArrayList<>(List.of(timestamp, cumulativeCases)));
        hospitalisationData.add(new ArrayList<>(List.of(timestamp, hospitalisedNum)));
        deathData.add(new ArrayList<>(List.of(timestamp, deceasedNum)));

        List<Integer> vaccinationEntry = new ArrayList<Integer>();
        vaccinationEntry.add(timestamp);
        vaccinationEntry.addAll(vaccinationCounts);
        vaccinationData.add(vaccinationEntry);
    }

    public void reset() {
        stateDistributionData = new ArrayList<>();
        incidentCaseData = new ArrayList<>();
        prevalentCaseData = new ArrayList<>();
        cumulativeCaseData = new ArrayList<>();
        hospitalisationData = new ArrayList<>();
        deathData = new ArrayList<>();
        vaccinationData = new ArrayList<>();

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
}
