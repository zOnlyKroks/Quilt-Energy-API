package de.flow2.api.machines.output;

// TODO: Add JavaDoc
public class DelegatingOutput<T> extends AbstractOutput<T> {

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
