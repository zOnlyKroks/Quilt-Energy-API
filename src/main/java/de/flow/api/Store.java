package de.flow.api;

public interface Store<T, C> extends Inputs.Input<T, C>, Outputs.Output<T, C> {

	class DefaultStore<T, C> implements Store<T, C> {

		protected Inputs.Input<T, C> input;
		protected Outputs.Output<T, C> output;
		protected Unit<T, C> unit;

		public DefaultStore(Inputs.Input<T, C> input, Outputs.Output<T, C> output) {
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
