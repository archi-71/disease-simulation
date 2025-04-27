package simulation.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import simulation.core.Simulation;
import simulation.core.SimulationOutput;
import simulation.disease.HealthState;

/**
 * Class for the UI's bottom data panel
 */
public class Data extends VBox {

    private Stage stage;
    private Simulation simulation;
    private int population;
    private int duration;
    private int currentRun;
    private boolean visualisation;

    // UI elements
    private TabPane tabPane;
    private ArrayList<StackedAreaGraph> stateDistributionGraphs;
    private ArrayList<LineGraph> incidentCaseGraphs;
    private ArrayList<LineGraph> prevalentCaseGraphs;
    private ArrayList<LineGraph> cumulativeCaseGraphs;
    private ArrayList<LineGraph> hospitalisationGraphs;
    private ArrayList<LineGraph> deathGraphs;
    private ArrayList<LineGraph> vaccinationGraphs;

    /**
     * Construct the data panel
     * 
     * @param stage      Stage reference
     * @param simulation Simulation
     */
    public Data(Stage stage, Simulation simulation) {
        this.stage = stage;
        this.simulation = simulation;
        visualisation = true;

        // Create graph lists
        stateDistributionGraphs = new ArrayList<>();
        incidentCaseGraphs = new ArrayList<>();
        prevalentCaseGraphs = new ArrayList<>();
        cumulativeCaseGraphs = new ArrayList<>();
        hospitalisationGraphs = new ArrayList<>();
        deathGraphs = new ArrayList<>();
        vaccinationGraphs = new ArrayList<>();

        // Create tabs
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        Tab stateDistributionTab = new Tab("State Distribution");
        Tab incidentCaseTab = new Tab("Incident Cases");
        Tab prevalentCaseTab = new Tab("Prevalent Cases");
        Tab cumulativeCaseTab = new Tab("Cumulative Cases");
        Tab hospitalisationTab = new Tab("Hospitalisations");
        Tab deathTab = new Tab("Deaths");
        Tab vaccinationTab = new Tab("Vaccinations");
        tabPane.getTabs().addAll(stateDistributionTab, incidentCaseTab, prevalentCaseTab, cumulativeCaseTab,
                hospitalisationTab, deathTab, vaccinationTab);
        getChildren().add(tabPane);
    }

    /**
     * Reset the data panel
     */
    public void reset() {
        population = simulation.getParameters().getPopulationParams().getPopulationSize().getValue();
        duration = simulation.getParameters().getDuration().getValue() * 86400;

        // If simulation is restarted, clear all graphs and add first run
        if (simulation.getRun() == 0) {
            stateDistributionGraphs.clear();
            incidentCaseGraphs.clear();
            prevalentCaseGraphs.clear();
            cumulativeCaseGraphs.clear();
            hospitalisationGraphs.clear();
            deathGraphs.clear();
            vaccinationGraphs.clear();
            addNewRun();
            currentRun = 0;
            setContent(currentRun);
        }
    }

    /**
     * Update the data panel
     */
    public void update() {
        // Skip if visualisation is disabled
        if (!visualisation) {
            return;
        }

        // Update graphs with new data
        SimulationOutput output = simulation.getOutput();
        int run = simulation.getRun();
        for (int r = currentRun; r <= run; r++) {
            stateDistributionGraphs.get(r).update(output.getStateDistibutionData(r));
            incidentCaseGraphs.get(r).update(output.getIncidentCaseData(r));
            prevalentCaseGraphs.get(r).update(output.getPrevalentCaseData(r));
            cumulativeCaseGraphs.get(r).update(output.getCumulativeCaseData(r));
            hospitalisationGraphs.get(r).update(output.getHospitalisationData(r));
            deathGraphs.get(r).update(output.getDeathData(r));
            vaccinationGraphs.get(r).update(output.getVaccinationData(r));

            // Add a new set of graphs if needed for a new run
            if (r < run) {
                addNewRun();
            }
        }

        // Switch to next tab if a new run has started
        if (currentRun < run) {
            currentRun = run;
            setContent(currentRun);
        }
    }

