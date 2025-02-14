package simulation.params;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class FileParam implements IParam {

    private Stage stage;
    private String name;
    private String description;
    private File file;
    private boolean dirty;

    public FileParam(String name, String description, String defaultPath) {
        this.name = name;
        this.description = description;
        this.file = new File(defaultPath);
        this.dirty = true;
    }

    public FileParam(FileParam param) {
        this.name = param.name;
        this.file = new File(param.file.getAbsolutePath());
        this.dirty = param.dirty;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public File getFile() {
        return file;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clean() {
        dirty = false;
    }

    public Region getInputUI() {
        Label inputLabel = new Label(name);
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Shapefiles (*.shp)", "*.shp"));
        Label fileLabel = new Label(file.getName());
        fileLabel.getStyleClass().add("italics");
        Button button = new Button("Select file");
        button.setOnAction(event -> {
            File newFile = fileChooser.showOpenDialog(stage);
            try {
                if (!Files.isSameFile(file.toPath(), newFile.toPath())) {
                    file = newFile;
                    fileLabel.setText(file.getName());
                    dirty = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        HBox fileInput = new HBox(fileLabel, button);
        fileInput.setAlignment(Pos.CENTER_RIGHT);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        HBox box = new HBox(inputLabel, space, fileInput);
        box.setAlignment(Pos.CENTER);
        Tooltip tip = new Tooltip(description);
        Tooltip.install(box, tip);

        return box;
    }
}
