package com.fisherevans.eventRouter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Register a method to a set of action IDs
 *
 * Author: Fisher Evans
 * Date: 2/5/14
 */
@Retention(RUNTIME)
@Target(value={METHOD})
public @interface StaticEventAction {
    long value();
}