    /**
     * Set whether to update graphs live
     * 
     * @param visualisation True for live visualisation false to disable
     */
    public void setVisualisation(boolean visualisation) {
        this.visualisation = visualisation;
    }

    /**
     * Add a new set of graphs for a new run
     */
    private void addNewRun() {
        // State distribution graph
        StackedAreaGraph stateDistributionGraph = new StackedAreaGraph("State Distribution",
                "Population", population,
                duration);
        for (HealthState state : HealthState.values()) {
            stateDistributionGraph.addSeries(state.getName());
        }
        stateDistributionGraphs.add(stateDistributionGraph);

        // Incident case graph
        LineGraph incidentCaseGraph = new LineGraph("Incident Cases", "New Cases (per day)",
                population, duration);
        incidentCaseGraph.addSeries("Incident Cases", "red");
        incidentCaseGraphs.add(incidentCaseGraph);

        // Prevalent case graph
        LineGraph prevalentCaseGraph = new LineGraph("Prevalent Cases", "Current Cases", population,
                duration);
        prevalentCaseGraph.addSeries("Prevalent Cases", "red");
        prevalentCaseGraphs.add(prevalentCaseGraph);

        // Cumulative case graph
        LineGraph cumulativeCaseGraph = new LineGraph("Cumulative Cases", "Total Cases", population,
                duration);
        cumulativeCaseGraph.addSeries("Cumulative Cases", "red");
        cumulativeCaseGraphs.add(cumulativeCaseGraph);

        // Hospitalisation graph
        LineGraph hospitalisationGraph = new LineGraph("Hospitalisations", "Hospitalised Cases",
                population, duration);
        hospitalisationGraph.addSeries("Hospitalisations", "#B73A3A");
        hospitalisationGraphs.add(hospitalisationGraph);

        // Death graph
        LineGraph deathGraph = new LineGraph("Deaths", "Deaths", population, duration);
        deathGraph.addSeries("Deaths", "black");
        deathGraphs.add(deathGraph);

        // Vaccination graph
        LineGraph vaccinationGraph = new LineGraph("Vaccinations", "Vaccine Coverage", population,
                duration, true);
        for (int i = 0; i < simulation.getOutput().getVaccineNumber(); i++) {
            vaccinationGraph.addSeries("Vaccine " + (i + 1));
        }
        vaccinationGraphs.add(vaccinationGraph);
    }

