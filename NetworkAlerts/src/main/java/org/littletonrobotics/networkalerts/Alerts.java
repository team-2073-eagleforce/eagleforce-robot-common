package org.littletonrobotics.networkalerts;

import edu.wpi.first.shuffleboard.api.data.ComplexData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Map;

/**
 * Represents a list of alerts.
 */
public final class Alerts extends ComplexData<Alerts> {

  private final String[] errors;
  private final String[] warnings;
  private final String[] infos;

  /**
   * Creates a new NetworkAlerts object.
   *
   * @param errors   List of active error alerts
   * @param warnings List of active warning alerts
   * @param infos    List of active info alerts
   */
  public Alerts(String[] errors, String[] warnings, String[] infos) {
    this.errors = errors;
    this.warnings = warnings;
    this.infos = infos;
  }

  /**
   * Gets the list of error alerts.
   */
  public String[] getErrors() {
    return errors;
  }

  /**
   * Gets the list of warning alerts.
   */
  public String[] getWarnings() {
    return warnings;
  }

  /**
   * Gets the list of info alerts.
   */
  public String[] getInfos() {
    return infos;
  }

  /**
   * Gets a collection of all alerts.
   */
  public ObservableList<AlertItem> getCollection() {
    ObservableList<AlertItem> collection = FXCollections.observableArrayList();
    for (String text : errors) {
      collection.add(new AlertItem(AlertType.ERROR, text));
    }
    for (String text : warnings) {
      collection.add(new AlertItem(AlertType.WARNING, text));
    }
    for (String text : infos) {
      collection.add(new AlertItem(AlertType.INFO, text));
    }
    if (collection.size() == 0) {
      collection.add(new AlertItem(AlertType.LOCAL, "(Nothing to report)"));
    }
    return collection;
  }

  @Override
  public String toHumanReadableString() {
    return Integer.toString(errors.length) + " error(s), " + Integer.toString(warnings.length) + " warning(s), "
        + Integer.toString(infos.length) + " info(s)";
  }

  @Override
  public Map<String, Object> asMap() {
    return Map.of("errors", errors, "warnings", warnings, "infos", infos);
  }

  public static class AlertItem {
    public final AlertType type;
    public final String text;

    public AlertItem(AlertType type, String text) {
      this.type = type;
      this.text = text;
    }
  }

  public static enum AlertType {
    ERROR, WARNING, INFO, LOCAL
  }
}
