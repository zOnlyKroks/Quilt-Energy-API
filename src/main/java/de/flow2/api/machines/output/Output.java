package de.flow2.api.machines.output;

import de.flow2.api.machines.Typed;

import java.io.Serializable;

// TODO: Add JavaDoc
public interface Output<T extends Serializable> extends Typed<T> {
	T extractableAmount();

	void extract(T amount);
}
