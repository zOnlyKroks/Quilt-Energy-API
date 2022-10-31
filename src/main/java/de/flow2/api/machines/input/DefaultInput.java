package de.flow2.api.machines.input;

import de.flow2.api.Type;

import java.util.function.Consumer;
import java.util.function.Supplier;

// TODO: Add JavaDoc
public class DefaultInput<T> extends AbstractInput<T> {

	private Supplier<T> desired;
	private Consumer<T> provided;

	public DefaultInput(Type<T> type, Supplier<T> desired, Consumer<T> provided) {
		super(type);
		this.desired = desired;
		this.provided = provided;
	}

	@Override
	public T extractableAmount() {
		return desired.get();
	}

	@Override
	public void extract(T amount) {
		provided.accept(amount);
	}
}
