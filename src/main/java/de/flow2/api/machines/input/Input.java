package de.flow2.api.machines.input;

import de.flow2.api.machines.Typed;

// TODO: Add JavaDoc
public interface Input<T> extends Typed<T> {
	void insert(T amount);
}
