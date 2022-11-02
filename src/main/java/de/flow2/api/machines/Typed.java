package de.flow2.api.machines;

import de.flow2.api.Type;

import java.io.Serializable;

// TODO: Add JavaDoc
public interface Typed<T extends Serializable> {
	Type<T> type();
}
