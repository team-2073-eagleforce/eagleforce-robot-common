package com.team2073.common.inject;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
@Documented
public @interface InjectNamed {
	/**
	 * Return the name of the binding to be injected.
	 * <p>
	 * return an empty string to use the name of the annotated field.
	 */
	String value() default "";
}
