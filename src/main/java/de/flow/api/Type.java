package de.flow.api;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.Getter;

public abstract class Type<T, C> {

	@Getter
	private final Class<T> clazz;

	public Type(Class<T> clazz) {
		this.clazz = clazz;
	}

	public abstract C container();

	public abstract void add(C container, T element);

	public abstract void subtract(C container, T element);

	public abstract boolean containsAll(C container, C shouldContain);

	public static class NumberType extends Type<Double, AtomicDouble> {
		public NumberType() {
			super(Double.class);
		}

		@Override
		public AtomicDouble container() {
			return new AtomicDouble();
		}

		@Override
		public void add(AtomicDouble container, Double element) {
			container.addAndGet(element);
		}

		@Override
		public void subtract(AtomicDouble container, Double element) {
			container.addAndGet(-element);
		}

		@Override
		public boolean containsAll(AtomicDouble container, AtomicDouble shouldContain) {
			return container.get() >= shouldContain.get();
		}
	}
}
