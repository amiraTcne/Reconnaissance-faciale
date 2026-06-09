package IHM;

import java.io.File;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import laBdd.Bdd;
import laBdd.Personne;
import calculs.ACP;
import calculs.Retour;
import imgs.ImageVisage;
public class VueCorrespondanceTrouvee extends VBox{
    Bdd bdd = new Bdd(); 
    ACP acp = new ACP(bdd.createA());
	public VueCorrespondanceTrouvee(File imageSelectionnee,Retour[] tableauPourcentage) {
		this.setSpacing(20);
        this.setAlignment(Pos.CENTER);
        HBox troisColonnes = creerLaVue(imageSelectionnee, tableauPourcentage);
        this.getChildren().add(troisColonnes);
		 //Force le HBox à occuper tout l'espace vertical disponible dans la VBox
        VBox.setVgrow(troisColonnes, javafx.scene.layout.Priority.ALWAYS);
		
	}
	private HBox creerLaVue(File imageSelectionnee, Retour[] tableauPourcentage) {
		float meilleurPourcentage = tableauPourcentage[0].getPourcentage();
		int idimg= tableauPourcentage[0].getIndice();
		String imgtrouvee = bdd.getImg(idimg).path;
		Personne personneGagnante = bdd.rechercher(idimg);
		
        // Left : selected image + "no match" message
        ImageView preview = new ImageView(new Image(imageSelectionnee.toURI().toString()));
        ImageVisage nouvelleIm = new ImageVisage(0, imageSelectionnee.getPath());
        preview.setFitWidth(220);
        preview.setFitHeight(220);
        preview.setPreserveRatio(true);
        //bloc gauche sous-titres
        Label legende = new Label("Image sélectionnée");
        legende.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
        Label message = new Label("✅ Correspondance trouvée !");
        message.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");
        message.setWrapText(true);
        message.setTextAlignment(TextAlignment.CENTER);
        message.setMaxWidth(220);
        VBox gauche = new VBox(15, preview, legende,message);
        gauche.setAlignment(Pos.CENTER);
        //bloc centre 
        Label labelComparaison = new Label("👤 ⟷ 👤");
        labelComparaison.setStyle("-fx-font-size: 36px; -fx-text-fill: black;");
        Label lblTitre = new Label("Correspondance ");
        lblTitre.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");
        VBox zoneImport = new VBox(10, labelComparaison, lblTitre);
        zoneImport.setAlignment(Pos.CENTER);
        zoneImport.setPadding(new Insets(30));
        zoneImport.setStyle("-fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-color: white;");
        VBox centre = new VBox(zoneImport);
        centre.setAlignment(Pos.CENTER);
     // --- Right : nav buttons + large display + thumbnail strip (15 closest) ---
        ImageView imageComparee = new ImageView();
        imageComparee.setFitWidth(180);
        imageComparee.setFitHeight(180);
        imageComparee.setPreserveRatio(true);

        Label nomPersonne = new Label();
        nomPersonne.setStyle("-fx-font-weight: bold; -fx-text-fill: #2e7d32;");

        Label pourcentage = new Label();
        pourcentage.setStyle("-fx-text-fill: gray;");
        
        HBox bandeAutresImages = new HBox(15);
        bandeAutresImages.setAlignment(Pos.CENTER);

        Button precedent = new Button("Précédent");
        Button suivant = new Button("Suivant");
        HBox navigation = new HBox(10, precedent, suivant);
        navigation.setAlignment(Pos.CENTER);

        VBox grandAffichage = new VBox(12, navigation, imageComparee, nomPersonne, pourcentage, bandeAutresImages);
        grandAffichage.setAlignment(Pos.CENTER);

        // Number of thumbnails shown
        int nbVignettes = tableauPourcentage.length;

        // Column of thumbnails; the selected one gets a green border.
        VBox colonneVignettes = new VBox(8);
        colonneVignettes.setAlignment(Pos.TOP_CENTER);
        colonneVignettes.setPadding(new Insets(5));

        Button[] vignettes = new Button[nbVignettes];
        // Index of the currently displayed image (mutable via a 1-cell array).
        int[] courant = {0};

        // Updates the large display, the percentage, the green border and the nav buttons.
        Runnable maj = () -> {
            Retour r = tableauPourcentage[courant[0]];
            ImageVisage img = bdd.getImg(r.getIndice());
            Personne p = bdd.rechercher(r.getIndice());
            imageComparee.setImage(new Image(new File(img.getPath()).toURI().toString()));
            nomPersonne.setText(p.getPrenom() + " " + p.getNom());
            pourcentage.setText(String.format("Correspondance de %.0f%%", r.getPourcentage()));
            bandeAutresImages.getChildren().clear();

            for (ImageVisage autreImg : bdd.imagesBdd.get(p)) {
                if (autreImg.id != img.id) {
                    ImageView mini = new ImageView(new Image(new File(autreImg.getPath()).toURI().toString()));
                    mini.setFitWidth(65);
                    mini.setFitHeight(65);
                    mini.setPreserveRatio(true);

                    float pMini = acp.pourcentageImage(nouvelleIm, autreImg.id);

                    Label labelMini = new Label(String.format("%.0f %%", pMini));
                    labelMini.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");

                    VBox blocMini = new VBox(5, mini, labelMini);
                    blocMini.setAlignment(Pos.CENTER);

                    bandeAutresImages.getChildren().add(blocMini);
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
            Retour r = tableauPourcentage[i];
            ImageVisage img = bdd.getImg(r.getIndice());
            ImageView icone = new ImageView(new Image(new File(img.getPath()).toURI().toString()));
            icone.setFitWidth(60);
            icone.setFitHeight(60);
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
        scrollVignettes.setPrefWidth(90);
        scrollVignettes.setPrefViewportHeight(300);

        // select the closest match by default
        maj.run();

        HBox droite = new HBox(15, grandAffichage, scrollVignettes);
        droite.setAlignment(Pos.CENTER);

        // Assembly
        HBox vue = new HBox(40, gauche, centre, droite);
        vue.setAlignment(Pos.CENTER);
        vue.setPadding(new Insets(30));
        return vue;
    }
}
