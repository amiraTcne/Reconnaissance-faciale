package IHM;

import java.io.File;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * Main JavaFX application of the facial recognition project.
 * This class builds the graphical interface and manages the different views the user
 * can navigate through
 * @author Ouerghi Hedy
 * @version 0.1
 */
public class ReconnaissanceFacialeApp extends Application {

    /** The controller handling the "import a photo" button (opens a FileChooser). */
    private CtrlImport ctrlImport;
    /** The root layout of the scene; its center node is swapped to change views. */
    private BorderPane root;
    /** The primary stage of the application, kept to open dialogs over it. */
    private Stage primaryStage;

    /**
     * Entry point of the JavaFX application. Initializes the controller, builds the
     * root layout with the header and the home view, then displays the window.
     * @param primaryStage the primary stage provided by the JavaFX runtime
     * @throws Exception if the application fails to start
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        ctrlImport = new CtrlImport(primaryStage, this);

        root = new BorderPane();
        root.setTop(creerHeader());
        root.setCenter(creerVueAccueil());

        Scene scene = new Scene(root, 900, 500);
        primaryStage.setTitle("Reconnaissance Faciale");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(400);
        primaryStage.show();
    }

    /**
     * Builds the green banner displayed at the top of the window.
     * @return an HBox containing the centered application title
     */
    private HBox creerHeader() {
        Label titre = new Label("👥  Reconnaissance Faciale");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
        HBox header = new HBox(titre);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #C8E6C9;");
        return header;
    }

    /**
     * Builds the home view, composed of a description of the tool and an import zone
     * containing the button that triggers the file selection.
     * @return a VBox containing the description and the import zone
     */
    private VBox creerVueAccueil() {
        Label description = new Label(
                "Notre outil est un outil de reconnaissance faciale. " +
                        "Vous pouvez choisir une image et elle sera analysée.\n" +
                        "Le but est de trouver à quelle personne correspond cette image.\n" +
                        "Si une correspondance est trouvée, l'image la plus proche de celle fournie " +
                        "sera affichée à côté avec le pourcentage de correspondance associé."
        );
        description.setWrapText(true);
        description.setTextAlignment(TextAlignment.CENTER);
        description.setMaxWidth(Double.MAX_VALUE);
        description.setStyle(
                "-fx-background-color: #C8E6C9; " +
                        "-fx-padding: 20; " +
                        "-fx-text-fill: #2e7d32; " +
                        "-fx-font-size: 13px;"
        );

        Label labelImport = new Label("📁  Importer une photo");
        labelImport.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label placeholder = new Label("Sélectionnez la photo que vous souhaitez analyser");
        placeholder.setStyle("-fx-text-fill: gray;");

        Button boutonImporter = new Button("+");
        boutonImporter.setStyle(
                "-fx-background-color: #43A047; -fx-text-fill: white; " +
                        "-fx-font-size: 18px; -fx-min-width: 40; -fx-min-height: 40; " +
                        "-fx-background-radius: 4;"
        );
        boutonImporter.setOnAction(ctrlImport);

        VBox zoneImport = new VBox(10, labelImport, placeholder, boutonImporter);
        zoneImport.setAlignment(Pos.CENTER);
        zoneImport.setPadding(new Insets(30));
        zoneImport.setStyle(
                "-fx-border-color: #ddd; -fx-border-radius: 8; " +
                        "-fx-background-color: white;"
        );

        VBox conteneurImport = new VBox(zoneImport);
        conteneurImport.setPadding(new Insets(20));
        conteneurImport.setAlignment(Pos.TOP_CENTER);

        VBox vue = new VBox(description, conteneurImport);
        vue.setAlignment(Pos.TOP_CENTER);
        return vue;
    }

    /**
     * Displays the selected-image view : a preview of the chosen image, a caption,
     * and two buttons allowing the user to either deselect the image (going back to
     * the home view) or analyse it.
     * @param file the image file selected by the user through the FileChooser
     */
    public void afficherImageSelectionnee(File file) {
        ImageView preview = new ImageView(new Image(file.toURI().toString()));
        preview.setFitWidth(220);
        preview.setFitHeight(220);
        preview.setPreserveRatio(true);

        Label legende = new Label("Image sélectionnée");
        legende.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");

        Button boutonDeselectionner = new Button("Désélectionner");
        boutonDeselectionner.setStyle(
                "-fx-background-color: #E53935; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-background-radius: 4;"
        );
        boutonDeselectionner.setOnAction(e -> root.setCenter(creerVueAccueil()));

        Button boutonAnalyser = new Button("Analyser");
        boutonAnalyser.setStyle(
                "-fx-background-color: #43A047; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-background-radius: 4;"
        );
        boutonAnalyser.setOnAction(e -> System.out.println("Analyse de : " + file.getAbsolutePath()));

        HBox boutons = new HBox(15, boutonDeselectionner, boutonAnalyser);
        boutons.setAlignment(Pos.CENTER);

        VBox vue = new VBox(15, preview, legende, boutons);
        vue.setAlignment(Pos.CENTER);
        vue.setPadding(new Insets(30));

        root.setCenter(vue);
    }

    /**
     * Launches the JavaFX application.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}