    /**
     * Set the content of each tab for a given run
     * 
     * @param run Current run number
     */
    private void setContent(int run) {
        // State distribution tab
        Button stateDistributionExportButton = new Button("Export data as CSV");
        stateDistributionExportButton.setOnAction(event -> {
            exportData(
                    "state_distribution.csv",
                    Arrays.asList("timestamp", "susceptible", "exposed", "infectious", "asymptomatic",
                            "symptomatic_mild",
                            "symptomatic_severe", "deceased", "recovered"),
                    simulation.getOutput().getStateDistibutionData(run));
        });
        HBox stateDistributionExport = new HBox(stateDistributionExportButton);
        stateDistributionExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(0)
                .setContent(new VBox(getRunControls(run), stateDistributionGraphs.get(run), stateDistributionExport));

        // Incident case tab
        Button incidentCaseExportButton = new Button("Export data as CSV");
        incidentCaseExportButton.setOnAction(event -> {
            exportData(
                    "incident_cases.csv",
                    Arrays.asList("timestamp", "incident_cases"),
                    simulation.getOutput().getIncidentCaseData(run));
        });
        HBox incidentCaseExport = new HBox(incidentCaseExportButton);
        incidentCaseExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(1)
                .setContent(new VBox(getRunControls(run), incidentCaseGraphs.get(run), incidentCaseExport));

        // Prevalent case tab
        Button prevalentCaseExportButton = new Button("Export data as CSV");
        prevalentCaseExportButton.setOnAction(event -> {
            exportData(
                    "prevalent_cases.csv",
                    Arrays.asList("timestamp", "prevalent_cases"),
                    simulation.getOutput().getPrevalentCaseData(run));
        });
        HBox prevalentCaseExport = new HBox(prevalentCaseExportButton);
        prevalentCaseExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(2)
                .setContent(new VBox(getRunControls(run), prevalentCaseGraphs.get(run), prevalentCaseExport));

        // Cumulative case tab
        Button cumulativeCaseExportButton = new Button("Export data as CSV");
        cumulativeCaseExportButton.setOnAction(event -> {
            exportData(
                    "cumulative_cases.csv",
                    Arrays.asList("timestamp", "cumulative_cases"),
                    simulation.getOutput().getCumulativeCaseData(run));
        });
        HBox cumulativeCaseExport = new HBox(cumulativeCaseExportButton);
        cumulativeCaseExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(3)
                .setContent(new VBox(getRunControls(run), cumulativeCaseGraphs.get(run), cumulativeCaseExport));

        // Hospitalisation tab
        Button hospitalisationExportButton = new Button("Export data as CSV");
        hospitalisationExportButton.setOnAction(event -> {
            exportData(
                    "hospitalisations.csv",
                    Arrays.asList("timestamp", "hospitalisations"),
                    simulation.getOutput().getHospitalisationData(run));
        });
        HBox hospitalisationExport = new HBox(hospitalisationExportButton);
        hospitalisationExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(4)
                .setContent(new VBox(getRunControls(run), hospitalisationGraphs.get(run), hospitalisationExport));

        // Death tab
        Button deathExportButton = new Button("Export data as CSV");
        deathExportButton.setOnAction(event -> {
            exportData(
                    "deaths.csv",
                    Arrays.asList("timestamp", "deaths"),
                    simulation.getOutput().getDeathData(run));
        });
        HBox deathExport = new HBox(deathExportButton);
        deathExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(5).setContent(new VBox(getRunControls(run), deathGraphs.get(run), deathExport));

        // Vaccination tab
        Button vaccinationExportButton = new Button("Export data as CSV");
        vaccinationExportButton.setOnAction(event -> {
            List<String> headerNames = new ArrayList<String>();
            headerNames.add("timestamp");
            for (int i = 0; i < simulation.getOutput().getVaccineNumber(); i++) {
                headerNames.add("vaccine_" + (i + 1));
            }
            exportData(
                    "vaccinations.csv",
                    headerNames,
                    simulation.getOutput().getVaccinationData(run));
        });
        HBox vaccinationExport = new HBox(vaccinationExportButton);
        vaccinationExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(6)
                .setContent(new VBox(getRunControls(run), vaccinationGraphs.get(run), vaccinationExport));

        tabPane.requestLayout();
        stage.getScene().getRoot().requestFocus();
    }

    /**
     * Create UI to navigate between runs
     * 
     * @param run Current run number
     * @return
     */
    private HBox getRunControls(int run) {
        // No controls needed if only one run
        if (simulation.getParameters().getRuns().getValue() == 1) {
            return new HBox();
        }

        // Previous run button
        Button previousRun = new Button("< Previous");
        previousRun.setOnAction(event -> setContent(run - 1));
        if (run == 0) {
            previousRun.setDisable(true);
        }

        // Current run label
        Label runLabel;
        int runs = simulation.getParameters().getRuns().getValue();
        if (run == runs) {
            runLabel = new Label("Average Run");
        } else {
            runLabel = new Label("Run " + Math.min(run + 1, runs) + " / " + runs);
        }

        // Next run button
        Button nextRun = new Button("Next >");
        nextRun.setOnAction(event -> setContent(run + 1));
        if (run == currentRun) {
            nextRun.setDisable(true);
        }

        HBox box = new HBox(20, previousRun, runLabel, nextRun);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    /**
     * Export given graph data as a CSV file
     * 
     * @param filename    Name of the file
     * @param headerNames Header names for the CSV
     * @param data        Data to be exported
     */
    private void exportData(String filename, List<String> headerNames, List<List<Integer>> data) {
        // Select location to save the file
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            File file = new File(selectedDirectory, filename);

            // Create the file if needed and write data
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(String.join(",", headerNames));
                writer.newLine();
                for (List<Integer> row : data) {
                    String rowStr = String.join(",", row.stream().map(String::valueOf).toArray(CharSequence[]::new));
                    writer.write(rowStr);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
