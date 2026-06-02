package IHM;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Contrôleur du bouton "Importer une photo".
 * Ouvre un FileChooser et affiche le chemin sélectionné dans la console.
 */
public class CtrlImport implements EventHandler<ActionEvent> {

    private Stage stage;

    public CtrlImport(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void handle(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir une image");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.bmp")
        );
        File datasetDir = new File("dataset");
        if (datasetDir.exists() && datasetDir.isDirectory()) {
            chooser.setInitialDirectory(datasetDir);
        }
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            System.out.println("Image sélectionnée : " + file.getAbsolutePath());
        }
    }
}
