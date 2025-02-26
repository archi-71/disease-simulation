package simulation.params;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EnvironmentParams implements IParam {

    private FileParam buildingsFile = new FileParam("Buildings Shapefile",
            "A shapefile (.shp) of OSM data defining buildings in the environment",
            "maps/small/warwick/buildings.shp");
    private FileParam roadsFile = new FileParam("Roads Shapefile",
            "A shapefile (.shp) of OSM data defining roads in the environment",
            "maps/small/warwick/roads.shp");
    private IntegerParam hospitalCapacity = new IntegerParam("Total Hospital Capacity",
            "The maximum number of individuals who can be hospitalised simultaneously",
            500, 0, Integer.MAX_VALUE);

    public EnvironmentParams(Stage stage) {
        buildingsFile.setStage(stage);
        roadsFile.setStage(stage);
    }

    public EnvironmentParams(EnvironmentParams params) {
        buildingsFile = new FileParam(params.buildingsFile);
        roadsFile = new FileParam(params.roadsFile);
        hospitalCapacity = new IntegerParam(params.hospitalCapacity);
    }

    public FileParam getBuildingsFile() {
        return buildingsFile;
    }

    public FileParam getRoadsFile() {
        return roadsFile;
    }

    public IntegerParam getHospitalCapacity() {
        return hospitalCapacity;
    }

    public boolean isDirty() {
        return buildingsFile.isDirty() || roadsFile.isDirty() || hospitalCapacity.isDirty();
    }

    public void clean() {
        buildingsFile.clean();
        roadsFile.clean();
        hospitalCapacity.clean();
    }

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
