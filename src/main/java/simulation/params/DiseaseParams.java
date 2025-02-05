package simulation.params;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import simulation.population.AgeGroup;

public class DiseaseParams implements IParam {

    private IntegerParam initialInfected = new IntegerParam("Initital Number Infected", 1);

    private FloatParam transmissionRate = new FloatParam("Transmission Rate", 5f);

    private MapParam<AgeGroup, FloatParam> symptomaticProbability = new MapParam<AgeGroup, FloatParam>(
            "Symptomatic Probability By Age") {
        {
            setValue(AgeGroup._0_TO_9, new FloatParam("0 to 9 years", 0.5f));
            setValue(AgeGroup._10_TO_19, new FloatParam("10 to 19 years", 0.55f));
            setValue(AgeGroup._20_TO_29, new FloatParam("20 to 29 years", 0.6f));
            setValue(AgeGroup._30_TO_39, new FloatParam("30 to 39 years", 0.65f));
            setValue(AgeGroup._40_TO_49, new FloatParam("40 to 49 years", 0.7f));
            setValue(AgeGroup._50_TO_59, new FloatParam("50 to 59 years", 0.75f));
            setValue(AgeGroup._60_TO_69, new FloatParam("60 to 69 years", 0.8f));
            setValue(AgeGroup._70_TO_79, new FloatParam("70 to 79 years", 0.85f));
            setValue(AgeGroup._80_TO_89, new FloatParam("80 to 89 years", 0.9f));
            setValue(AgeGroup._90_PLUS, new FloatParam("90+ years", 0.95f));
        }
    };
    private MapParam<AgeGroup, FloatParam> severeSymptomaticProbability = new MapParam<AgeGroup, FloatParam>(
            "Severe Symptomatic Probability By Age") {
        {
            setValue(AgeGroup._0_TO_9, new FloatParam("0 to 9 years", 0.0005f));
            setValue(AgeGroup._10_TO_19, new FloatParam("10 to 19 years", 0.001f));
            setValue(AgeGroup._20_TO_29, new FloatParam("20 to 29 years", 0.005f));
            setValue(AgeGroup._30_TO_39, new FloatParam("30 to 39 years", 0.01f));
            setValue(AgeGroup._40_TO_49, new FloatParam("40 to 49 years", 0.05f));
            setValue(AgeGroup._50_TO_59, new FloatParam("50 to 59 years", 0.1f));
            setValue(AgeGroup._60_TO_69, new FloatParam("60 to 69 years", 0.2f));
            setValue(AgeGroup._70_TO_79, new FloatParam("70 to 79 years", 0.3f));
            setValue(AgeGroup._80_TO_89, new FloatParam("80 to 89 years", 0.4f));
            setValue(AgeGroup._90_PLUS, new FloatParam("90+ years", 0.4f));
        }
    };
    private MapParam<AgeGroup, FloatParam> mortalityProbability = new MapParam<AgeGroup, FloatParam>(
            "Mortality Probability By Age") {
        {
            setValue(AgeGroup._0_TO_9, new FloatParam("0 to 9 years", 0.0001f));
            setValue(AgeGroup._10_TO_19, new FloatParam("10 to 19 years", 0.0002f));
            setValue(AgeGroup._20_TO_29, new FloatParam("20 to 29 years", 0.001f));
            setValue(AgeGroup._30_TO_39, new FloatParam("30 to 39 years", 0.005f));
            setValue(AgeGroup._40_TO_49, new FloatParam("40 to 49 years", 0.01f));
            setValue(AgeGroup._50_TO_59, new FloatParam("50 to 59 years", 0.02f));
            setValue(AgeGroup._60_TO_69, new FloatParam("60 to 69 years", 0.05f));
            setValue(AgeGroup._70_TO_79, new FloatParam("70 to 79 years", 0.15f));
            setValue(AgeGroup._80_TO_89, new FloatParam("80 to 89 years", 0.25f));
            setValue(AgeGroup._90_PLUS, new FloatParam("90+ years", 0.3f));
        }
    };

    private FloatParam relativeMortalityWithoutHospitalisation = new FloatParam(
            "Relative Mortality Without Hospitalisation", 3f);

    private GammaDistributionParam exposedToInfectiousPeriod = new GammaDistributionParam(
            "Exposed to Infectious Period (days)", 0.5f, 0.1f);
    private GammaDistributionParam infectiousToSymptomaticPeriod = new GammaDistributionParam(
            "Infectious to Symptomatic Period (days)", 1f, 0.2f);
    private GammaDistributionParam mildToSevereSymptomaticPeriod = new GammaDistributionParam(
            "Mild to Severe Symptomatic Period (days)", 1f, 0.2f);
    private GammaDistributionParam severeSymptomaticToDeathPeriod = new GammaDistributionParam(
            "Severe Symptomatic to Death Period (days)", 1f, 0.2f);
    private GammaDistributionParam asymptomaticToRecoveredPeriod = new GammaDistributionParam(
            "Asymptomatic To Recovered Period (days)", 1f, 0.2f);
    private GammaDistributionParam mildSymptomaticToRecoveredPeriod = new GammaDistributionParam(
            "Mild Symptomatic To Recovered Period (days)", 1.25f, 0.25f);
    private GammaDistributionParam severeSymptomaticToRecoveredPeriod = new GammaDistributionParam(
            "Severe Symptomatic To Recovered Period (days)",
            1.5f, 0.3f);

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

    public DiseaseParams() {
    }

    public DiseaseParams(DiseaseParams params) {
        initialInfected = params.initialInfected;
        transmissionRate = params.transmissionRate;
        symptomaticProbability = params.symptomaticProbability;
        severeSymptomaticProbability = params.severeSymptomaticProbability;
        mortalityProbability = params.mortalityProbability;
        relativeMortalityWithoutHospitalisation = params.relativeMortalityWithoutHospitalisation;
        exposedToInfectiousPeriod = params.exposedToInfectiousPeriod;
        infectiousToSymptomaticPeriod = params.infectiousToSymptomaticPeriod;
        mildToSevereSymptomaticPeriod = params.mildToSevereSymptomaticPeriod;
        severeSymptomaticToDeathPeriod = params.severeSymptomaticToDeathPeriod;
        asymptomaticToRecoveredPeriod = params.asymptomaticToRecoveredPeriod;
        mildSymptomaticToRecoveredPeriod = params.mildSymptomaticToRecoveredPeriod;
        severeSymptomaticToRecoveredPeriod = params.severeSymptomaticToRecoveredPeriod;
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
        return titledPane;
    }
}
