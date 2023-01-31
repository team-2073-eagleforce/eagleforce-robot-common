package com.team2073.KeyboardInputStatus;

import edu.wpi.first.shuffleboard.api.data.ComplexData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.Map;

/**
 * Represents a list of alerts.
 */
public final class Status extends ComplexData<Status> {

  private final String[] statuses;

  public Status(String[] statuses) {
    this.statuses = statuses;
  }

  @Override
  public Map<String, Object> asMap() {

    return Map.of("statuses", statuses);
  }

  public ObservableList<String> getCollections() {

    ObservableList<String> collections = FXCollections.observableArrayList();
    collections.addAll(Arrays.asList(statuses));

    if (collections.size() == 0) {
      collections.add("No keyboard input");
    }

    return collections;
  }

  @Override
  public String toHumanReadableString() {
    return statuses.length + " statuses";
  }

}