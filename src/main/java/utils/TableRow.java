package utils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import utils.enums.TableControlBtn;
import utils.enums.TableRowType;

import java.util.ArrayList;
import java.util.List;

public class TableRow {
    TableRowType type;
    String classe;
    List<TableCell> cells;
    List<HBox> actions;

    public TableRow(TableRowType type) {
        this.type = type;
        this.cells = new ArrayList();
        this.actions = new ArrayList<>();
    }

    public void addCell(TableCell row) {
        cells.add(row);
    }

    public HBox build() {
        HBox hBox = new HBox();
        hBox.getStyleClass().add(type == TableRowType.HEADER ? "table-header" : "table-body");
        for (TableCell cell : cells) {
            Label lb = cell.build();
            if (type == TableRowType.HEADER) {
                lb.getStyleClass().add("table-header-label");
            } else {
                lb.getStyleClass().add("table-body-label");
            }
            hBox.getChildren().add(lb);
        }
        if (type == TableRowType.BODY) {
            actions.forEach(action -> {
                hBox.getChildren().add(action);
            });
        }
        return hBox;
    }

    public void addAction(String iconName, String classe, Runnable action) {
        HBox container = new HBox();
        container.getStyleClass().add(classe);

        FontAwesomeIconView icon = new FontAwesomeIconView();
        icon.setGlyphName(iconName);
        icon.setSize("20");
        icon.setFill(javafx.scene.paint.Color.WHITE);

        icon.setOnMouseClicked(event -> {
            action.run();
        });
        container.getChildren().add(icon);
        actions.add(container);
    }
}