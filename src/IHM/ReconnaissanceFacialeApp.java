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
import laBdd.Bdd;
import projet.ACP;
import projet.Retour;
import projet.Vecteur;
import imgs.ImageVisage;

/**
 * Main JavaFX application of the facial recognition project.
 * Builds the interface and navigates between the home/import view, the
 * selected-image view, and the result views (match found or no match).
 * @author Ouerghi Hedy
 * @version 0.2
 */
public class ReconnaissanceFacialeApp extends Application {

    /** The controller handling the "import a photo" button. */
    private CtrlImport ctrlImport;
    /** The root layout whose center is swapped to change views. */
    private BorderPane root;
    /** The primary stage of the application. */
    private Stage primaryStage;
    /** The ACP, built once at startup to avoid recomputing the eigenfaces. */
    private ACP acp;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        ctrlImport = new CtrlImport(primaryStage, this);

        // Builds the database and the ACP once (heavy computation done a single time).
        Bdd bdd = new Bdd();
        this.acp = new ACP(bdd.createA());

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

    /** Builds the green banner at the top of the window. */
    private HBox creerHeader() {
        Label titre = new Label("👥  Reconnaissance Faciale");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
        HBox header = new HBox(titre);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #C8E6C9;");
        return header;
    }

    /** Builds the home view : tool description and import zone. */
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
                "-fx-background-color: #C8E6C9; -fx-padding: 20; " +
                        "-fx-text-fill: #2e7d32; -fx-font-size: 13px;"
        );

        Label labelImport = new Label("📁  Importer une photo");
        labelImport.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label placeholder = new Label("Sélectionnez la photo que vous souhaitez analyser");
        placeholder.setStyle("-fx-text-fill: gray;");

        Button boutonImporter = new Button("+");
        boutonImporter.setStyle(
                "-fx-background-color: #43A047; -fx-text-fill: white; " +
                        "-fx-font-size: 18px; -fx-min-width: 40; -fx-min-height: 40; -fx-background-radius: 4;"
        );
        boutonImporter.setOnAction(ctrlImport);

        VBox zoneImport = new VBox(10, labelImport, placeholder, boutonImporter);
        zoneImport.setAlignment(Pos.CENTER);
        zoneImport.setPadding(new Insets(30));
        zoneImport.setStyle("-fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-color: white;");

        VBox conteneurImport = new VBox(zoneImport);
        conteneurImport.setPadding(new Insets(20));
        conteneurImport.setAlignment(Pos.TOP_CENTER);

        VBox vue = new VBox(description, conteneurImport);
        vue.setAlignment(Pos.TOP_CENTER);
        return vue;
    }

    /** Displays the selected image with a deselect and an analyse button. */
    public void afficherImageSelectionnee(File file) {
        ImageView preview = new ImageView(new Image(file.toURI().toString()));
        preview.setFitWidth(220);
        preview.setFitHeight(220);
        preview.setPreserveRatio(true);

        Label legende = new Label("Image sélectionnée");
        legende.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");

        Button boutonDeselectionner = new Button("Désélectionner");
        boutonDeselectionner.setStyle(
                "-fx-background-color: #E53935; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4;"
        );
        boutonDeselectionner.setOnAction(e -> root.setCenter(creerVueAccueil()));

        Button boutonAnalyser = new Button("Analyser");
        boutonAnalyser.setStyle(
                "-fx-background-color: #43A047; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4;"
        );
        boutonAnalyser.setOnAction(e -> analyser(file));

        HBox boutons = new HBox(15, boutonDeselectionner, boutonAnalyser);
        boutons.setAlignment(Pos.CENTER);

        VBox vue = new VBox(15, preview, legende, boutons);
        vue.setAlignment(Pos.CENTER);
        vue.setPadding(new Insets(30));

        root.setCenter(vue);
    }

    /**
     * Analyses the selected image and displays the matching view if a person is
     * recognized, or the no-match view otherwise.
     * @param file the image file selected by the user
     */
    private void analyser(File file) {
        ImageVisage nouvelleIm = new ImageVisage(0, file.getPath());
        Vecteur resultat = acp.identifier(nouvelleIm);
        if (resultat != null) {
            Retour[] tableau = acp.tableauComparaison(nouvelleIm);
            root.setCenter(new VueCorrespondanceTrouvee(file, tableau));
        } else {
            root.setCenter(creerVueAucuneCorrespondance(file));
        }
    }

    /**
     * Builds the no-match view, displaying the selected image and a message
     * indicating no correspondence was found.
     * @param file the image file selected by the user
     */
    private VBox creerVueAucuneCorrespondance(File file) {
        ImageView preview = new ImageView(new Image(file.toURI().toString()));
        preview.setFitWidth(220);
        preview.setFitHeight(220);
        preview.setPreserveRatio(true);

        Label message = new Label("Aucune correspondance trouvée");
        message.setStyle("-fx-font-style: italic; -fx-text-fill: #E53935;");

        Button boutonRetour = new Button("Importer une nouvelle photo");
        boutonRetour.setStyle(
                "-fx-background-color: #43A047; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4;"
        );
        boutonRetour.setOnAction(e -> root.setCenter(creerVueAccueil()));

        VBox vue = new VBox(15, preview, message, boutonRetour);
        vue.setAlignment(Pos.CENTER);
        vue.setPadding(new Insets(30));
        return vue;
    }

    public static void main(String[] args) {
        launch(args);
    }
}