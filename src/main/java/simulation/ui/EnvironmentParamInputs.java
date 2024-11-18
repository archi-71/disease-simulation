package simulation.ui;

import java.io.File;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import simulation.params.EnvironmentParams;
import javafx.stage.Stage;

public class EnvironmentParamInputs extends VBox {

    public EnvironmentParamInputs(EnvironmentParams parameters, Stage stage) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Shapefiles (*.shp)", "*.shp"));

        Label buildingsFileLabel = new Label(parameters.getBuildingsFile().getName());
        Button buidlingsFileButton = new Button("Select file");
        buidlingsFileButton.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(stage);
            parameters.setBuildingsFile(file);
            buildingsFileLabel.setText(file.getName());
        });
        HBox buildingsFile = new HBox(new Label("Buildings Shapefile: "), buildingsFileLabel, buidlingsFileButton);

        Label roadsFileLabel = new Label(parameters.getRoadsFile().getName());
        Button roadsFileButton = new Button("Select file");
        roadsFileButton.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(stage);
            parameters.setRoadsFile(file);
            roadsFileLabel.setText(file.getName());
        });
        HBox roadsFile = new HBox(new Label("Roads Shapefile: "), roadsFileLabel, roadsFileButton);

        getChildren().addAll(buildingsFile, roadsFile);
    }
}
