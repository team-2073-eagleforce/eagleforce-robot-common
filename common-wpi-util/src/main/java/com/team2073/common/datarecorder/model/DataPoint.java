package com.team2073.common.datarecorder.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author pbriggs
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPoint {

    /** Special value to represent null. Do not use. */
    static final String NULL = "__NULL__";

    String name() default NULL;

}
