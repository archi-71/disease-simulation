package simulation.params;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SimulationParams implements IParam {

    private IntegerParam runs = new IntegerParam("Simulation Runs",
            "The number of runs of the simulation to run",
            2, 1, Integer.MAX_VALUE);

    private IntegerParam duration = new IntegerParam("Simulation Duration",
            "The duration of the simulation in days",
            20, 1, Integer.MAX_VALUE);

    private EnvironmentParams environmentParams;
    private PopulationParams populationParams;
    private DiseaseParams diseaseParams;
    private InterventionParams interventionParams;

    public SimulationParams(Stage stage) {
        environmentParams = new EnvironmentParams(stage);
        populationParams = new PopulationParams();
        diseaseParams = new DiseaseParams();
        interventionParams = new InterventionParams();
    };

    public SimulationParams(SimulationParams params) {
        runs = new IntegerParam(params.runs);
        duration = new IntegerParam(params.duration);
        environmentParams = new EnvironmentParams(params.environmentParams);
        populationParams = new PopulationParams(params.populationParams);
        diseaseParams = new DiseaseParams(params.diseaseParams);
        interventionParams = new InterventionParams(params.interventionParams);
    }

    public IntegerParam getRuns() {
        return runs;
    }

    public IntegerParam getDuration() {
        return duration;
    }

    public EnvironmentParams getEnvironmentParams() {
        return environmentParams;
    }

    public PopulationParams getPopulationParams() {
        return populationParams;
    }

    public DiseaseParams getDiseaseParams() {
        return diseaseParams;
    }

    public InterventionParams getInterventionParams() {
        return interventionParams;
    }

    public boolean isDirty() {
        return runs.isDirty() || duration.isDirty() || environmentParams.isDirty()
                || populationParams.isDirty() || diseaseParams.isDirty() || interventionParams.isDirty();
    }

    public void clean() {
        runs.clean();
        duration.clean();
        environmentParams.clean();
        populationParams.clean();
        diseaseParams.clean();
        interventionParams.clean();
    }

    public Region getInputUI() {
        VBox container = new VBox(
                runs.getInputUI(),
                duration.getInputUI(),
                environmentParams.getInputUI(),
                populationParams.getInputUI(),
                diseaseParams.getInputUI(),
                interventionParams.getInputUI());
        TitledPane titledPane = new TitledPane("Parameters", container);
        titledPane.getStyleClass().add("big-titled-pane");
        titledPane.setCollapsible(false);
        return titledPane;
    }
}
