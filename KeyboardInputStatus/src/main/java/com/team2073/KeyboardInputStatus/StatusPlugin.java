package com.team2073.KeyboardInputStatus;

import edu.wpi.first.shuffleboard.api.data.DataType;
import edu.wpi.first.shuffleboard.api.plugin.Plugin;
import edu.wpi.first.shuffleboard.api.widget.ComponentType;
import edu.wpi.first.shuffleboard.api.plugin.Description;
import edu.wpi.first.shuffleboard.api.widget.WidgetType;

import java.util.List;
import java.util.Map;

@Description(group = "com.team2073", name = "KeyboardInputStatus", version = "1.0.0", summary = "A plugin to display status from keyboard inputs")
public final class StatusPlugin extends Plugin {
  @Override
  public List<DataType> getDataTypes() {
    return List.of(StatusDataType.instance);
  }

  @Override
  public List<ComponentType> getComponents() {
    return List.of(WidgetType.forAnnotatedWidget(StatusWidget.class));
  }

  @Override
  public Map<DataType, ComponentType> getDefaultComponents() {
    return Map.of(StatusDataType.instance, WidgetType.forAnnotatedWidget(StatusWidget.class));
  }
}
