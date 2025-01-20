package simulation.params;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import simulation.population.AgeGroup;

public class PopulationParams implements IParam {
    private IntegerParam populationSize = new IntegerParam("Population Size", 1000);
    private DiscreteDistributionParam<Integer> householdSizeDistribution = new DiscreteDistributionParam<Integer>(
            "Household Size Distribution") {
        {
            setValue(1, new FloatParam("1 person", 0.2f));
            setValue(2, new FloatParam("2 people", 0.3f));
            setValue(3, new FloatParam("3 people", 0.25f));
            setValue(4, new FloatParam("4 people", 0.15f));
            setValue(5, new FloatParam("5 people", 0.055f));
            setValue(6, new FloatParam("6 people", 0.025f));
            setValue(7, new FloatParam("7 people", 0.015f));
            setValue(8, new FloatParam("8+ people", 0.005f));
        }
    };
    private DiscreteDistributionParam<AgeGroup> ageDistribution = new DiscreteDistributionParam<AgeGroup>(
            "Age Distribution") {
        {
            setValue(AgeGroup._0_TO_9, new FloatParam("0 to 4 years", 11.7f));
            setValue(AgeGroup._10_TO_19, new FloatParam("15 to 19 years", 11.0f));
            setValue(AgeGroup._20_TO_29, new FloatParam("25 to 29 years", 12.8f));
            setValue(AgeGroup._30_TO_39, new FloatParam("35 to 39 years", 12.6f));
            setValue(AgeGroup._40_TO_49, new FloatParam("40 to 44 years", 11.4f));
            setValue(AgeGroup._50_TO_59, new FloatParam("50 to 54 years", 11.1f));
            setValue(AgeGroup._60_TO_69, new FloatParam("60 to 69 years", 9.5f));
            setValue(AgeGroup._70_TO_79, new FloatParam("70 to 79 years", 6.8f));
            setValue(AgeGroup._80_TO_89, new FloatParam("80 to 89 years", 4.1f));
            setValue(AgeGroup._90_PLUS, new FloatParam("90+ years", 1.6f));
        }
    };
    private FloatParam schoolEntryRate = new FloatParam("School Entry Rate", 0.99f);
    private FloatParam universityEntryRate = new FloatParam("University Entry Rate", 0.35f);
    private FloatParam unemploymentRate = new FloatParam("Unemployment Rate", 0.05f);

    public IntegerParam getPopulationSize() {
        return populationSize;
    }

    public DiscreteDistributionParam<Integer> getHouseholdSizeDistribution() {
        return householdSizeDistribution;
    }

    public DiscreteDistributionParam<AgeGroup> getAgeDistribution() {
        return ageDistribution;
    }

    public FloatParam getSchoolEntryRate() {
        return schoolEntryRate;
    }

    public FloatParam getUniversityEntryRate() {
        return universityEntryRate;
    }

    public FloatParam getUnemploymentRate() {
        return unemploymentRate;
    }

    public PopulationParams() {
    };

    public PopulationParams(PopulationParams params) {
        populationSize = params.populationSize;
        householdSizeDistribution = new DiscreteDistributionParam<Integer>(params.householdSizeDistribution);
        ageDistribution = new DiscreteDistributionParam<AgeGroup>(params.ageDistribution);
        schoolEntryRate = params.schoolEntryRate;
        universityEntryRate = params.universityEntryRate;
        unemploymentRate = params.unemploymentRate;
    }

    public Region getInputUI() {
        VBox inputs = new VBox(
                populationSize.getInputUI(),
                householdSizeDistribution.getInputUI(),
                ageDistribution.getInputUI(),
                schoolEntryRate.getInputUI(),
                universityEntryRate.getInputUI(),
                unemploymentRate.getInputUI());
        TitledPane titledPane = new TitledPane("Population", inputs);
        return titledPane;
    }
}
