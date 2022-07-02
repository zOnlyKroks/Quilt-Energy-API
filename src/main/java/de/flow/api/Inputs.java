package de.flow.api;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Inputs extends NetworkableCollection {

	interface Input<T, C> extends Unitable<T, C>, Networkable<T, C> {
		T extractableAmount();

		void extract(T amount);
	}

	class DefaultInput<T, C> implements Input<T, C> {

		protected Supplier<T> supply;
		protected Consumer<T> consume;
		protected Unit<T, C> unit;

		public DefaultInput(Supplier<T> supply, Consumer<T> consume, Unit<T, C> unit) {
			this.supply = supply;
			this.consume = consume;
			this.unit = unit;
		}

		@Override
		public T extractableAmount() {
			return supply.get();
		}

		@Override
		public void extract(T amount) {
			consume.accept(amount);
		}

		@Override
		public Unit<T, C> unit() {
			return unit;
		}
	}

	class LimitedDefaultInput<T, C> extends DefaultInput<T, C> {

		private T limit;

		public LimitedDefaultInput(DefaultInput<T, C> output, T limit) {
			super(output.supply, output.consume, output.unit);
			this.limit = limit;
		}

		public LimitedDefaultInput(Supplier<T> supply, Consumer<T> consume, Unit<T, C> unit, T limit) {
			super(supply, consume, unit);
			this.limit = limit;
		}

		@Override
		public T extractableAmount() {
			return unit.type().minValue(super.extractableAmount(), limit);
		}
	}
}
