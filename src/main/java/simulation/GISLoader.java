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

    private FeatureFilters filters;

    private FeatureCollection<SimpleFeatureType, SimpleFeature> residentialFeatures;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> schoolFeatures;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> universityFeatures;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> hospitalFeatures;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> essentialAmenityFeatures;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> essentialWorkplacesFeatures;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> nonEssentialAmenityFeatures;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> nonEssentialWorkplacesFeatures;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> roadFeatures;

    public FeatureCollection<SimpleFeatureType, SimpleFeature> getBuildingFeatures(BuildingType type) {
        switch (type) {
            case RESIDENTIAL:
                return residentialFeatures;
            case SCHOOL:
                return schoolFeatures;
            case UNIVERSITY:
                return universityFeatures;
            case HOSPITAL:
                return hospitalFeatures;
            case ESSENTIAL_AMENITY:
                return essentialAmenityFeatures;
            case ESSENTIAL_WORKPLACE:
                return essentialWorkplacesFeatures;
            case NON_ESSENTIAL_AMENITY:
                return nonEssentialAmenityFeatures;
            case NON_ESSENTIAL_WORKPLACE:
                return nonEssentialWorkplacesFeatures;
            default:
                return null;
        }
    }

    public FeatureCollection<SimpleFeatureType, SimpleFeature> getRoadFeatures() {
        return roadFeatures;
    }

    public GISLoader() {
        filters = new FeatureFilters();
    }

    public boolean loadBuildings(File file) {
        try {
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = loadShapefile(file);
            residentialFeatures = featureSource.getFeatures(filters.getResidentialFilter());
            schoolFeatures = featureSource.getFeatures(filters.getSchoolFilter());
            universityFeatures = featureSource.getFeatures(filters.getUniversityFilter());
            hospitalFeatures = featureSource.getFeatures(filters.getHospitalFilter());
            essentialAmenityFeatures = featureSource.getFeatures(filters.getEssentialAmenitiesFilter());
            essentialWorkplacesFeatures = featureSource.getFeatures(filters.getEssentialWorkplacesFilter());
            nonEssentialAmenityFeatures = featureSource.getFeatures(filters.getNonEssentialAmenitiesFilter());
            nonEssentialWorkplacesFeatures = featureSource.getFeatures(filters.getNonEssentialWorkplacesFilter());
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