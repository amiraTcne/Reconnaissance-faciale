package IHM;

import java.io.File;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import laBdd.Bdd;
import laBdd.Personne;
import calculs.ACP;
import calculs.Retour;
import calculs.Vecteur;
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
    /** The database, kept to resolve image indices to their file paths. */
    private Bdd bdd;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        ctrlImport = new CtrlImport(primaryStage, this);

        // Builds the database and the ACP once (heavy computation done a single time).
        this.bdd = new Bdd();
        this.acp = new ACP(bdd.createA());

        root = new BorderPane();
        root.setTop(creerHeader());
        root.setCenter(creerVueAccueil());

        Scene scene = new Scene(root, 900, 500);
        primaryStage.setTitle("Facial Recognition");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(400);
        primaryStage.show();
    }

    /** Builds the green banner at the top of the window. */
    private HBox creerHeader() {
        Label titre = new Label("👥  Facial Recognition");
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
                "This interface is a facial recognition tool. " +
                        "You can select an image, and it will be analyzed.\n" +
                        "The goal is to determine who is in this photo.\n"
        );
        description.setWrapText(true);
        description.setTextAlignment(TextAlignment.CENTER);
        description.setAlignment(Pos.CENTER);
        description.setMaxWidth(Double.MAX_VALUE);
        description.setStyle(
                "-fx-background-color: #C8E6C9; -fx-padding: 20; " +
                        "-fx-text-fill: #2e7d32; -fx-font-size: 13px;"
        );

        Label labelImport = new Label("📁  Import an image");
        labelImport.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label placeholder = new Label("Select the photo you want to analyze");
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

        Label legende = new Label("Selected image");
        legende.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");

        Button boutonDeselectionner = new Button("Modify");
        boutonDeselectionner.setStyle(
                "-fx-background-color: #E53935; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4;"
        );
        boutonDeselectionner.setOnAction(e -> root.setCenter(creerVueAccueil()));

        Button boutonAnalyser = new Button("Analyze");
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
        Retour[] tableau = acp.uneImageParPersonne(nouvelleIm);
        if (resultat != null) {
            root.setCenter(new VueCorrespondanceTrouvee(file, tableau,ctrlImport));
        } else {
            root.setCenter(creerVueAucuneCorrespondance(file, tableau));
        }
    }
    /**
     * Builds the no-match view : the selected image and a red message on the left,
     * a re-import zone in the center, and on the right a carousel browsing the base
     * images that were compared, ordered from the closest match to the farthest.
     * @param file    the image file selected by the user
     * @param tableau the sorted comparison results (closest match first)
     * @return the assembled no-match view
     */
    private HBox creerVueAucuneCorrespondance(File file, Retour[] tableau) {
        // Left : selected image + "no match" message
        ImageView preview = new ImageView(new Image(file.toURI().toString()));
        ImageVisage nouvelleIm = new ImageVisage(0, file.getPath());
        preview.setFitWidth(250);
        preview.setFitHeight(250);
        preview.setPreserveRatio(true);
        
        Label legende = new Label("Selected image");
        legende.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");

        Label message = new Label("No matches found");
        message.setStyle("-fx-font-style: italic; -fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #E53935;");
        message.setWrapText(true);
        message.setTextAlignment(TextAlignment.CENTER);
        message.setMaxWidth(250);
        
        VBox gauche = new VBox(15, preview, legende, message);
        gauche.setAlignment(Pos.CENTER);
        gauche.setPadding(new Insets(0, 0, 80, 0));
        
        // Center : re-import zone
        Label labelImport = new Label("📁  Import a new image");
        labelImport.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label placeholder = new Label("Select the photo you want to analyze");
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

        VBox centre = new VBox(zoneImport);
        centre.setAlignment(Pos.CENTER);
        centre.setPadding(new Insets(0, 0, 60, 110));
        // Right : nav buttons + large display + thumbnail strip
        ImageView imageComparee = new ImageView();
        imageComparee.setFitWidth(250);
        imageComparee.setFitHeight(250);
        imageComparee.setPreserveRatio(true);

        Label nomPersonne = new Label();
        nomPersonne.setStyle("-fx-font-weight: bold; -fx-text-fill: #2e7d32;");

        Label pourcentage = new Label();
        pourcentage.setStyle("-fx-text-fill: gray;");
        
        HBox bandeAutresImages = new HBox(50);
        bandeAutresImages.setAlignment(Pos.CENTER);

        Button precedent = new Button("Previous");
        Button suivant = new Button("Next");
        HBox navigation = new HBox(10, precedent, suivant);
        navigation.setAlignment(Pos.CENTER);

        VBox grandAffichage = new VBox(12, navigation, imageComparee, nomPersonne, pourcentage, bandeAutresImages);
        grandAffichage.setAlignment(Pos.CENTER);

        // Number of thumbnails shown
        int nbVignettes = tableau.length;

        // Column of thumbnails; the selected one gets a green border.
        VBox colonneVignettes = new VBox(8);
        colonneVignettes.setAlignment(Pos.TOP_CENTER);
        colonneVignettes.setPadding(new Insets(5));

        Button[] vignettes = new Button[nbVignettes];
        // Index of the currently displayed image (mutable via a 1-cell array).
        int[] courant = {0};

        // Updates the large display, the percentage, the green border and the nav buttons.
        Runnable maj = () -> {
            Retour r = tableau[courant[0]];
            ImageVisage img = bdd.getImg(r.getIndice());
            Personne p = bdd.rechercher(r.getIndice());
            imageComparee.setImage(new Image(new File(img.getPath()).toURI().toString()));
            nomPersonne.setText(p.getPrenom() + " " + p.getNom());
            pourcentage.setText(String.format("Match score : %.0f%%", r.getPourcentage()));
            bandeAutresImages.getChildren().clear();

            for (ImageVisage autreImg : bdd.imagesBdd.get(p)) {
                if (autreImg.id != img.id) {
                    ImageView mini = new ImageView(new Image(new File(autreImg.getPath()).toURI().toString()));
                    mini.setFitWidth(100);
                    mini.setFitHeight(100);
                    mini.setPreserveRatio(true);

                    float pMini = acp.pourcentageImage(nouvelleIm, autreImg.id);

                    Label labelMini = new Label(String.format("%.0f %%", pMini));
                    labelMini.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");

                    VBox blocMini = new VBox(5, mini, labelMini);
                    blocMini.setAlignment(Pos.CENTER);
                    Button boutonMini = new Button();
                    boutonMini.setGraphic(blocMini);
                    boutonMini.setOnAction(e -> {
                        imageComparee.setImage(mini.getImage());
                        pourcentage.setText(String.format("Match score : %.0f%%", pMini));
                    });
                    bandeAutresImages.getChildren().add(boutonMini);
                }
            }
            for (int k = 0; k < vignettes.length; k++) {
                vignettes[k].setStyle("-fx-background-color: transparent; -fx-padding: 2;");
            }
            vignettes[courant[0]].setStyle(
                    "-fx-background-color: transparent; -fx-padding: 2; " +
                            "-fx-border-color: #43A047; -fx-border-width: 3;"
            );
            precedent.setDisable(courant[0] == 0);
            suivant.setDisable(courant[0] == nbVignettes - 1);
        };

        precedent.setOnAction(e -> { if (courant[0] > 0) { courant[0]--; maj.run(); } });
        suivant.setOnAction(e -> { if (courant[0] < nbVignettes - 1) { courant[0]++; maj.run(); } });

        for (int i = 0; i < nbVignettes; i++) {
            Retour r = tableau[i];
            ImageVisage img = bdd.getImg(r.getIndice());
            ImageView icone = new ImageView(new Image(new File(img.getPath()).toURI().toString()));
            icone.setFitWidth(150);
            icone.setFitHeight(150);
            icone.setPreserveRatio(true);

            Button bouton = new Button();
            bouton.setGraphic(icone);
            bouton.setStyle("-fx-background-color: transparent; -fx-padding: 2;");
            final int indice = i;
            bouton.setOnAction(e -> { courant[0] = indice; maj.run(); });
            vignettes[i] = bouton;
            colonneVignettes.getChildren().add(bouton);
        }

        ScrollPane scrollVignettes = new ScrollPane(colonneVignettes);
        scrollVignettes.setFitToWidth(true);
        scrollVignettes.setPrefWidth(300);
        scrollVignettes.setPrefViewportHeight(300);

        // select the closest match by default
        maj.run();

        HBox droite = new HBox(100, grandAffichage, scrollVignettes);
        droite.setAlignment(Pos.CENTER);

        // Assembly
        HBox vue = new HBox(40, gauche, centre, droite);
        vue.setAlignment(Pos.TOP_CENTER);
        vue.setPadding(new Insets(30));
        return vue;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
