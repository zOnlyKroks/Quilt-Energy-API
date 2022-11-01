package de.flow2.api.machines.output;

import de.flow2.api.Type;

// TODO: Add JavaDoc
public abstract class AbstractOutput<T> implements Output<T> {

	private Type<T> type;

	protected AbstractOutput(Type<T> type) {
		this.type = type;
	}

	@Override
	public Type<T> type() {
		return type;
	}
}
