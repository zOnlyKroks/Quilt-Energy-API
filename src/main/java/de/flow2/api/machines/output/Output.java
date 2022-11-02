package de.flow2.api.machines.output;

import de.flow2.api.machines.Typed;

// TODO: Add JavaDoc
public interface Output<T> extends Typed<T> {
	T extractableAmount();

	void extract(T amount);
}
