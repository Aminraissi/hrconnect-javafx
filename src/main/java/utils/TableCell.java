package utils;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import models.StatutDemande;

public class TableCell {
    String text;
    protected int minWidth;

    public TableCell(String text, int minWidth) {
        this.text = text;
        this.minWidth = minWidth;
    }

    public Label build() {
        Label label = new Label(text);
        label.setMinWidth(minWidth);
        label.setMaxWidth(minWidth);
        return label;
    }

    // Nouvelle méthode pour créer un cercle coloré avec le texte du statut
    public HBox buildWithStatusCircleAndText(StatutDemande statut) {
        HBox hbox = new HBox(5); // Espacement entre le cercle et le texte
        Circle circle = new Circle(5); // Rayon du cercle

        // Définir la couleur du cercle en fonction du statut
        switch (statut) {
            case ACCEPTEE:
                circle.setFill(Color.GREEN);
                break;
            case EN_ATTENTE:
                circle.setFill(Color.YELLOW);
                break;
            case REFUSEE:
                circle.setFill(Color.RED);
                break;
            default:
                circle.setFill(Color.GRAY); // Couleur par défaut
                break;
        }

        Label label = new Label(statut.name()); // Afficher le texte du statut
        label.setMinWidth(minWidth);
        label.setMaxWidth(minWidth);

        hbox.getChildren().addAll(circle, label);
        return hbox;
    }
}