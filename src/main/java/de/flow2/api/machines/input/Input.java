package de.flow2.api.machines.input;

import de.flow2.api.machines.Typed;

import java.io.Serializable;

// TODO: Add JavaDoc
public interface Input<T extends Serializable> extends Typed<T> {
	void insert(T amount);
}
