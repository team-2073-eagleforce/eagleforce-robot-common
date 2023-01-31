package org.littletonrobotics.networkalerts;

import org.littletonrobotics.networkalerts.Alerts.AlertItem;
import org.littletonrobotics.networkalerts.Alerts.AlertType;

import edu.wpi.first.shuffleboard.api.widget.Description;
import edu.wpi.first.shuffleboard.api.widget.ParametrizedController;
import edu.wpi.first.shuffleboard.api.widget.SimpleAnnotatedWidget;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;

@Description(name = "Alerts", dataTypes = Alerts.class, summary = "Displays a list of alerts.")
@ParametrizedController("AlertsWidget.fxml")
public final class AlertsWidget extends SimpleAnnotatedWidget<Alerts> {

  private Image errorIcon = new Image(getClass().getResourceAsStream("img/error.png"));
  private Image warningIcon = new Image(getClass().getResourceAsStream("img/warning.png"));
  private Image infoIcon = new Image(getClass().getResourceAsStream("img/info.png"));

  @FXML
  private Pane root;

  @FXML
  private ListView<AlertItem> list;

  @FXML
  @SuppressWarnings("incomplete-switch")
  private void initialize() {
    list.setCellFactory(param -> new ListCell<AlertItem>() {
      @Override
      protected void updateItem(AlertItem item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setGraphic(null);
          setText(null);
        } else {
          setMinWidth(param.getWidth() - 32);
          setMaxWidth(param.getWidth() - 32);
          setPrefWidth(param.getWidth() - 32);

          setWrapText(true);
          setText(item.text);

          if (item.type == AlertType.LOCAL) {
            setTextAlignment(TextAlignment.CENTER);
            setStyle("-fx-alignment: center;");
            setGraphic(null);
          } else {
            setTextAlignment(TextAlignment.LEFT);
            setStyle("-fx-alignment: left;");

            ImageView imageView = new ImageView();
            switch (item.type) {
              case ERROR:
                imageView.setImage(errorIcon);
                break;
              case WARNING:
                imageView.setImage(warningIcon);
                break;
              case INFO:
                imageView.setImage(infoIcon);
                break;
            }
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            imageView.setTranslateX(-3);
            imageView.setSmooth(true);
            setGraphic(imageView);
          }
        }
      }
    });

    list.setSelectionModel(new NoSelectionModel<AlertItem>());
    list.itemsProperty().bind(dataOrDefault.map(Alerts::getCollection));
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
    public void selectIndices(int index, int... indices) {
    }

    @Override
    public void selectAll() {
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
