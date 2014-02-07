package com.fisherevans.eventRouter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Register a method to an action id
 *
 * Author: Fisher Evans
 * Date: 2/7/14
 */
@Retention(RUNTIME)
@Target(value={METHOD})
public @interface EventActions {
    long[] value();
}
