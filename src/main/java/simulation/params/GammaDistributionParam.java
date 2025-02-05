package simulation.params;

import org.apache.commons.math3.distribution.GammaDistribution;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class GammaDistributionParam implements IParam {

    private String name;
    private float mean;
    private float standardDeviation;
    private GammaDistribution distribution;

    public GammaDistributionParam(String name, float mean, float standardDeviation) {
        this.name = name;
        this.mean = mean;
        this.standardDeviation = standardDeviation;
        initialiseDistribution();
    }

    public float sample() {
        return (float) distribution.sample();
    }

    public Region getInputUI() {
        Label meanLabel = new Label("Mean");
        TextField meanField = new TextField();
        meanField.setText(String.valueOf(mean));
        meanField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                mean = Float.parseFloat(newValue);
                initialiseDistribution();
            } catch (NumberFormatException e) {
                meanField.setText(oldValue);
            }
        });
        HBox meanInput = new HBox(meanLabel, meanField);

        Label standardDeviationLabel = new Label("Standard Deviation");
        TextField standardDeviationField = new TextField();
        standardDeviationField.setText(String.valueOf(standardDeviation));
        standardDeviationField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                standardDeviation = Float.parseFloat(newValue);
                initialiseDistribution();
            } catch (NumberFormatException e) {
                standardDeviationField.setText(oldValue);
            }
        });
        HBox standardDeviationInput = new HBox(standardDeviationLabel, standardDeviationField);

        TitledPane titledPane = new TitledPane(name, new VBox(meanInput, standardDeviationInput));
        return titledPane;
    }

    private void initialiseDistribution() {
        float shape = mean * mean / standardDeviation;
        float scale = standardDeviation / mean;
        distribution = new GammaDistribution(shape, scale);
    }
}
