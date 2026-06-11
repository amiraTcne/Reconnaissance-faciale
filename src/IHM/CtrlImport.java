package IHM;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controller for the "Importer une photo" button.
 * When the button is clicked, this controller opens a FileChooser allowing the user
 * to select an image file. The chooser is initialized in the dataset folder when it
 * exists, and is filtered to only show image files. Once a file is selected, it is
 * passed to the application so it can display the selected-image view.
 * @author Ouerghi Hedy
 * @version 0.1
 */
public class CtrlImport implements EventHandler<ActionEvent> {

    /** The stage over which the FileChooser dialog is opened. */
    private Stage stage;
    /** The application notified with the selected file so it can update its view. */
    private ReconnaissanceFacialeApp app;

    /**
     * Constructor for CtrlImport.
     * @param stage the stage over which the FileChooser will be displayed
     * @param app   the application to notify once an image has been selected
     */
    public CtrlImport(Stage stage, ReconnaissanceFacialeApp app) {
        this.stage = stage;
        this.app = app;
    }

    /**
     * Handles the click on the import button : opens a FileChooser filtered on image
     * files, starting in the dataset folder if it exists, and transmits the chosen
     * file to the application. Does nothing if the user cancels the selection.
     * @param event the action event fired by the import button
     */
    @Override
    public void handle(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select an image");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.bmp")
        );
        File datasetDir = new File("dataset");
        if (datasetDir.exists() && datasetDir.isDirectory()) {
            chooser.setInitialDirectory(datasetDir);
        }
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            app.afficherImageSelectionnee(file);
        }
    }
}
