package simulation.params;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class IntegerParam implements IParam {

    private String name;
    private int value;

    public IntegerParam(String name, int defaultValue) {
        this.name = name;
        this.value = defaultValue;
    }

    public int getValue() {
        return value;
    }

    public Region getInputUI() {
        Label label = new Label(name);
        TextField field = new TextField();
        field.setText(String.valueOf(value));
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                value = Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                field.setText(oldValue);
            }
        });
        return new HBox(label, field);
    }
}
