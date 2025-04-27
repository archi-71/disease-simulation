package simulation.params;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import simulation.interventions.InterventionType;

/**
 * Class to represent a single intervention parameter
 */
public class InterventionParam implements IParam {

        // The type of intervention
        private InterventionType type;

        // The start day of the intervention
        private IntegerParam start = new IntegerParam("Start Day",
                        "The first day this intervention is active",
                        1, 1, Integer.MAX_VALUE);

        // The end day of the intervention
        private IntegerParam end = new IntegerParam("End Day",
                        "The last day this intervention is active",
                        1, 1, Integer.MAX_VALUE);

        // The parameters associated with the intervention
        private MapParam<String, FloatParam> params;

        /**
         * Construct a new intervention parameter
         * @param type The type of intervention
         */
        public InterventionParam(InterventionType type) {
                this.type = type;
                params = new MapParam<String, FloatParam>(type.getName());
                switch (type) {
                        case MASKS:
                                // Mask inhalation protection
                                params.setValue("inhalationProtection",
                                                new FloatParam("Inhalation Protection",
                                                                "The effectiveness of the mask at minimising disease spread to the wearer (0 = no effect, 1 = no susceptibility)",
                                                                0f, 0f, 1f));
                                // Mask exhalation protection
                                params.setValue("exhalationProtection",
                                                new FloatParam("Exhalation Protection",
                                                                "The effectiveness of the mask at minimising disease spread from the wearer (0 = no effect, 1 = no infectivity)",
                                                                0f, 0f, 1f));
                                // Mask compliance
                                params.setValue("compliance",
                                                new FloatParam("Compliance",
                                                                "The probability that an individual will wear a mask",
                                                                0f, 0f, 1f));
                                break;
                        case SOCIAL_DISTANCING:
                                // Social distancing effectiveness
                                params.setValue("effectiveness",
                                                new FloatParam("Effectiveness",
                                                                "The effectiveness of social distancing at minimising close contacts (0 = no effect, 1 = no close contacts)",
                                                                0f, 0f, 1f));
                                // Social distancing compliance
                                params.setValue("compliance",
                                                new FloatParam("Compliance",
                                                                "The probability that an individual will follow social distancing",
                                                                0f, 0f, 1f));
                                break;
                        case ISOLATION:
                                // Isolation effectiveness
                                params.setValue("compliance",
                                                new FloatParam("Compliance",
                                                                "The probability that an individual will isolate when aware of infection",
                                                                0f, 0f, 1f));
                                break;
                        case TESTING:
                                // Testing effectiveness
                                params.setValue("frequency",
                                                new FloatParam("Frequency",
                                                                "The average number of tests a person will take each day",
                                                                0f, 0f, Float.MAX_VALUE));
                                // Testing result wait time
                                params.setValue("wait",
                                                new FloatParam("Result Wait Time",
                                                                "The average number of days between testing and receiving a result",
                                                                0f, 0f, Float.MAX_VALUE));
                                // Testing false positive rate
                                params.setValue("falsePositive",
                                                new FloatParam("False Positive Rate",
                                                                "The proportion of tests that falsely return a positive result",
                                                                0f, 0f, 1f));
                                // Testing false negative rate
                                params.setValue("falseNegative",
                                                new FloatParam("False Negative Rate",
                                                                "The proportion of tests that falsely return a negative result",
                                                                0f, 0f, 1f));
                                // Testing compliance
                                params.setValue("compliance",
                                                new FloatParam("Compliance",
                                                                "The probability that an individual will take tests",
                                                                0f, 0f, 1f));
                                break;
                        case TRACING_AND_QUARANTINE:
                                // Tracing effectiveness
                                params.setValue("effectiveness",
                                                new FloatParam("Tracing Effectiveness",
                                                                "The effectiveness of contact tracing (0 = no effect, 1 = all close contacts traced)",
                                                                0f, 0f, 1f));
                                // Tracing wait time
                                params.setValue("wait",
                                                new FloatParam("Tracing Wait Time",
                                                                "The average number of days for close contacts to be identified and traced after contact",
                                                                0f, 0f, Float.MAX_VALUE));
                                // Minimum quarantine time
                                params.setValue("time",
                                                new FloatParam("Minimum Quarantine Time",
                                                                "The minimum number of days a person must quarantine for after being traced",
                                                                0f, 0f, Float.MAX_VALUE));
                                // Quarantine compliance
                                params.setValue("compliance",
                                                new FloatParam("Compliance",
                                                                "The probability that an individual will quarantine when traced",
                                                                0f, 0f, 1f));
                                break;
                        case SCHOOL_CLOSURE:
                                // School closure proportion
                                params.setValue("closures",
                                                new FloatParam("Closures",
                                                                "The proportion of schools closed",
                                                                0f, 0f, 1f));
                                break;
                        case UNIVERSITY_CLOSURE:
                                // University closure proportion
                                params.setValue("closures",
                                                new FloatParam("Closures",
                                                                "The proportion of universities closed",
                                                                0f, 0f, 1f));
                                break;
                        case WORKPLACE_CLOSURE:
                                // Workplace closure proportion
                                params.setValue("closures",
                                                new FloatParam("Closures",
                                                                "The proportion of non-essential workplaces closed",
                                                                0f, 0f, 1f));
                                break;
                        case LOCKDOWN:
                                // Lockdown compliance
                                params.setValue("compliance",
                                                new FloatParam("Compliance",
                                                                "The probability that an individual will follow lockdown restrictions",
                                                                0f, 0f, 1f));
                                break;
                        case VACCINATION:
                                // Vaccination susceptibility reduction
                                params.setValue("susceptibilityReduction",
                                                new FloatParam(
                                                                "Susceptibility Reduction",
                                                                "The reduction in susceptibility to infection due to the vaccine (0 = no effect, 1 = no susceptibility)",
                                                                0f, 0f, 1f));
                                // Vaccination symptom severity reduction
                                params.setValue("severityReduction",
                                                new FloatParam("Symptom Severity Reduction",
                                                                "The reduction in symptom severity due to the vaccine (0 = no effect, 1 = always asymptomatic)",
                                                                0f, 0f, 1f));
                                // Vaccination rate
                                params.setValue("rate",
                                                new FloatParam("Rate",
                                                                "The proportion of the total population vaccinated each day",
                                                                0f, 0f, 1f));
                                // Vaccination compliance
                                params.setValue("compliance",
                                                new FloatParam("Compliance",
                                                                "The probability that an individual will accept the vaccine when available",
                                                                0f, 0f, 1f));
                                break;
                }
        }

