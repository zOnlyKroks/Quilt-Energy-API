package de.flow2.api.machines.input;

import java.util.function.BooleanSupplier;

// TODO: Add JavaDoc
public class LockableInput<T> extends DelegatingInput<T> {

	private BooleanSupplier lock;

	public LockableInput(Input<T> delegate, BooleanSupplier lock) {
		super(delegate);
		this.lock = lock;
	}

	@Override
	public T extractableAmount() {
		return lock.getAsBoolean() ? super.extractableAmount() : type().defaultValue();
	}
}
