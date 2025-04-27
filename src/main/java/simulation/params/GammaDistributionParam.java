package simulation.params;

import org.apache.commons.math3.distribution.GammaDistribution;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Class to represent a gamma distribution parameter
 */
public class GammaDistributionParam implements IParam {

    private String name;
    private String description;
    private float mean;
    private float standardDeviation;
    private GammaDistribution distribution;
    private boolean dirty;

    /**
     * Construct a new gamma distribution parameter
     * 
     * @param name              Parameter name
     * @param description       Parameter description
     * @param mean              Mean of the distribution
     * @param standardDeviation Standard deviation of the distribution
     */
    public GammaDistributionParam(String name, String description, float mean, float standardDeviation) {
        this.name = name;
        this.description = description;
        this.mean = mean;
        this.standardDeviation = standardDeviation;
        initialiseDistribution();
        dirty = true;
    }

    /**
     * Clone a gamma distribution parameter
     * 
     * @param param Gamma distribution parameter to copy
     */
    public GammaDistributionParam(GammaDistributionParam param) {
        name = param.name;
        mean = param.mean;
        standardDeviation = param.standardDeviation;
        initialiseDistribution();
        dirty = param.dirty;
    }

    /**
     * Randomly sample from the distribution
     * 
     * @return Randomly sampled value
     */
    public float sample() {
        return (float) distribution.sample();
    }

    /**
     * Check if parameter has been modified
     * 
     * @return True if parameter has been modified
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Mark parameter as up to date
     */
    public void clean() {
        dirty = false;
    }

    /**
     * Generate UI to input gamma distribution parameter
     * 
     * @return Pane for gamma distribution parameter input
     */
    public Region getInputUI() {
        Label meanLabel = new Label("Mean");
        TextField meanField = new TextField();
        meanField.setText(String.valueOf(mean));

        // Only update mean once the field is unfocused
        meanField.focusedProperty().addListener((observable, wasFocussed, isFocussed) -> {
            if (!isFocussed) {
                float newValue;
                try {
                    newValue = Math.min(Math.max(Float.parseFloat(meanField.getText()), Float.MIN_VALUE),
                            Float.MAX_VALUE);
                } catch (NumberFormatException e) {
                    newValue = mean;
                }
                if (mean != newValue) {
                    mean = newValue;
                    initialiseDistribution();
                    dirty = true;
                }
                meanField.setText(String.valueOf(mean));
            }
        });
        meanField.setAlignment(Pos.CENTER_RIGHT);
        Region meanSpace = new Region();
        HBox.setHgrow(meanSpace, Priority.ALWAYS);
        HBox meanBox = new HBox(meanLabel, meanSpace, meanField);
        meanBox.setAlignment(Pos.CENTER);
        Tooltip meanTip = new Tooltip(
                "Mean of " + Character.toLowerCase(description.charAt(0)) + description.substring(1));
        Tooltip.install(meanBox, meanTip);

        Label standardDeviationLabel = new Label("Standard Deviation");
        TextField standardDeviationField = new TextField();
        standardDeviationField.setText(String.valueOf(standardDeviation));

        // Only update standard deviation once the field is unfocused
        standardDeviationField.focusedProperty().addListener((observable, wasFocussed, isFocussed) -> {
            if (!isFocussed) {
                float newValue;
                try {
                    newValue = Math.min(Math.max(Float.parseFloat(standardDeviationField.getText()), Float.MIN_VALUE),
                            Float.MAX_VALUE);
                } catch (NumberFormatException e) {
                    newValue = standardDeviation;
                }
                if (standardDeviation != newValue) {
                    standardDeviation = newValue;
                    initialiseDistribution();
                    dirty = true;
                }
                standardDeviationField.setText(String.valueOf(standardDeviation));
            }
        });
        standardDeviationField.setAlignment(Pos.CENTER_RIGHT);
        Region standardDeviationSpace = new Region();
        HBox.setHgrow(standardDeviationSpace, Priority.ALWAYS);
        HBox standardDeviationBox = new HBox(standardDeviationLabel, standardDeviationSpace, standardDeviationField);
        standardDeviationBox.setAlignment(Pos.CENTER);
        Tooltip standardDeviationTip = new Tooltip(
                "Standard deviation of " + Character.toLowerCase(description.charAt(0)) + description.substring(1));
        Tooltip.install(standardDeviationBox, standardDeviationTip);

        TitledPane titledPane = new TitledPane(name, new VBox(meanBox, standardDeviationBox));
        titledPane.setExpanded(false);
        return titledPane;
    }

    /**
     * Initialise the gamma distribution
     */
    private void initialiseDistribution() {
        // Convert mean and standard deviation to shape and scale parameters
        float shape = mean * mean / standardDeviation;
        float scale = standardDeviation / mean;
        distribution = new GammaDistribution(shape, scale);
    }
}
