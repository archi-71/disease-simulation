package simulation.params;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import simulation.interventions.InterventionType;

public class InterventionParam implements IParam {

        private InterventionType type;

        private IntegerParam start = new IntegerParam("Start day",
                        "The first day this intervention is active",
                        1, 1, Integer.MAX_VALUE);

        private IntegerParam end = new IntegerParam("End day",
                        "The last day this intervention is active",
                        1, 1, Integer.MAX_VALUE);

        private MapParam<String, FloatParam> params;

        public InterventionParam(InterventionType type) {
                this.type = type;
                params = new MapParam<String, FloatParam>(type.getName());
                switch (type) {
                        case MASKS:
                                params.setValue("incomingProtection",
                                                new FloatParam("Incoming Protection",
                                                                "The effectiveness of the mask at minimising disease spread to the wearer (0 = no effect, 1 = no susceptibility)",
                                                                0f, 0f, 1f));
                                params.setValue("outgoingProtection",
                                                new FloatParam("Outgoing Protection",
                                                                "The effectiveness of the mask at minimising disease spread from the wearer (0 = no effect, 1 = no infectivity)",
                                                                0f, 0f, 1f));
                                params.setValue("compliance",
                                                new FloatParam("Compliance",
                                                                "The probability that an individual will wear a mask",
                                                                0f, 0f, 1f));
                                break;
                        case SOCIAL_DISTANCING:
                                params.setValue("effectiveness",
                                                new FloatParam("Effectiveness",
                                                                "The effectiveness of social distancing at minimising close contacts (0 = no effect, 1 = no close contacts)",
                                                                0f, 0f, 1f));
                                params.setValue("compliance",
                                                new FloatParam("Compliance",
                                                                "The probability that an individual will follow social distancing",
                                                                0f, 0f, 1f));
                                break;
                        case TESTING:
                                params.setValue("frequency",
                                                new FloatParam("Frequency",
                                                                "The average number of tests a person will take each day",
                                                                0f, 0f, Float.MAX_VALUE));
                                params.setValue("wait",
                                                new FloatParam("Result Wait Time",
                                                                "The average number of days between testing and receiving a result",
                                                                0f, 0f, Float.MAX_VALUE));
                                params.setValue("falsePositive",
                                                new FloatParam("False Positive Rate",
                                                                "The proportion of tests that falsely return a positive result",
                                                                0f, 0f, 1f));
                                params.setValue("falseNegative",
                                                new FloatParam("False Negative Rate",
                                                                "The proportion of tests that falsely return a negative result",
                                                                0f, 0f, 1f));
                                params.setValue("compliance",
                                                new FloatParam("Compliance",
                                                                "The probability that an individual will take tests",
                                                                0f, 0f, 1f));
                                break;
                        case ISOLATION:
                                params.setValue("compliance",
                                                new FloatParam("Compliance",
                                                                "The probability that an individual will isolate when aware of infection",
                                                                0f, 0f, 1f));
                                break;
                        case TRACING_AND_QUARANTINE:
                                params.setValue("effectiveness",
                                                new FloatParam("Tracing Effectiveness",
                                                                "The effectiveness of contact tracing (0 = no effect, 1 = all close contacts traced)",
                                                                0f, 0f, 1f));
                                params.setValue("wait",
                                                new FloatParam("Tracing Wait Time",
                                                                "The average number of days for close contacts to be identified and traced",
                                                                0f, 0f, Float.MAX_VALUE));
                                params.setValue("time",
                                                new FloatParam("Minimum Quarantine Time",
                                                                "The minimum number of days a person must quarantine for after being traced",
                                                                0f, 0f, Float.MAX_VALUE));
                                params.setValue("compliance",
                                                new FloatParam("Compliance",
                                                                "The probability that an individual will quarantine when traced",
                                                                0f, 0f, 1f));
                                break;
                        case SCHOOL_CLOSURE:
                                params.setValue("closures",
                                                new FloatParam("Closures",
                                                                "The proportion of schools closed",
                                                                0f, 0f, 1f));
                                break;
                        case UNIVERSITY_CLOSURE:
                                params.setValue("closures",
                                                new FloatParam("Closures",
                                                                "The proportion of universities closed",
                                                                0f, 0f, 1f));
                                break;
                        case WORKPLACE_CLOSURE:
                                params.setValue("closures",
                                                new FloatParam("Closures",
                                                                "The proportion of non-essential workplaces closed",
                                                                0f, 0f, 1f));
                                break;
                        case LOCKDOWN:
                                params.setValue("compliance",
                                                new FloatParam("Compliance",
                                                                "The probability that an individual will follow lockdown restrictions",
                                                                0f, 0f, 1f));
                                break;
                        case VACCINATION:
                                params.setValue("susceptibilityReduction",
                                                new FloatParam(
                                                                "Susceptibility Reduction",
                                                                "The reduction in susceptibility to infection due to the vaccine (0 = no effect, 1 = no susceptibility)",
                                                                0f, 0f, 1f));
                                params.setValue("severityReduction",
                                                new FloatParam("Symptom Severity Reduction",
                                                                "The reduction in symptom severity due to the vaccine (0 = no effect, 1 = always asymptomatic)",
                                                                0f, 0f, 1f));
                                params.setValue("rate",
                                                new FloatParam("Rate",
                                                                "The proportion of the total population vaccinated each day",
                                                                0f, 0f, 1f));
                                params.setValue("compliance",
                                                new FloatParam("Compliance",
                                                                "The probability that an individual will accept the vaccine",
                                                                0f, 0f, 1f));
                                break;
                }
        }

        public InterventionParam(InterventionParam param) {
                type = param.type;
                start = new IntegerParam(param.start);
                end = new IntegerParam(param.end);
                params = new MapParam<String, FloatParam>(param.params);
        }

        public InterventionType getType() {
                return type;
        }

        public IntegerParam getStart() {
                return start;
        }

        public IntegerParam getEnd() {
                return end;
        }

        public MapParam<String, FloatParam> getParams() {
                return params;
        }

        public boolean isDirty() {
                return start.isDirty() || end.isDirty() || params.isDirty();
        }

        public void clean() {
                start.clean();
                end.clean();
                params.clean();
        }

        public Region getInputUI() {
                TitledPane titledPane = (TitledPane) params.getInputUI();
                titledPane.setContent(new VBox(start.getInputUI(), end.getInputUI(), titledPane.getContent()));
                titledPane.setExpanded(true);
                return titledPane;
        }
}
