package simulation.params;

import java.io.File;

public class EnvironmentParams {
    private File buildingsFile = new File("../../../../maps/map/buildings-polygon.shp");
    private File roadsFile = new File("../../../../maps/map/roads-line.shp");

    public void setBuildingsFile(File buildingsFile) {
        this.buildingsFile = buildingsFile;
    }

    public void setRoadsFile(File roadsFile) {
        this.roadsFile = roadsFile;
    }

    public File getBuildingsFile() {
        return buildingsFile;
    }

    public File getRoadsFile() {
        return roadsFile;
    }

    public EnvironmentParams() {
    };

    public EnvironmentParams(EnvironmentParams params) {
        buildingsFile = params.buildingsFile;
        roadsFile = params.roadsFile;
    }
}
