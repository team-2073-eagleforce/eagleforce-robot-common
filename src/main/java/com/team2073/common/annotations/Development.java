package com.team2073.common.annotations;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * Marks methods and fields that are only used during development.
 * @author CTRL-SHIFT-F
 *
 */
@Retention(SOURCE)
@Target({ TYPE, FIELD, METHOD, CONSTRUCTOR })
public @interface Development {

}
