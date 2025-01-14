package simulation.ui;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import simulation.params.PopulationParams;
import simulation.population.AgeGroup;
import simulation.population.Distribution;

public class PopulationParamInputs extends VBox {

    private TextField populationSizeField;

    public PopulationParamInputs(PopulationParams parameters) {

        Label populationSizeLabel = new Label("Population Size: ");
        this.populationSizeField = new TextField();
        this.populationSizeField.setText(String.valueOf(parameters.getPopulationSize()));
        populationSizeField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int populationSize = Integer.parseInt(newValue);
                parameters.setPopulationSize(populationSize);
            } catch (NumberFormatException e) {
                populationSizeField.setText(oldValue);
            }
        });
        HBox populationSizeInput = new HBox(populationSizeLabel, populationSizeField);

        VBox householdSizeDistributionInput = new VBox();
        Distribution<Integer> householdSizeDistribution = parameters.getHouseholdSizeDistribution();
        for (int i = 1; i <= 8; i++) {
            final int householdSize = i;
            Label householdSizeLabel = new Label(householdSize + (householdSize == 8 ? "+" : "") + ": ");
            TextField householdSizeField = new TextField();
            householdSizeField.setText(String.valueOf(householdSizeDistribution.get(householdSize)));
            householdSizeField.textProperty().addListener((observable, oldValue,
                    newValue) -> {
                try {
                    float weight = Float.parseFloat(newValue);
                    parameters.getHouseholdSizeDistribution().set(householdSize, weight);
                } catch (NumberFormatException e) {
                    householdSizeField.setText(oldValue);
                }
            });
            householdSizeDistributionInput.getChildren().add(new HBox(householdSizeLabel, householdSizeField));
        }
        TitledPane householdSizeDistributionPane = new TitledPane("Household Size Distribution",
                householdSizeDistributionInput);
        householdSizeDistributionPane.setExpanded(false);

        VBox ageDistributionInput = new VBox();
        Distribution<AgeGroup> ageDistribution = parameters.getAgeDistribution();
        for (AgeGroup ageGroup : AgeGroup.values()) {
            Label ageLabel = new Label(ageGroup.getMinAge() + "-" + ageGroup.getMaxAge() + ": ");
            TextField ageField = new TextField();
            ageField.setText(String.valueOf(ageDistribution.get(ageGroup)));
            ageField.textProperty().addListener((observable, oldValue,
                    newValue) -> {
                try {
                    float weight = Float.parseFloat(newValue);
                    parameters.getAgeDistribution().set(ageGroup, weight);
                } catch (NumberFormatException e) {
                    ageField.setText(oldValue);
                }
            });
            ageDistributionInput.getChildren().add(new HBox(ageLabel, ageField));
        }
        TitledPane ageDistributionPane = new TitledPane("Age Distribution", ageDistributionInput);
        ageDistributionPane.setExpanded(false);

        Label unemploymentRateLabel = new Label("Unemployment Rate: ");
        TextField unemploymentRateField = new TextField();
        unemploymentRateField.setText(String.valueOf(parameters.getUnemploymentRate()));
        unemploymentRateField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                float unemploymentRate = Float.parseFloat(newValue);
                parameters.setUnemploymentRate(unemploymentRate);
            } catch (NumberFormatException e) {
                unemploymentRateField.setText(oldValue);
            }
        });
        HBox unemploymentRateInput = new HBox(unemploymentRateLabel, unemploymentRateField);

        Label universityEntryRateLabel = new Label("University Entry Rate: ");
        TextField universityEntryRateField = new TextField();
        universityEntryRateField.setText(String.valueOf(parameters.getUniversityEntryRate()));
        universityEntryRateField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                float universityEntryRate = Float.parseFloat(newValue);
                parameters.setUniversityEntryRate(universityEntryRate);
            } catch (NumberFormatException e) {
                universityEntryRateField.setText(oldValue);
            }
        });
        HBox universityEntryRateInput = new HBox(universityEntryRateLabel, universityEntryRateField);

        getChildren().addAll(populationSizeInput, householdSizeDistributionPane, ageDistributionPane,
                unemploymentRateInput, universityEntryRateInput);
    }

    public void updatePopulationSize(int populationSize) {
        Platform.runLater(() -> {
            this.populationSizeField.setText(String.valueOf(populationSize));
        });
    }
}
