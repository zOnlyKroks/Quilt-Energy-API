package de.flow2.api.machines.input;

import de.flow2.api.Type;

// TODO: Add JavaDoc
public abstract class AbstractInput<T> implements Input<T> {

	private Type<T> type;

	public AbstractInput(Type<T> type) {
		this.type = type;
	}

	@Override
	public Type<T> type() {
		return type;
	}
}
