package de.flow2.api.machines.output;

import de.flow2.api.Type;

import java.util.function.Consumer;

// TODO: Add JavaDoc
public class DefaultOutput<T> extends AbstractOutput<T> {

	private Consumer<T> consumer;

	public DefaultOutput(Type<T> type, Consumer<T> consumer) {
		super(type);
		this.consumer = consumer;
	}

	@Override
	public void insert(T amount) {
		consumer.accept(amount);
	}
}
