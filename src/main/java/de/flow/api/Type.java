package de.flow.api;

import com.google.common.util.concurrent.AtomicDouble;

public interface Type<T, C> {

	C container();

	void add(C container, T element);

	void subtract(C container, T element);

	boolean containsAll(C container, C shouldContain);

	T available(C container, T needed);

	boolean isEmpty(C container);

	C min(C c1, C c2);

	C copy(C container);

	class NumberType implements Type<Double, AtomicDouble> {

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

		@Override
		public Double available(AtomicDouble container, Double needed) {
			if (container.get() == 0) return null;
			if (container.get() > needed) {
				return needed;
			} else {
				return container.get();
			}
		}

		@Override
		public boolean isEmpty(AtomicDouble container) {
			return container.get() == 0;
		}

		@Override
		public AtomicDouble min(AtomicDouble c1, AtomicDouble c2) {
			return c1.get() < c2.get() ? c1 : c2;
		}

		@Override
		public AtomicDouble copy(AtomicDouble container) {
			return new AtomicDouble(container.get());
		}
	}
}
