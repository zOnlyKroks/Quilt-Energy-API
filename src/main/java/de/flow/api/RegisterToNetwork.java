package de.flow.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a field with type {@link de.flow.api.NetworkBlock.Input},
 * {@link de.flow.api.NetworkBlock.Output}, {@link de.flow.api.NetworkBlock.Store}, or
 * {@link de.flow.api.NetworkBlock.Transmitter} from a {@link NetworkBlock} to be used by a {@link Network}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegisterToNetwork {
}
