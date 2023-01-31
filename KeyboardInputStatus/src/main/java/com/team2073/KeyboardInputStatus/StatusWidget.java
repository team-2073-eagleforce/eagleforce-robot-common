package com.team2073.KeyboardInputStatus;

import edu.wpi.first.shuffleboard.api.widget.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

@Description(name = "Status", dataTypes = Status.class, summary = "Status of keyboard inputted commands")
@ParametrizedController("StatusWidget.fxml")
public final class StatusWidget extends SimpleAnnotatedWidget<Status> {

  @FXML
  private Pane root;

  @FXML
  private ListView<String> list;

  @FXML
  @SuppressWarnings("incomplete-switch")
  private void initialize() {
    list.setCellFactory(param -> new ListCell<String>() {
      @Override
      public void updateItem(String message, boolean empty) {
        super.updateItem(message, empty);
        if (empty || message == null) {
          setText(null);
        } else {
          setMinWidth(param.getWidth() - 32);
          setMaxWidth(param.getWidth() - 32);
          setPrefWidth(param.getWidth() - 32);

          setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                  BorderWidths.DEFAULT)));


          setTextAlignment(TextAlignment.LEFT);
          setStyle("-fx-alignment: left;");

          setWrapText(true);
          setText(message);

        }
      }
    });
//
    list.setSelectionModel(new NoSelectionModel<>());
    list.itemsProperty().bind(dataOrDefault.map(Status::getCollections));
  }

  @Override
  public Pane getView() {
    return root;
  }

  public class NoSelectionModel<T> extends MultipleSelectionModel<T> {

    @Override
    public ObservableList<Integer> getSelectedIndices() {
      return FXCollections.emptyObservableList();
    }

    @Override
    public ObservableList<T> getSelectedItems() {
      return FXCollections.emptyObservableList();
    }

    @Override
    public void selectAll() {
    }

    @Override
    public void selectIndices(int index, int... indices) {
    }

    @Override
    public void selectFirst() {

    }

    @Override
    public void selectLast() {

    }

    @Override
    public void clearAndSelect(int index) {

    }

    @Override
    public void select(int index) {

    }

    @Override
    public void select(T obj) {

    }

    @Override
    public void clearSelection(int index) {

    }

    @Override
    public void clearSelection() {

    }

    @Override
    public boolean isSelected(int index) {
      return false;
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public void selectPrevious() {

    }

    @Override
    public void selectNext() {

    }
  }
}
