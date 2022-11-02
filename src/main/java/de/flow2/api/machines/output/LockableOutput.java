package de.flow2.api.machines.output;

import java.util.function.BooleanSupplier;

// TODO: Add JavaDoc
public class LockableOutput<T> extends DelegatingOutput<T> {

	private BooleanSupplier lock;

	public LockableOutput(Output<T> delegate, BooleanSupplier lock) {
		super(delegate);
		this.lock = lock;
	}

	@Override
	public T extractableAmount() {
		return lock.getAsBoolean() ? super.extractableAmount() : type().defaultValue();
	}
}
