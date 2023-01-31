package com.team2073.KeyboardInputStatus;

import edu.wpi.first.shuffleboard.api.data.ComplexDataType;

import java.util.Map;
import java.util.function.Function;

public final class StatusDataType extends ComplexDataType<Status> {

  private static final String TYPE_NAME = "Status";

  public static final StatusDataType instance = new StatusDataType();

  Status status;
  private StatusDataType() {
    super(TYPE_NAME, Status.class);
  }

  @Override
  public Function<Map<String, Object>, Status> fromMap() {
    return map ->  status = new Status((String[]) map.getOrDefault("statuses", new String[] {}));
  }

  @Override
  public Status getDefaultValue() {
    return new Status(new String[]{});
  }
}
