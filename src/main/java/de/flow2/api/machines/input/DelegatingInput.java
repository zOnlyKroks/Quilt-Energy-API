package de.flow2.api.machines.input;

// TODO: Add JavaDoc
public class DelegatingInput<T> extends AbstractInput<T> {

	protected Input<T> delegate;

	public DelegatingInput(Input<T> delegate) {
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
