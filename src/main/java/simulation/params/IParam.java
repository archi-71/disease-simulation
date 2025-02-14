package simulation.params;

import javafx.scene.layout.Region;

public interface IParam {

    public boolean isDirty();

    public void clean();

    public Region getInputUI();

}
