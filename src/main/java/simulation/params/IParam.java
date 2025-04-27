package simulation.params;

import javafx.scene.layout.Region;

/**
 * Interface to represent a simulation parameter
 */
public interface IParam {
    // Get 'dirtiness' of parameter, indicating change 
    public boolean isDirty();

    // Set 'dirtiness' of parameter to false
    public void clean();

    // Generate UI for the user to input parameter
    public Region getInputUI();
}
