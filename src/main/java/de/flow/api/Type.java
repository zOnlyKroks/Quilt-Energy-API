package de.flow.api;

import com.google.common.util.concurrent.AtomicDouble;

public interface Type<C> {

	C container();

	void add(C container, C element);

	void subtract(C container, C element);

	boolean containsAll(C container, C shouldContain);

	C available(C container, C needed);

	boolean isEmpty(C container);

	C min(C c1, C c2);

	C copy(C container);

	class NumberType implements Type<AtomicDouble> {

		@Override
		public AtomicDouble container() {
			return new AtomicDouble();
		}

		@Override
		public void add(AtomicDouble container, AtomicDouble element) {
			container.addAndGet(element.get());
		}

		@Override
		public void subtract(AtomicDouble container, AtomicDouble element) {
			container.addAndGet(-element.get());
		}

		@Override
		public boolean containsAll(AtomicDouble container, AtomicDouble shouldContain) {
			return container.get() >= shouldContain.get();
		}

		@Override
		public AtomicDouble available(AtomicDouble container, AtomicDouble needed) {
			if (container.get() <= 0) return null;
			if (container.get() > needed.get()) {
				return new AtomicDouble(needed.get());
			} else {
				return new AtomicDouble(container.get());
			}
		}

		@Override
		public boolean isEmpty(AtomicDouble container) {
			return container.get() <= 0;
		}

		@Override
		public AtomicDouble min(AtomicDouble c1, AtomicDouble c2) {
			return new AtomicDouble(Math.min(c1.get(), c2.get()));
		}

		@Override
		public AtomicDouble copy(AtomicDouble container) {
			return new AtomicDouble(container.get());
		}
	}
}
