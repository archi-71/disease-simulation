package simulation.params;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import simulation.population.AgeGroup;

public class DiseaseParams implements IParam {

        private IntegerParam initialInfected = new IntegerParam("Initital Number Infected",
                        "The number of individuals who have been exposed to the disease at the start of the simulation",
                        1, 1, Integer.MAX_VALUE);

        private FloatParam transmissionRate = new FloatParam("Transmission Rate",
                        "The average number of successful transmissions from an infectious to susceptible individual each day",
                        5f, 0f, Float.MAX_VALUE);

        private MapParam<AgeGroup, FloatParam> symptomaticProbability = new MapParam<AgeGroup, FloatParam>(
                        "Symptomatic Probability By Age") {
                {
                        setValue(AgeGroup._0_TO_9, new FloatParam("0 to 9 years",
                                        "The probability of an individual aged 0 to 4 years old to develop symptoms after being infected",
                                        0.5f, 0f, 1f));
                        setValue(AgeGroup._10_TO_19, new FloatParam("10 to 19 years",
                                        "The probability of an individual aged 10 to 19 years old to develop symptoms after being infected",
                                        0.55f, 0f, 1f));
                        setValue(AgeGroup._20_TO_29, new FloatParam("20 to 29 years",
                                        "The probability of an individual aged 20 to 29 years old to develop symptoms after being infected",
                                        0.6f, 0f, 1f));
                        setValue(AgeGroup._30_TO_39, new FloatParam("30 to 39 years",
                                        "The probability of an individual aged 30 to 39 years old to develop symptoms after being infected",
                                        0.65f, 0f, 1f));
                        setValue(AgeGroup._40_TO_49, new FloatParam("40 to 49 years",
                                        "The probability of an individual aged 40 to 49 years old to develop symptoms after being infected",
                                        0.7f, 0f, 1f));
                        setValue(AgeGroup._50_TO_59, new FloatParam("50 to 59 years",
                                        "The probability of an individual aged 50 to 59 years old to develop symptoms after being infected",
                                        0.75f, 0f, 1f));
                        setValue(AgeGroup._60_TO_69, new FloatParam("60 to 69 years",
                                        "The probability of an individual aged 60 to 69 years old to develop symptoms after being infected",
                                        0.8f, 0f, 1f));
                        setValue(AgeGroup._70_TO_79, new FloatParam("70 to 79 years",
                                        "The probability of an individual aged 70 to 79 years old to develop symptoms after being infected",
                                        0.85f, 0f, 1f));
                        setValue(AgeGroup._80_TO_89, new FloatParam("80 to 89 years",
                                        "The probability of an individual aged 80 to 89 years old to develop symptoms after being infected",
                                        0.9f, 0f, 1f));
                        setValue(AgeGroup._90_PLUS, new FloatParam("90+ years",
                                        "The probability of an individual aged 90 years or older old to develop symptoms after being infected",
                                        0.95f, 0f, 1f));
                }
        };

