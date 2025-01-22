package simulation.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
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

    private TabPane tabPane;
    private StackedAreaGraph stateDistributionGraph;
    private LineGraph incidentCaseGraph;
    private LineGraph prevalentCaseGraph;
    private LineGraph cumulativeCaseGraph;
    private LineGraph hospitalisationGraph;
    private LineGraph deathGraph;

    public Data(Stage stage, Simulation simulation) {
        this.stage = stage;
        this.simulation = simulation;

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        Tab stateDistributionTab = new Tab("State Distribution");
        Tab incidentCaseTab = new Tab("Incident Cases");
        Tab prevalentCaseTab = new Tab("Prevalent Cases");
        Tab cumulativeCaseTab = new Tab("Cumulative Cases");
        Tab hospitalisationTab = new Tab("Hospitalisations");
        Tab deathTab = new Tab("Deaths");

        tabPane.getTabs().addAll(stateDistributionTab, incidentCaseTab, prevalentCaseTab, cumulativeCaseTab,
                hospitalisationTab, deathTab);

        getChildren().add(tabPane);
    }

    public void reset() {
        population = simulation.getParameters().getPopulationParams().getPopulationSize().getValue();
        duration = simulation.getParameters().getSimulationDuration().getValue() * 86400;

        stateDistributionGraph = new StackedAreaGraph("State Distribution", "Population", population, duration);
        for (HealthState state : HealthState.values()) {
            stateDistributionGraph.addSeries(state.getName(), state.getColour());
        }
        Button stateDistributionExportButton = new Button("Export data as CSV");
        stateDistributionExportButton.setOnAction(event -> {
            exportData(
                    "state_distribution.csv",
                    List.of("timestamp", "susceptible", "exposed", "infectious", "asymptomatic", "symptomatic_mild",
                            "symptomatic_severe", "deceased", "recovered"),
                    simulation.getOutput().getStateDistibutionData());
        });
        HBox stateDistributionExport = new HBox(stateDistributionExportButton);
        stateDistributionExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(0).setContent(new VBox(stateDistributionGraph, stateDistributionExport));

        incidentCaseGraph = new LineGraph("Incident Cases", "New Cases (per hour)", population, duration);
        Button incidentCaseExportButton = new Button("Export data as CSV");
        incidentCaseExportButton.setOnAction(event -> {
            exportData(
                    "incident_cases.csv",
                    List.of("timestamp", "incident_cases"),
                    simulation.getOutput().getIncidentCaseData());
        });
        HBox incidentCaseExport = new HBox(incidentCaseExportButton);
        incidentCaseExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(1).setContent(new VBox(incidentCaseGraph, incidentCaseExport));

        prevalentCaseGraph = new LineGraph("Prevalent Cases", "Current Cases", population, duration);
        Button prevalentCaseExportButton = new Button("Export data as CSV");
        prevalentCaseExportButton.setOnAction(event -> {
            exportData(
                    "prevalent_cases.csv",
                    List.of("timestamp", "prevalent_cases"),
                    simulation.getOutput().getPrevalentCaseData());
        });
        HBox prevalentCaseExport = new HBox(prevalentCaseExportButton);
        prevalentCaseExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(2).setContent(new VBox(prevalentCaseGraph, prevalentCaseExport));

        cumulativeCaseGraph = new LineGraph("Cumulative Cases", "Total Cases", population, duration);
        Button cumulativeCaseExportButton = new Button("Export data as CSV");
        cumulativeCaseExportButton.setOnAction(event -> {
            exportData(
                    "cumulative_cases.csv",
                    List.of("timestamp", "cumulative_cases"),
                    simulation.getOutput().getCumulativeCaseData());
        });
        HBox cumulativeCaseExport = new HBox(cumulativeCaseExportButton);
        cumulativeCaseExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(3).setContent(new VBox(cumulativeCaseGraph, cumulativeCaseExport));

        hospitalisationGraph = new LineGraph("Hospitalisations", "Hospitalised Cases", population, duration);
        Button hospitalisationExportButton = new Button("Export data as CSV");
        hospitalisationExportButton.setOnAction(event -> {
            exportData(
                    "hospitalisations.csv",
                    List.of("timestamp", "hospitalisations"),
                    simulation.getOutput().getHospitalisationData());
        });
        HBox hospitalisationExport = new HBox(hospitalisationExportButton);
        hospitalisationExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(4).setContent(new VBox(hospitalisationGraph, hospitalisationExport));

        deathGraph = new LineGraph("Deaths", "Deaths", population, duration);
        Button deathExportButton = new Button("Export data as CSV");
        deathExportButton.setOnAction(event -> {
            exportData(
                    "deaths.csv",
                    List.of("timestamp", "deaths"),
                    simulation.getOutput().getDeathData());
        });
        HBox deathExport = new HBox(deathExportButton);
        deathExport.setAlignment(Pos.CENTER_RIGHT);
        tabPane.getTabs().get(5).setContent(new VBox(deathGraph, deathExport));

        tabPane.requestLayout();
    }

    public void update() {
        SimulationOutput output = simulation.getOutput();

        stateDistributionGraph.update(output.getStateDistibutionData());
        incidentCaseGraph.update(output.getIncidentCaseData());
        prevalentCaseGraph.update(output.getPrevalentCaseData());
        cumulativeCaseGraph.update(output.getCumulativeCaseData());
        hospitalisationGraph.update(output.getHospitalisationData());
        deathGraph.update(output.getDeathData());
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
