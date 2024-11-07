package simulation;

import java.io.File;
import java.io.IOException;

import org.geotools.api.data.FeatureSource;
import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.feature.FeatureCollection;

public class GISLoader {

    // Building types:

    // residential
    // schools
    // universities
    // hospitals
    // recreation
    // essential retail
    // non-essential retail
    // other essential workplaces
    // other non-essential workplaces

    private FeatureCollection<SimpleFeatureType, SimpleFeature> buildingFeatures;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> roadFeatures;

    public FeatureCollection<SimpleFeatureType, SimpleFeature> getBuildingFeatures() {
        return buildingFeatures;
    }

    public FeatureCollection<SimpleFeatureType, SimpleFeature> getRoadFeatures() {
        return roadFeatures;
    }

    public boolean loadBuildings(File file) {
        try {
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = loadShapefile(file);
            buildingFeatures = featureSource.getFeatures();
            return true;
        } catch (Exception e) {
            System.err.println("Could not load buildings from file '" + file.getAbsolutePath() + "'");
            return false;
        }
    }

    public boolean loadRoads(File file) {
        try {
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = loadShapefile(file);
            roadFeatures = featureSource.getFeatures();
            return true;
        } catch (Exception e) {
            System.err.println("Could not load roads from file '" + file.getAbsolutePath() + "'");
            return false;
        }
    }

    private FeatureSource<SimpleFeatureType, SimpleFeature> loadShapefile(File file) throws IOException {
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        if (store == null)
            return null;
        return store.getFeatureSource();
    }
}