        private MapParam<AgeGroup, FloatParam> severeSymptomaticProbability = new MapParam<AgeGroup, FloatParam>(
                        "Severe Symptomatic Probability By Age") {
                {
                        setValue(AgeGroup._0_TO_9, new FloatParam("0 to 9 years",
                                        "The probability of an individual aged 0 to 9 years old to develop severe symptoms after being infected",
                                        0.0005f, 0f, 1f));
                        setValue(AgeGroup._10_TO_19, new FloatParam("10 to 19 years",
                                        "The probability of an individual aged 10 to 19 years old to develop severe symptoms after being infected",
                                        0.001f, 0f, 1f));
                        setValue(AgeGroup._20_TO_29, new FloatParam("20 to 29 years",
                                        "The probability of an individual aged 20 to 29 years old to develop severe symptoms after being infected",
                                        0.005f, 0f, 1f));
                        setValue(AgeGroup._30_TO_39, new FloatParam("30 to 39 years",
                                        "The probability of an individual aged 30 to 39 years old to develop severe symptoms after being infected",
                                        0.01f, 0f, 1f));
                        setValue(AgeGroup._40_TO_49, new FloatParam("40 to 49 years",
                                        "The probability of an individual aged 40 to 49 years old to develop severe symptoms after being infected",
                                        0.05f, 0f, 1f));
                        setValue(AgeGroup._50_TO_59, new FloatParam("50 to 59 years",
                                        "The probability of an individual aged 50 to 59 years old to develop severe symptoms after being infected",
                                        0.1f, 0f, 1f));
                        setValue(AgeGroup._60_TO_69, new FloatParam("60 to 69 years",
                                        "The probability of an individual aged 60 to 69 years old to develop severe symptoms after being infected",
                                        0.2f, 0f, 1f));
                        setValue(AgeGroup._70_TO_79, new FloatParam("70 to 79 years",
                                        "The probability of an individual aged 70 to 79 years old to develop severe symptoms after being infected",
                                        0.3f, 0f, 1f));
                        setValue(AgeGroup._80_TO_89, new FloatParam("80 to 89 years",
                                        "The probability of an individual aged 80 to 89 years old to develop severe symptoms after being infected",
                                        0.4f, 0f, 1f));
                        setValue(AgeGroup._90_PLUS, new FloatParam("90+ years",
                                        "The probability of an individual aged 90 years or older to develop severe symptoms after being infected",
                                        0.4f, 0f, 1f));
                }
        };
        private MapParam<AgeGroup, FloatParam> mortalityProbability = new MapParam<AgeGroup, FloatParam>(
                        "Mortality Probability By Age") {
                {
                        setValue(AgeGroup._0_TO_9, new FloatParam("0 to 9 years",
                                        "The probability of an individual aged 0 to 9 years old to die to the disease after being infected",
                                        0.0001f, 0f, 1f));
                        setValue(AgeGroup._10_TO_19, new FloatParam("10 to 19 years",
                                        "The probability of an individual aged 10 to 19 years old to die to the disease after being infected",
                                        0.0002f, 0f, 1f));
                        setValue(AgeGroup._20_TO_29, new FloatParam("20 to 29 years",
                                        "The probability of an individual aged 20 to 29 years old to die to the disease after being infected",
                                        0.001f, 0f, 1f));
                        setValue(AgeGroup._30_TO_39, new FloatParam("30 to 39 years",
                                        "The probability of an individual aged 30 to 39 years old to die to the disease after being infected",
                                        0.005f, 0f, 1f));
                        setValue(AgeGroup._40_TO_49, new FloatParam("40 to 49 years",
                                        "The probability of an individual aged 40 to 49 years old to die to the disease after being infected",
                                        0.01f, 0f, 1f));
                        setValue(AgeGroup._50_TO_59, new FloatParam("50 to 59 years",
                                        "The probability of an individual aged 50 to 59 years old to die to the disease after being infected",
                                        0.02f, 0f, 1f));
                        setValue(AgeGroup._60_TO_69, new FloatParam("60 to 69 years",
                                        "The probability of an individual aged 60 to 69 years old to die to the disease after being infected",
                                        0.05f, 0f, 1f));
                        setValue(AgeGroup._70_TO_79, new FloatParam("70 to 79 years",
                                        "The probability of an individual aged 70 to 79 years old to die to the disease after being infected",
                                        0.15f, 0f, 1f));
                        setValue(AgeGroup._80_TO_89, new FloatParam("80 to 89 years",
                                        "The probability of an individual aged 80 to 89 years old to die to the disease after being infected",
                                        0.25f, 0f, 1f));
                        setValue(AgeGroup._90_PLUS, new FloatParam("90+ years",
                                        "The probability of an individual aged 90 years or older to die to the disease after being infected",
                                        0.3f, 0f, 1f));
                }
        };

        private FloatParam relativeMortalityWithoutHospitalisation = new FloatParam(
                        "Relative Mortality Without Hospitalisation",
                        "The multiplier applied to an individual's mortality rate if they fail to receive hospital treatment",
                        3f, 1, Float.MAX_VALUE);

        private GammaDistributionParam exposedToInfectiousPeriod = new GammaDistributionParam(
                        "Exposed to Infectious Period",
                        "The number of days from exposure to becoming infectious",
                        0.5f, 0.1f);

        private GammaDistributionParam infectiousToSymptomaticPeriod = new GammaDistributionParam(
                        "Infectious to Symptomatic Period",
                        "The number of days from becoming infectious to showing symptoms",
                        1f, 0.2f);

        private GammaDistributionParam mildToSevereSymptomaticPeriod = new GammaDistributionParam(
                        "Mild to Severe Symptomatic Period",
                        "The number of days from showing mild symptoms to showing severe symptoms",
                        1f, 0.2f);

        private GammaDistributionParam severeSymptomaticToDeathPeriod = new GammaDistributionParam(
                        "Severe Symptomatic to Death Period",
                        "The number of days from showing severe symptoms to death",
                        1f, 0.2f);

        private GammaDistributionParam asymptomaticToRecoveredPeriod = new GammaDistributionParam(
                        "Asymptomatic To Recovered Period",
                        "The number of days from being asymptomatic to recovering",
                        1f, 0.2f);

        private GammaDistributionParam mildSymptomaticToRecoveredPeriod = new GammaDistributionParam(
                        "Mild Symptomatic To Recovered Period",
                        "The number of days from showing mild symptoms to recovering",
                        1.25f, 0.25f);

        private GammaDistributionParam severeSymptomaticToRecoveredPeriod = new GammaDistributionParam(
                        "Severe Symptomatic To Recovered Period",
                        "The number of days from showing severe symptoms to recovering",
                        1.5f, 0.3f);

        public DiseaseParams() {
        }

