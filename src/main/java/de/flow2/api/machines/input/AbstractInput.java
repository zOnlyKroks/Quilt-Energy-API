package de.flow2.api.machines.input;

import de.flow2.api.Type;

import java.io.Serializable;

// TODO: Add JavaDoc
public abstract class AbstractInput<T extends Serializable> implements Input<T> {

	private Type<T> type;

	protected AbstractInput(Type<T> type) {
		this.type = type;
	}

	@Override
	public Type<T> type() {
		return type;
	}
}
