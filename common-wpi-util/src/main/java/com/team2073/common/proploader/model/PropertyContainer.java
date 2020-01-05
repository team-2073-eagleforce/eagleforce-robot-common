package com.team2073.common.proploader.model;

import com.team2073.common.proploader.PropertyLoader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a Property Container to be picked up by auto-registration with a {@link PropertyLoader}.
 * See {@link PropertyLoader#autoRegisterAllPropContainers(String)}.
 * 
 * See {@link #name()} for info on property file name resolution.
 *
 * @author Preston Briggs
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyContainer {

    /** Special value to represent null. Do not use. */
    public static final String NULL = "__NULL__";
    
    /**
     * The custom name to be used to resolve the file name of this Property Container.
     * Ex: A value of "Foo" would result in searching for a file named "Foo.properties".
     *
     * By default the class name is used, removing any trailing "Properties" in the class name.
     * Ex: A class of "ApplicationProperties" would result in a file name of "Application.properties".
     * @see PropertyContainer
     */
    String name() default NULL;

}
