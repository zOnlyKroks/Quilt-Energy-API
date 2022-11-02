package de.flow2.api.machines.output;

import de.flow2.api.Type;

import java.util.function.Consumer;
import java.util.function.Supplier;

// TODO: Add JavaDoc
public class DefaultOutput<T> extends AbstractOutput<T> {

	private Supplier<T> desired;
	private Consumer<T> provided;

	public DefaultOutput(Type<T> type, Supplier<T> desired, Consumer<T> provided) {
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
