package simulation.params;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Class to represent an integer parameter
 */
public class IntegerParam implements IParam {

    private String name;
    private String description;
    private int value;
    private int min;
    private int max;
    private boolean dirty;

    /**
     * Construct a new integer parameter
     * @param name  Parameter name
     * @param description Parameter description
     * @param defaultValue Default integer value
     * @param min Minimum integer value
     * @param max Maximum integer value
     */
    public IntegerParam(String name, String description, int defaultValue, int min, int max) {
        this.name = name;
        this.description = description;
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.dirty = true;
    }

    /**
     * Clone an integer parameter
     * @param param Integer parameter to copy
     */
    public IntegerParam(IntegerParam param) {
        this.name = param.name;
        this.value = param.value;
        this.dirty = param.dirty;
    }

    /**
     * Set integer value
     * @return Integer value
     */
    public int getValue() {
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
     * Generate UI to input integer parameter
     * @return Pane for integer parameter input
     */
    public Region getInputUI() {
        Label label = new Label(name);
        TextField field = new TextField();
        field.setText(String.valueOf(value));

        // Only update value once the field is unfocused
        field.focusedProperty().addListener((observable, wasFocussed, isFocussed) -> {
            if (!isFocussed) {
                int newValue;
                try {
                    newValue = Math.min(Math.max(Integer.parseInt(field.getText()), min), max);
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
