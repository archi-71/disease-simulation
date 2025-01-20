package simulation.params;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EnvironmentParams implements IParam {
    private FileParam buildingsFile = new FileParam("Buildings Shapefile", "maps/small/warwick/buildings.shp");
    private FileParam roadsFile = new FileParam("Roads Shapefile", "maps/small/warwick/roads.shp");
    private IntegerParam hospitalCapacity = new IntegerParam("Total Hospital Capacity", 100);

    public FileParam getBuildingsFile() {
        return buildingsFile;
    }

    public FileParam getRoadsFile() {
        return roadsFile;
    }

    public IntegerParam getHospitalCapacity() {
        return hospitalCapacity;
    }

    public EnvironmentParams(Stage stage) {
        buildingsFile.setStage(stage);
        roadsFile.setStage(stage);
    };

    public EnvironmentParams(EnvironmentParams params) {
        buildingsFile = params.buildingsFile;
        roadsFile = params.roadsFile;
    }

    public Region getInputUI() {
        VBox inputs = new VBox(
                buildingsFile.getInputUI(),
                roadsFile.getInputUI(),
                hospitalCapacity.getInputUI());
        TitledPane titledPane = new TitledPane("Environment", inputs);
        return titledPane;
    }
}
