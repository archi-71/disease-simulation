package simulation.params;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import simulation.population.AgeGroup;

public class PopulationParams implements IParam {

        private IntegerParam populationSize = new IntegerParam("Population Size",
                        "The number of individuals in the population",
                        1000, 1, Integer.MAX_VALUE);

        private DiscreteDistributionParam<Integer> householdSizeDistribution = new DiscreteDistributionParam<Integer>(
                        "Household Size Distribution") {
                {
                        setValue(1, new FloatParam("1 person",
                                        "The proportion of households of 1 person", 0.2f, 0f, Float.MAX_VALUE));
                        setValue(2, new FloatParam("2 people",
                                        "The proportion of households of 2 people", 0.3f, 0f, Float.MAX_VALUE));
                        setValue(3, new FloatParam("3 people",
                                        "The proportion of households of 3 people", 0.25f, 0f, Float.MAX_VALUE));
                        setValue(4, new FloatParam("4 people",
                                        "The proportion of households of 4 people", 0.15f, 0f, Float.MAX_VALUE));
                        setValue(5, new FloatParam("5 people",
                                        "The proportion of households of 5 people", 0.055f, 0f, Float.MAX_VALUE));
                        setValue(6, new FloatParam("6 people",
                                        "The proportion of households of 6 people", 0.025f, 0f, Float.MAX_VALUE));
                        setValue(7, new FloatParam("7 people",
                                        "The proportion of households of 7 people", 0.015f, 0f, Float.MAX_VALUE));
                        setValue(8, new FloatParam("8+ people",
                                        "The proportion of households of 8 or more people", 0.005f, 0f,
                                        Float.MAX_VALUE));
                }
        };

        private DiscreteDistributionParam<AgeGroup> ageDistribution = new DiscreteDistributionParam<AgeGroup>(
                        "Age Distribution") {
                {
                        setValue(AgeGroup._0_TO_9, new FloatParam("0 to 9 years",
                                        "The proportion of individuals aged 0 to 9 years", 0.117f, 0f,
                                        Float.MAX_VALUE));
                        setValue(AgeGroup._10_TO_19, new FloatParam("10 to 19 years",
                                        "The proportion of individuals aged 10 to 19 years", 0.11f, 0f,
                                        Float.MAX_VALUE));
                        setValue(AgeGroup._20_TO_29, new FloatParam("20 to 29 years",
                                        "The proportion of individuals aged 20 to 29 years", 0.128f, 0f,
                                        Float.MAX_VALUE));
                        setValue(AgeGroup._30_TO_39, new FloatParam("30 to 39 years",
                                        "The proportion of individuals aged 30 to 39 years", 0.126f, 0f,
                                        Float.MAX_VALUE));
                        setValue(AgeGroup._40_TO_49, new FloatParam("40 to 49 years",
                                        "The proportion of individuals aged 40 to 49 years", 0.114f, 0f,
                                        Float.MAX_VALUE));
                        setValue(AgeGroup._50_TO_59, new FloatParam("50 to 59 years",
                                        "The proportion of individuals aged 50 to 59 years", 0.111f, 0f,
                                        Float.MAX_VALUE));
                        setValue(AgeGroup._60_TO_69, new FloatParam("60 to 69 years",
                                        "The proportion of individuals aged 60 to 69 years", 0.095f, 0f,
                                        Float.MAX_VALUE));
                        setValue(AgeGroup._70_TO_79, new FloatParam("70 to 79 years",
                                        "The proportion of individuals aged 70 to 79 years", 0.068f, 0f,
                                        Float.MAX_VALUE));
                        setValue(AgeGroup._80_TO_89, new FloatParam("80 to 89 years",
                                        "The proportion of individuals aged 80 to 89 years", 0.041f, 0f,
                                        Float.MAX_VALUE));
                        setValue(AgeGroup._90_PLUS, new FloatParam("90+ years",
                                        "The proportion of individuals aged 90 years or older", 0.016f, 0f,
                                        Float.MAX_VALUE));
                }
        };

        private FloatParam schoolEntryRate = new FloatParam("School Entry Rate",
                        "The proportion of school age children (5-17 year olds) who attend school",
                        0.99f, 0f, 1f);

        private FloatParam universityEntryRate = new FloatParam("University Entry Rate",
                        "The proportion of young adults (18-24 year olds) who attend university",
                        0.35f, 0f, 1f);

        private FloatParam unemploymentRate = new FloatParam("Unemployment Rate",
                        "The proportion of working age adults (18-65 year olds) who are not employed or studying",
                        0.05f, 0f, 1f);

        public PopulationParams() {
        }

        public PopulationParams(PopulationParams params) {
                populationSize = new IntegerParam(params.populationSize);
                householdSizeDistribution = new DiscreteDistributionParam<Integer>(params.householdSizeDistribution);
                ageDistribution = new DiscreteDistributionParam<AgeGroup>(params.ageDistribution);
                schoolEntryRate = new FloatParam(params.schoolEntryRate);
                universityEntryRate = new FloatParam(params.universityEntryRate);
                unemploymentRate = new FloatParam(params.unemploymentRate);
        }

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

        public boolean isDirty() {
                return populationSize.isDirty() || householdSizeDistribution.isDirty() || ageDistribution.isDirty()
                                || schoolEntryRate.isDirty() || universityEntryRate.isDirty()
                                || unemploymentRate.isDirty();
        }

        public void clean() {
                populationSize.clean();
                householdSizeDistribution.clean();
                ageDistribution.clean();
                schoolEntryRate.clean();
                universityEntryRate.clean();
                unemploymentRate.clean();
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
                titledPane.getStyleClass().add("big-titled-pane");
                return titledPane;
        }
}
