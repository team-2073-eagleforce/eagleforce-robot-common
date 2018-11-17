package com.team2073.common.datarecorder.model;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * @author Preston Briggs
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface DataPointIgnore {
}