        public DiseaseParams(DiseaseParams params) {
                initialInfected = new IntegerParam(params.initialInfected);
                transmissionRate = new FloatParam(params.transmissionRate);
                symptomaticProbability = new MapParam<AgeGroup, FloatParam>(params.symptomaticProbability);
                severeSymptomaticProbability = new MapParam<AgeGroup, FloatParam>(params.severeSymptomaticProbability);
                mortalityProbability = new MapParam<AgeGroup, FloatParam>(params.mortalityProbability);
                relativeMortalityWithoutHospitalisation = new FloatParam(
                                params.relativeMortalityWithoutHospitalisation);
                exposedToInfectiousPeriod = new GammaDistributionParam(params.exposedToInfectiousPeriod);
                infectiousToSymptomaticPeriod = new GammaDistributionParam(params.infectiousToSymptomaticPeriod);
                mildToSevereSymptomaticPeriod = new GammaDistributionParam(params.mildToSevereSymptomaticPeriod);
                severeSymptomaticToDeathPeriod = new GammaDistributionParam(params.severeSymptomaticToDeathPeriod);
                asymptomaticToRecoveredPeriod = new GammaDistributionParam(params.asymptomaticToRecoveredPeriod);
                mildSymptomaticToRecoveredPeriod = new GammaDistributionParam(params.mildSymptomaticToRecoveredPeriod);
                severeSymptomaticToRecoveredPeriod = new GammaDistributionParam(
                                params.severeSymptomaticToRecoveredPeriod);
        }

        public IntegerParam getInitialInfected() {
                return initialInfected;
        }

        public FloatParam getTransmissionRate() {
                return transmissionRate;
        }

        public MapParam<AgeGroup, FloatParam> getSymptomaticProbability() {
                return symptomaticProbability;
        }

        public MapParam<AgeGroup, FloatParam> getSevereSymptomaticProbability() {
                return severeSymptomaticProbability;
        }

        public MapParam<AgeGroup, FloatParam> getMortalityProbability() {
                return mortalityProbability;
        }

        public FloatParam getRelativeMortalityWithoutHospitalisation() {
                return relativeMortalityWithoutHospitalisation;
        }

        public GammaDistributionParam getExposedToInfectiousPeriod() {
                return exposedToInfectiousPeriod;
        }

        public GammaDistributionParam getInfectiousToSymptomaticPeriod() {
                return infectiousToSymptomaticPeriod;
        }

        public GammaDistributionParam getMildToSevereSymptomaticPeriod() {
                return mildToSevereSymptomaticPeriod;
        }

        public GammaDistributionParam getSevereSymptomaticToDeathPeriod() {
                return severeSymptomaticToDeathPeriod;
        }

        public GammaDistributionParam getAsymptomaticToRecoveredPeriod() {
                return asymptomaticToRecoveredPeriod;
        }

        public GammaDistributionParam getMildSymptomaticToRecoveredPeriod() {
                return mildSymptomaticToRecoveredPeriod;
        }

        public GammaDistributionParam getSevereSymptomaticToRecoveredPeriod() {
                return severeSymptomaticToRecoveredPeriod;
        }

        public boolean isDirty() {
                return initialInfected.isDirty() || transmissionRate.isDirty() || symptomaticProbability.isDirty()
                                || severeSymptomaticProbability.isDirty() || mortalityProbability.isDirty()
                                || relativeMortalityWithoutHospitalisation.isDirty()
                                || exposedToInfectiousPeriod.isDirty()
                                || infectiousToSymptomaticPeriod.isDirty() || mildToSevereSymptomaticPeriod.isDirty()
                                || severeSymptomaticToDeathPeriod.isDirty() || asymptomaticToRecoveredPeriod.isDirty()
                                || mildSymptomaticToRecoveredPeriod.isDirty()
                                || severeSymptomaticToRecoveredPeriod.isDirty();
        }

        public void clean() {
                initialInfected.clean();
                transmissionRate.clean();
                symptomaticProbability.clean();
                severeSymptomaticProbability.clean();
                mortalityProbability.clean();
                relativeMortalityWithoutHospitalisation.clean();
                exposedToInfectiousPeriod.clean();
                infectiousToSymptomaticPeriod.clean();
                mildToSevereSymptomaticPeriod.clean();
                severeSymptomaticToDeathPeriod.clean();
                asymptomaticToRecoveredPeriod.clean();
                mildSymptomaticToRecoveredPeriod.clean();
                severeSymptomaticToRecoveredPeriod.clean();
        }

        public Region getInputUI() {
                VBox inputs = new VBox(
                                initialInfected.getInputUI(),
                                transmissionRate.getInputUI(),
                                symptomaticProbability.getInputUI(),
                                severeSymptomaticProbability.getInputUI(),
                                mortalityProbability.getInputUI(),
                                relativeMortalityWithoutHospitalisation.getInputUI(),
                                exposedToInfectiousPeriod.getInputUI(),
                                infectiousToSymptomaticPeriod.getInputUI(),
                                mildToSevereSymptomaticPeriod.getInputUI(),
                                severeSymptomaticToDeathPeriod.getInputUI(),
                                asymptomaticToRecoveredPeriod.getInputUI(),
                                mildSymptomaticToRecoveredPeriod.getInputUI(),
                                severeSymptomaticToRecoveredPeriod.getInputUI());
                TitledPane titledPane = new TitledPane("Disease", inputs);
                titledPane.getStyleClass().add("big-titled-pane");
                return titledPane;
        }
}
