package simulation.params;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class FloatParam implements IParam {

    private String name;
    private float value;

    public FloatParam(String name, float defaultValue) {
        this.name = name;
        this.value = defaultValue;
    }

    public float getValue() {
        return value;
    }

    public Region getInputUI() {
        Label label = new Label(name);
        TextField field = new TextField();
        field.setText(String.valueOf(value));
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                value = Float.parseFloat(newValue);
            } catch (NumberFormatException e) {
                field.setText(oldValue);
            }
        });
        return new HBox(label, field);
    }
}
