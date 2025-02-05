package simulation.params;

import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import simulation.interventions.InterventionType;

public class InterventionParam implements IParam {

    private InterventionType type;
    private IntegerParam start = new IntegerParam("Start day", 0);
    private IntegerParam end = new IntegerParam("End day", 0);
    private MapParam<String, FloatParam> params;

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

    public InterventionParam(InterventionType type) {
        this.type = type;
        params = new MapParam<String, FloatParam>(type.getName());
        switch (type) {
            case MASKS:
                params.setValue("incomingProtection", new FloatParam("Incoming Protection", 0f));
                params.setValue("outgoingProtection", new FloatParam("Outgoing Protection", 0f));
                params.setValue("compliance", new FloatParam("Compliance", 0f));
                break;
            case SOCIAL_DISTANCING:
                params.setValue("effectiveness", new FloatParam("Effectiveness", 0f));
                params.setValue("compliance", new FloatParam("Compliance", 0f));
                break;
            case TESTING:
                params.setValue("frequency", new FloatParam("Frequency", 0f));
                params.setValue("wait", new FloatParam("Result Wait Time", 0f));
                params.setValue("falsePositive", new FloatParam("False Positive Rate", 0f));
                params.setValue("falseNegative", new FloatParam("False Negative Rate", 0f));
                params.setValue("compliance", new FloatParam("Compliance", 0f));
                break;
            case ISOLATION:
                params.setValue("compliance", new FloatParam("Compliance", 0f));
                break;
            case TRACING_AND_QUARANTINE:
                params.setValue("effectiveness", new FloatParam("Tracing Effectiveness", 0f));
                params.setValue("wait", new FloatParam("Tracing Wait Time", 0f));
                params.setValue("time", new FloatParam("Minimum Quarantine Time", 0f));
                params.setValue("compliance", new FloatParam("Compliance", 0f));
                break;
            case SCHOOL_CLOSURE:
                params.setValue("closures", new FloatParam("Closures", 0f));
                break;
            case UNIVERSITY_CLOSURE:
                params.setValue("closures", new FloatParam("Closures", 0f));
                break;
            case WORKPLACE_CLOSURE:
                params.setValue("closures", new FloatParam("Closures", 0f));
                break;
            case LOCKDOWN:
                params.setValue("compliance", new FloatParam("Compliance", 0f));
                break;
            case VACCINATION:
                params.setValue("susceptibilityReduction", new FloatParam("Susceptibility Reduction", 0f));
                params.setValue("severityReduction", new FloatParam("Symptom Severity Reduction", 0f));
                params.setValue("rate", new FloatParam("Rate", 0f));
                params.setValue("compliance", new FloatParam("Compliance", 0f));
                break;
        }
    }

    public Region getInputUI() {
        VBox inputs = new VBox(
                start.getInputUI(),
                end.getInputUI(),
                params.getInputUI());
        return inputs;
    }
}
