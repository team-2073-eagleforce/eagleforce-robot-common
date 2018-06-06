package com.team2073.common.inject;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
@Documented
public @interface InjectNamed {
	/**
	 * Return the name of the binding to be injected.
	 * <p>
	 * Return an empty string to use the name of the annotated field. 
	 */
	String value() default "";
}
