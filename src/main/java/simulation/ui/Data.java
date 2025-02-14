package simulation.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

public class Data extends VBox {

    private Stage stage;
    private Simulation simulation;
    private int population;
    private int duration;
    private int currentRun;
    private boolean visualisation;

    private TabPane tabPane;
    private ArrayList<StackedAreaGraph> stateDistributionGraphs;
    private ArrayList<LineGraph> incidentCaseGraphs;
    private ArrayList<LineGraph> prevalentCaseGraphs;
    private ArrayList<LineGraph> cumulativeCaseGraphs;
    private ArrayList<LineGraph> hospitalisationGraphs;
    private ArrayList<LineGraph> deathGraphs;
    private ArrayList<LineGraph> vaccinationGraphs;

    public Data(Stage stage, Simulation simulation) {
        this.stage = stage;
        this.simulation = simulation;
        visualisation = true;

        stateDistributionGraphs = new ArrayList<>();
        incidentCaseGraphs = new ArrayList<>();
        prevalentCaseGraphs = new ArrayList<>();
        cumulativeCaseGraphs = new ArrayList<>();
        hospitalisationGraphs = new ArrayList<>();
        deathGraphs = new ArrayList<>();
        vaccinationGraphs = new ArrayList<>();

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

    public void reset() {
        population = simulation.getParameters().getPopulationParams().getPopulationSize().getValue();
        duration = simulation.getParameters().getDuration().getValue() * 86400;

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

    public void update() {
        if (!visualisation) {
            return;
        }
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
            if (r < run) {
                addNewRun();
            }
        }
        if (currentRun < run) {
            currentRun = run;
            setContent(currentRun);
        }
    }

    public void setVisualisation(boolean visualisation) {
        this.visualisation = visualisation;
    }

    private void addNewRun() {
        StackedAreaGraph stateDistributionGraph = new StackedAreaGraph("State Distribution",
                "Population", population,
                duration);
        for (HealthState state : HealthState.values()) {
            stateDistributionGraph.addSeries(state.getName());
        }
        stateDistributionGraphs.add(stateDistributionGraph);

        LineGraph incidentCaseGraph = new LineGraph("Incident Cases", "New Cases (per hour)",
                population, duration);
        incidentCaseGraph.addSeries("Incident Cases", "red");
        incidentCaseGraphs.add(incidentCaseGraph);

        LineGraph prevalentCaseGraph = new LineGraph("Prevalent Cases", "Current Cases", population,
                duration);
        prevalentCaseGraph.addSeries("Prevalent Cases", "red");
        prevalentCaseGraphs.add(prevalentCaseGraph);

        LineGraph cumulativeCaseGraph = new LineGraph("Cumulative Cases", "Total Cases", population,
                duration);
        cumulativeCaseGraph.addSeries("Cumulative Cases", "red");
        cumulativeCaseGraphs.add(cumulativeCaseGraph);

        LineGraph hospitalisationGraph = new LineGraph("Hospitalisations", "Hospitalised Cases",
                population, duration);
        hospitalisationGraph.addSeries("Hospitalisations", "#B73A3A");
        hospitalisationGraphs.add(hospitalisationGraph);

        LineGraph deathGraph = new LineGraph("Deaths", "Deaths", population, duration);
        deathGraph.addSeries("Deaths", "black");
        deathGraphs.add(deathGraph);

        LineGraph vaccinationGraph = new LineGraph("Vaccinations", "Vaccine Coverage", population,
                duration, true);
        for (int i = 0; i < simulation.getOutput().getVaccineNumber(); i++) {
            vaccinationGraph.addSeries("Vaccine " + (i + 1));
        }
        vaccinationGraphs.add(vaccinationGraph);
    }

    private void setContent(int run) {
        Button stateDistributionExportButton = new Button("Export data as CSV");
        stateDistributionExportButton.setOnAction(event -> {
            exportData(
                    "state_distribution.csv",
                    List.of("timestamp", "susceptible", "exposed", "infectious", "asymptomatic", "symptomatic_mild",
                            "symptomatic_severe", "deceased", "recovered"),
                    simulation.getOutput().getStateDistibutionData(run));
        });
        HBox stateDistributionExport = new HBox(stateDistributionExportButton);
        stateDistributionExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(0)
                .setContent(new VBox(getRunControls(run), stateDistributionGraphs.get(run), stateDistributionExport));

        Button incidentCaseExportButton = new Button("Export data as CSV");
        incidentCaseExportButton.setOnAction(event -> {
            exportData(
                    "incident_cases.csv",
                    List.of("timestamp", "incident_cases"),
                    simulation.getOutput().getIncidentCaseData(run));
        });
        HBox incidentCaseExport = new HBox(incidentCaseExportButton);
        incidentCaseExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(1)
                .setContent(new VBox(getRunControls(run), incidentCaseGraphs.get(run), incidentCaseExport));

        Button prevalentCaseExportButton = new Button("Export data as CSV");
        prevalentCaseExportButton.setOnAction(event -> {
            exportData(
                    "prevalent_cases.csv",
                    List.of("timestamp", "prevalent_cases"),
                    simulation.getOutput().getPrevalentCaseData(run));
        });
        HBox prevalentCaseExport = new HBox(prevalentCaseExportButton);
        prevalentCaseExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(2)
                .setContent(new VBox(getRunControls(run), prevalentCaseGraphs.get(run), prevalentCaseExport));

        Button cumulativeCaseExportButton = new Button("Export data as CSV");
        cumulativeCaseExportButton.setOnAction(event -> {
            exportData(
                    "cumulative_cases.csv",
                    List.of("timestamp", "cumulative_cases"),
                    simulation.getOutput().getCumulativeCaseData(run));
        });
        HBox cumulativeCaseExport = new HBox(cumulativeCaseExportButton);
        cumulativeCaseExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(3)
                .setContent(new VBox(getRunControls(run), cumulativeCaseGraphs.get(run), cumulativeCaseExport));

        Button hospitalisationExportButton = new Button("Export data as CSV");
        hospitalisationExportButton.setOnAction(event -> {
            exportData(
                    "hospitalisations.csv",
                    List.of("timestamp", "hospitalisations"),
                    simulation.getOutput().getHospitalisationData(run));
        });
        HBox hospitalisationExport = new HBox(hospitalisationExportButton);
        hospitalisationExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(4)
                .setContent(new VBox(getRunControls(run), hospitalisationGraphs.get(run), hospitalisationExport));

        Button deathExportButton = new Button("Export data as CSV");
        deathExportButton.setOnAction(event -> {
            exportData(
                    "deaths.csv",
                    List.of("timestamp", "deaths"),
                    simulation.getOutput().getDeathData(run));
        });
        HBox deathExport = new HBox(deathExportButton);
        deathExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(5).setContent(new VBox(getRunControls(run), deathGraphs.get(run), deathExport));

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

    private HBox getRunControls(int run) {
        if (simulation.getParameters().getRuns().getValue() == 1) {
            return new HBox();
        }
        Button previousRun = new Button("< Previous");
        previousRun.setOnAction(event -> setContent(run - 1));
        if (run == 0) {
            previousRun.setDisable(true);
        }

        Label runLabel;
        int runs = simulation.getParameters().getRuns().getValue();
        if (run == runs) {
            runLabel = new Label("Average Run");
        } else {
            runLabel = new Label("Run " + Math.min(run + 1, runs) + " / " + runs);
        }
        Button nextRun = new Button("Next >");
        nextRun.setOnAction(event -> setContent(run + 1));
        if (run == currentRun) {
            nextRun.setDisable(true);
        }

        HBox box = new HBox(previousRun, runLabel, nextRun);
        box.setAlignment(Pos.CENTER);

        return box;
    }

    private void exportData(String filename, List<String> headerNames, List<List<Integer>> data) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            File file = new File(selectedDirectory, filename);

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
