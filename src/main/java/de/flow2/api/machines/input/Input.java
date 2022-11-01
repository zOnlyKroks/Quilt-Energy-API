package de.flow2.api.machines.input;

import de.flow2.api.Networkable;
import de.flow2.api.Type;

// TODO: Add JavaDoc
public interface Input<T> extends Networkable {
	Type<T> type();

	void insert(T amount);
}
