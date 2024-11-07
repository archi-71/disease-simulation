package simulation;

public class Environment {

    private EnvironmentParams parameters;
    private GISLoader gisLoader;

    public GISLoader getGISLoader() {
        return gisLoader;
    }

    public Environment(EnvironmentParams params) {
        parameters = params;
        gisLoader = new GISLoader();

        if (!gisLoader.loadBuildings(parameters.getBuildingsFile())) {
            return;
        }
        if (!gisLoader.loadRoads(parameters.getRoadsFile())) {
            return;
        }
    }
}
