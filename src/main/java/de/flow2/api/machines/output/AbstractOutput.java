package de.flow2.api.machines.output;

import de.flow2.api.Type;

import java.io.Serializable;

// TODO: Add JavaDoc
public abstract class AbstractOutput<T extends Serializable> implements Output<T> {

	private Type<T> type;

	protected AbstractOutput(Type<T> type) {
		this.type = type;
	}

	@Override
	public Type<T> type() {
		return type;
	}
}