        /**
         * Clone an intervention parameter
         * @param param Intervention parameter to copy
         */
        public InterventionParam(InterventionParam param) {
                type = param.type;
                start = new IntegerParam(param.start);
                end = new IntegerParam(param.end);
                params = new MapParam<String, FloatParam>(param.params);
        }

        /**
         * Get intervention type
         * @return Intervention type
         */
        public InterventionType getType() {
                return type;
        }

        /**
         * Get intervention start day
         * @return Intervention start day
         */
        public IntegerParam getStart() {
                return start;
        }

        /**
         * Get intervention end day
         * @return Intervention end day
         */
        public IntegerParam getEnd() {
                return end;
        }

        /**
         * Get intervention parameters
         * @return Intervention parameter map
         */
        public MapParam<String, FloatParam> getParams() {
                return params;
        }

        /**
         * Check if parameters have been modified
         * @return True if parameters have been modified
         */
        public boolean isDirty() {
                return start.isDirty() || end.isDirty() || params.isDirty();
        }

        /**
         * Mark parameters as up to date
         */
        public void clean() {
                start.clean();
                end.clean();
                params.clean();
        }

        /**
         * Generate UI to input intervention parameter
         * @return Pane for intervention parameter input
         */
        public Region getInputUI() {
                TitledPane titledPane = (TitledPane) params.getInputUI();
                titledPane.setContent(new VBox(start.getInputUI(), end.getInputUI(), titledPane.getContent()));
                titledPane.setExpanded(true);
                return titledPane;
        }
}
