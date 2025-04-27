package simulation.params;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Class to represent a float parameter
 */
public class FloatParam implements IParam {

    private String name;
    private String description;
    private float value;
    private float min;
    private float max;
    private boolean dirty;

    /**
     * Construct a new float parameter
     * @param name Parameter name
     * @param description Parameter description
     * @param defaultValue Default float value
     * @param min Minimum float value
     * @param max Maximum float value
     */
    public FloatParam(String name, String description, float defaultValue, float min, float max) {
        this.name = name;
        this.description = description;
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.dirty = true;
    }

    /**
     * Clone a float parameter
     * @param param Float parameter to copy
     */
    public FloatParam(FloatParam param) {
        this.name = param.name;
        this.value = param.value;
        this.dirty = param.dirty;
    }

    /**
     * Set float value
     * @return Float value
     */
    public float getValue() {
        return value;
    }

    /**
     * Check if parameter has been modified
     * @return True if parameter has been modified
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Mark parameter as up to date
     */
    public void clean() {
        dirty = false;
    }

    /**
     * Generate UI to input float parameter
     * @return Pane for float parameter input
     */
    public Region getInputUI() {
        Label label = new Label(name);
        TextField field = new TextField();
        field.setText(String.valueOf(value));

        // Only update value once the field is unfocused
        field.focusedProperty().addListener((observable, wasFocussed, isFocussed) -> {
            if (!isFocussed) {
                float newValue;
                try {
                    newValue = Math.min(Math.max(Float.parseFloat(field.getText()), min), max);
                } catch (NumberFormatException e) {
                    newValue = value;
                }
                if (value != newValue) {
                    value = newValue;
                    dirty = true;
                }
                field.setText(String.valueOf(value));
            }
        });
        field.setAlignment(Pos.CENTER_RIGHT);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        HBox box = new HBox(label, space, field);
        box.setAlignment(Pos.CENTER);
        Tooltip tip = new Tooltip(description);
        Tooltip.install(box, tip);

        return box;
    }
}
