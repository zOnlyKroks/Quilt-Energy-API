package de.flow.api;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface NetworkBlock {

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

		public LimitedDefaultInput(DefaultInput<T, C> input, T limit) {
			super(input.supply, input.consume, input.unit);
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

	interface Store<T, C> extends Input<T, C>, Output<T, C> {
	}

	class DefaultStore<T, C> implements Store<T, C> {

		protected Input<T, C> input;
		protected Output<T, C> output;
		protected Unit<T, C> unit;

		public DefaultStore(Input<T, C> input, Output<T, C> output) {
			this.input = input;
			this.output = output;
			if (input.unit() != output.unit()) {
				throw new IllegalArgumentException("Input and output unit must be the same");
			}
			this.unit = input.unit();
		}

		@Override
		public T extractableAmount() {
			return input.extractableAmount();
		}

		@Override
		public void extract(T amount) {
			input.extract(amount);
		}

		@Override
		public T desiredAmount() {
			return output.desiredAmount();
		}

		@Override
		public void provide(T amount) {
			output.provide(amount);
		}

		@Override
		public Unit<T, C> unit() {
			return unit;
		}
	}
}
