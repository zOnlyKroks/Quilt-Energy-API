package de.flow.api;

import java.util.concurrent.atomic.AtomicInteger;

class RedstoneType implements Type<AtomicInteger> {

	@Override
	public AtomicInteger container() {
		return new AtomicInteger();
	}

	@Override
	public void add(AtomicInteger container, AtomicInteger element) {
		container.set(Math.max(container.get(), element.get()));
	}

	@Override
	public void subtract(AtomicInteger container, AtomicInteger element) {

	}

	@Override
	public boolean containsAll(AtomicInteger container, AtomicInteger shouldContain) {
		if (container.get() == 0) return false;
		return true;
	}

	@Override
	public AtomicInteger available(AtomicInteger container, AtomicInteger needed) {
		return new AtomicInteger(container.get());
	}

	@Override
	public boolean isEmpty(AtomicInteger container) {
		return container.get() == 0;
	}

	@Override
	public AtomicInteger min(AtomicInteger c1, AtomicInteger c2) {
		return c1;
	}

	@Override
	public AtomicInteger copy(AtomicInteger container) {
		return new AtomicInteger(container.get());
	}
}
