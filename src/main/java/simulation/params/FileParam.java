package simulation.params;

import java.io.File;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class FileParam implements IParam {

    private String name;
    private File file;
    private Stage stage;

    public FileParam(String name, String defaultPath) {
        this.name = name;
        this.file = new File(defaultPath);
    }

    public File getFile() {
        return file;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Region getInputUI() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Shapefiles (*.shp)", "*.shp"));
        Label label = new Label(name);
        Button button = new Button("Select file");
        button.setOnAction(event -> {
            file = fileChooser.showOpenDialog(stage);
            ;
            label.setText(file.getName());
        });
        return new HBox(new Label("Buildings Shapefile: "), label, button);
    }
}
