package de.flow2.api.machines.input;

import de.flow2.api.Type;

import java.util.function.Consumer;

// TODO: Add JavaDoc
public class DefaultInput<T> extends AbstractInput<T> {

	private Consumer<T> consumer;

	public DefaultInput(Type<T> type, Consumer<T> consumer) {
		super(type);
		this.consumer = consumer;
	}

	@Override
	public void insert(T amount) {
		consumer.accept(amount);
	}
}
