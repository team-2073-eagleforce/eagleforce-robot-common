package com.team2073.common.datarecorder.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author pbriggs
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Recordable {

    public enum InclusionMode {
        INCLUDE_ALL,
        INCLUDE_ALL_FAIL_FAST,
        EXCLUDE_ALL
    }

    /** Special value to represent null. Do not use. */
    static final String NULL = "__NULL__";

    String name() default NULL;

    InclusionMode inclusionMode() default InclusionMode.INCLUDE_ALL;

}
