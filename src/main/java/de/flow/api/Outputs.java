package de.flow.api;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Outputs extends NetworkableCollection {

	interface Output<T, C> extends Unitable<T, C>, Networkable<T, C> {
		T desiredAmount();
		void provide(T amount);
	}

	class DefaultOutput<T, C> implements Output<T, C> {
		protected Supplier<T> desired;
		protected Consumer<T> provided;
		protected Unit<T, C> unit;

		public DefaultOutput(Supplier<T> desired, Consumer<T> provided, Unit<T, C> unit) {
			this.desired = desired;
			this.provided = provided;
			this.unit = unit;
		}

		@Override
		public T desiredAmount() {
			return desired.get();
		}

		@Override
		public void provide(T amount) {
			provided.accept(amount);
		}

		@Override
		public Unit<T, C> unit() {
			return unit;
		}
	}

	class LimitedDefaultOutput<T, C> extends DefaultOutput<T, C> {

		private T limit;

		public LimitedDefaultOutput(DefaultOutput<T, C> output, T limit) {
			super(output.desired, output.provided, output.unit);
			this.limit = limit;
		}

		public LimitedDefaultOutput(Supplier<T> desired, Consumer<T> provided, Unit<T, C> unit, T limit) {
			super(desired, provided, unit);
			this.limit = limit;
		}

		@Override
		public T desiredAmount() {
			return unit.type().minValue(super.desiredAmount(), limit);
		}
	}
}
