package de.flow2.api.machines.output;

import java.io.Serializable;

// TODO: Add JavaDoc
public class LimitingOutput<T extends Serializable> extends DelegatingOutput<T> {

	private T limit;

	public LimitingOutput(Output<T> delegate, T limit) {
		super(delegate);
		this.limit = limit;
	}

	@Override
	public T extractableAmount() {
		return delegate.type().available(super.extractableAmount(), limit);
	}
}
