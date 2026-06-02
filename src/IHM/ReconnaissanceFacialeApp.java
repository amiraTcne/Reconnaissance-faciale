package IHM;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class ReconnaissanceFacialeApp extends Application {

    private CtrlImport ctrlImport;
    private BorderPane root;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Contrôleur
        ctrlImport = new CtrlImport(primaryStage);

        // Présentation
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

    /** Bandeau vert en haut. */
    private HBox creerHeader() {
        Label titre = new Label("👥  Reconnaissance Faciale");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
        HBox header = new HBox(titre);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #C8E6C9;");
        return header;
    }

    /** Vue ACCUEIL : description fusionnée au header + zone d'import en dessous. */
    private VBox creerVueAccueil() {
        // Description : même vert que le header, pleine largeur, pas d'espace au-dessus
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

        // Zone d'import
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

    public static void main(String[] args) {
        launch(args);
    }
}
