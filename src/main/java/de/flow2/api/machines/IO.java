package de.flow2.api.machines;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO: Add JavaDoc
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IO {
}
