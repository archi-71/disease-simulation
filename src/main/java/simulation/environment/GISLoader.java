package simulation.environment;

import java.io.File;
import java.io.IOException;

import org.geotools.api.data.FeatureSource;
import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.feature.FeatureCollection;

/**
 * Class to load GIS data from shapefiles
 */
public class GISLoader {

    private FeatureFilters filters;

    // Feature collections for different building types
    private FeatureCollection<SimpleFeatureType, SimpleFeature> residentialFeatures;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> schoolFeatures;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> universityFeatures;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> hospitalFeatures;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> essentialAmenityFeatures;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> essentialWorkplacesFeatures;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> nonEssentialAmenityFeatures;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> nonEssentialWorkplacesFeatures;
    private FeatureCollection<SimpleFeatureType, SimpleFeature> roadFeatures;

    /**
     * Get the feature collection for a given building type
     * @param type The building type
     * @return The feature collection
     */
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

    /**
     * Get the feature collection for roads
     * @return The feature collection
     */
    public FeatureCollection<SimpleFeatureType, SimpleFeature> getRoadFeatures() {
        return roadFeatures;
    }

    /**
     * Construct a GISLoader
     */
    public GISLoader() {
        filters = new FeatureFilters();
    }

    /**
     * Load buildings from a given shapefile
     * @param file The shapefile to load
     * @return True if the file was loaded successfully, false otherwise
     */
    public boolean loadBuildings(File file) {
        try {
            // Extract featurs from the shapefile
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = loadShapefile(file);

            // Filter features from feature source based on building type filters
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
            return false;
        }
    }

    /**
     * Load roads from a given shapefile
     * @param file The shapefile to load
     * @return True if the file was loaded successfully, false otherwise
     */
    public boolean loadRoads(File file) {
        try {
            // Extract features from the shapefile
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = loadShapefile(file);

            // Get features from feature source
            roadFeatures = featureSource.getFeatures();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Load a shapefile and extract its features
     * @param file The shapefile to load
     * @return The feature source, or null if the file could not be found
     * @throws IOException If an error occurs while loading the shapefile
     */
    private FeatureSource<SimpleFeatureType, SimpleFeature> loadShapefile(File file) throws IOException {
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        if (store == null)
            return null;
        return store.getFeatureSource();
    }
}