package com.team2073.common.proploader.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyContainer {

    /** Special value to represent null. Do not use. */
    static final String NULL = "__NULL__";

    String name() default NULL;

}
