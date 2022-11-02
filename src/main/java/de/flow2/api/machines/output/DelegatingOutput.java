package de.flow2.api.machines.output;

import java.io.Serializable;

// TODO: Add JavaDoc
public class DelegatingOutput<T extends Serializable> extends AbstractOutput<T> {

	protected Output<T> delegate;

	public DelegatingOutput(Output<T> delegate) {
		super(delegate.type());
		this.delegate = delegate;
	}

	@Override
	public T extractableAmount() {
		return delegate.extractableAmount();
	}

	@Override
	public void extract(T amount) {
		delegate.extract(amount);
	}
}
