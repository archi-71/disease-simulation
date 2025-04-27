package simulation.params;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Class to represent all simulation parameters
 */
public class SimulationParams implements IParam {

    // Number of simulation runs
    private IntegerParam runs = new IntegerParam("Simulation Runs",
            "The number of repeated simulations to perform",
            3, 1, Integer.MAX_VALUE);

    // Simulation duration
    private IntegerParam duration = new IntegerParam("Simulation Duration",
            "The duration of a simulation run in days",
            20, 1, Integer.MAX_VALUE);

    // Parameters for environment, population, disease and intervention components
    private EnvironmentParams environmentParams;
    private PopulationParams populationParams;
    private DiseaseParams diseaseParams;
    private InterventionParams interventionParams;

    /**
     * Construct new simulation parameters
     * 
     * @param stage Stage reference for file selection prompt
     */
    public SimulationParams(Stage stage) {
        environmentParams = new EnvironmentParams(stage);
        populationParams = new PopulationParams();
        diseaseParams = new DiseaseParams();
        interventionParams = new InterventionParams();
    };

    /**
     * Clone simulation parameters
     * 
     * @param params Simulation parameters to copy
     */
    public SimulationParams(SimulationParams params) {
        runs = new IntegerParam(params.runs);
        duration = new IntegerParam(params.duration);
        environmentParams = new EnvironmentParams(params.environmentParams);
        populationParams = new PopulationParams(params.populationParams);
        diseaseParams = new DiseaseParams(params.diseaseParams);
        interventionParams = new InterventionParams(params.interventionParams);
    }

    /**
     * Get simulation run count
     * 
     * @return Simulation run count
     */
    public IntegerParam getRuns() {
        return runs;
    }

    /**
     * Get simulation duration
     * 
     * @return Simulation duration
     */
    public IntegerParam getDuration() {
        return duration;
    }

    /**
     * Get environment parameters
     * 
     * @return Environment parameters
     */
    public EnvironmentParams getEnvironmentParams() {
        return environmentParams;
    }

    /**
     * Get population parameters
     * 
     * @return Population parameters
     */
    public PopulationParams getPopulationParams() {
        return populationParams;
    }

    /**
     * Get disease parameters
     * 
     * @return Disease parameters
     */
    public DiseaseParams getDiseaseParams() {
        return diseaseParams;
    }

    /**
     * Get intervention parameters
     * 
     * @return Intervention parameters
     */
    public InterventionParams getInterventionParams() {
        return interventionParams;
    }

    /**
     * Check if parameters have been modified
     * 
     * @return True if parameters have been modified
     */
    public boolean isDirty() {
        return runs.isDirty() || duration.isDirty() || environmentParams.isDirty()
                || populationParams.isDirty() || diseaseParams.isDirty() || interventionParams.isDirty();
    }

    /**
     * Mark parameters as up to date
     */
    public void clean() {
        runs.clean();
        duration.clean();
        environmentParams.clean();
        populationParams.clean();
        diseaseParams.clean();
        interventionParams.clean();
    }

    /**
     * Generate UI to input simulation parameters
     * 
     * @return Pane for simulation parameter inputs
     */
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
