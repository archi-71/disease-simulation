package simulation.params;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Class to represent environment parameters
 */
public class EnvironmentParams implements IParam {

    // Building shapefile
    private FileParam buildingsFile = new FileParam("Buildings Shapefile",
            "A shapefile (.shp) of OSM data defining buildings in the environment",
            "maps/warwick/buildings.shp");

    // Roads shapefile
    private FileParam roadsFile = new FileParam("Roads Shapefile",
            "A shapefile (.shp) of OSM data defining roads in the environment",
            "maps/warwick/roads.shp");

    // Total hospital capacity
    private IntegerParam hospitalCapacity = new IntegerParam("Total Hospital Capacity",
            "The maximum number of individuals who can be hospitalised simultaneously",
            500, 0, Integer.MAX_VALUE);

    /**
     * Construct new environment parameters
     * @param stage Stage reference for file selection prompt
     */
    public EnvironmentParams(Stage stage) {
        buildingsFile.setStage(stage);
        roadsFile.setStage(stage);
    }

    /**
     * Clone environment parameters
     * @param params Environment parameters to copy
     */
    public EnvironmentParams(EnvironmentParams params) {
        buildingsFile = new FileParam(params.buildingsFile);
        roadsFile = new FileParam(params.roadsFile);
        hospitalCapacity = new IntegerParam(params.hospitalCapacity);
    }

    /**
     * Get buildings shapefile
     * @return Buildings shapefile
     */
    public FileParam getBuildingsFile() {
        return buildingsFile;
    }

    /**
     * Get roads shapefile
     * @return Roads shapefile
     */
    public FileParam getRoadsFile() {
        return roadsFile;
    }

    /**
     * Get total hospital capacity
     * @return Total hospital capacity
     */
    public IntegerParam getHospitalCapacity() {
        return hospitalCapacity;
    }

    /**
     * Check if parameters have been modified
     * @return True if parameters have been modified
     */
    public boolean isDirty() {
        return buildingsFile.isDirty() || roadsFile.isDirty() || hospitalCapacity.isDirty();
    }

    /**
     * Mark parameters as up to date
     */
    public void clean() {
        buildingsFile.clean();
        roadsFile.clean();
        hospitalCapacity.clean();
    }

    /**
     * Generate UI to input environment parameters
     * @return Pane for environment parameter inputs
     */
    public Region getInputUI() {
        VBox inputs = new VBox(
                buildingsFile.getInputUI(),
                roadsFile.getInputUI(),
                hospitalCapacity.getInputUI());
        TitledPane titledPane = new TitledPane("Environment", inputs);
        titledPane.getStyleClass().add("big-titled-pane");
        return titledPane;
    }
}
