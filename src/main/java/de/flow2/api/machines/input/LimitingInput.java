package de.flow2.api.machines.input;

// TODO: Add JavaDoc
public class LimitingInput<T> extends DelegatingInput<T> {

	private T limit;

	public LimitingInput(Input<T> delegate, T limit) {
		super(delegate);
		this.limit = limit;
	}

	@Override
	public T extractableAmount() {
		return delegate.type().available(super.extractableAmount(), limit);
	}
}
