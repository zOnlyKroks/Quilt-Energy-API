package de.flow.api;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.experimental.UtilityClass;

import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class Utils {

	public static final Type<AtomicDouble> ENERGY_TYPE = new Type.NumberType();

	public static final Type<AtomicInteger> REDSTONE_TYPE = new Type<>() {
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
	};
}
