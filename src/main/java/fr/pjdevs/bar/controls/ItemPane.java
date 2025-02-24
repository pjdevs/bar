package fr.pjdevs.bar.controls;

import java.io.IOException;
import java.util.Optional;

import fr.pjdevs.bar.models.Item;
import fr.pjdevs.bar.models.ItemList;
import fr.pjdevs.bar.util.ChangedListenner;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Item pane component where items of the item list can be added to the cart and new items can be added.
 */
public class ItemPane extends VBox {
    /**
     * The grid where all items are displayed.
     */
    @FXML
    private GridPane itemsBox;
    /**
     * Button to add a new item.
     */
    private Button addBtn;

    /**
     * Creates an new ItemPane instance.
     */
    public ItemPane() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/ItemPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.getStyleClass().add("item-pane");

        this.addBtn = new Button();
        this.addBtn.setPrefSize(200.0, 200.0);
        this.addBtn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("images/add.png"))));
        this.addBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                addItem();
            } 
        });

        try {
            ItemList.getInstance().addListenner(new ChangedListenner(){
                @Override
                public void onChanged() {
                    update();
                }
            });
        } catch (IOException e) {
            new Alert(AlertType.ERROR, "Impossible d'ouvrir la liste des items :\n" + e.getMessage()).show();
        }

        this.update();
    }

    /**
     * Adds an item to the list by displaying an {@link ItemDialog}.
     */
    private void addItem() {
        Optional<Item> newItem = new ItemDialog(null).showAndWait();

        if (newItem.isPresent()) {
            try {
                ItemList.getInstance().add(newItem.get());
            } catch (IOException e) {
                new Alert(AlertType.ERROR, "Impossible d'ouvrir la liste des items :\n" + e.getMessage()).show();
            }
        } else {
            new Alert(AlertType.ERROR, "L'item n'a pas ete ajoute à cause de valeur incorectes").show();
        }
    }

    /**
     * Updates this component by updating the list of items, placing them on the grid and placing the add button.
     */
    private void update() {
        this.itemsBox.getChildren().clear();

        int currentRow = 0;
        int currentColumn = 0;
        int columnCount = 3;

        try {
            for (Item item : ItemList.getInstance().getList()) {
                ItemView iw = new ItemView(item);
                this.itemsBox.add(iw, currentColumn, currentRow);
                GridPane.setFillWidth(this.addBtn, true);
                GridPane.setFillHeight(this.addBtn, true);
                ++currentColumn;

                if (currentColumn > columnCount) {
                    currentColumn = 0;
                    ++currentRow;
                }
            }
        } catch (IOException e) {
            new Alert(AlertType.ERROR, "Impossible d'ourvrir la liste des items :\n" + e.getMessage()).show();
        }

        this.itemsBox.add(this.addBtn, currentColumn, currentRow);
        GridPane.setFillWidth(this.addBtn, true);
        GridPane.setFillHeight(this.addBtn, true);
    }
}
