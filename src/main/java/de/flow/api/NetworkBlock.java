package de.flow.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface NetworkBlock {

	default Direction[] ports() {
		return Direction.values();
	}
	BlockPos getPos();
	World getWorld();

	default boolean hasType(Type<?> type) {
		for (Field field : this.getClass().getDeclaredFields()) {
			if (!field.isAnnotationPresent(RegisterToNetwork.class)) continue;
			try {
				field.setAccessible(true);
				Unitable<?> unitable = (Unitable<?>) field.get(this);
				if (unitable.unit().type() == type) return true;
			} catch (Exception e) {
				// ignore
			}
		}
		return false;
	}

	interface Input<C> extends Unitable<C>, Networkable<C> {
		C extractableAmount();

		void extract(C amount);
	}

	class DefaultInput<C> implements Input<C> {

		protected Supplier<C> supply;
		protected Consumer<C> consume;
		protected Unit<C> unit;

		public DefaultInput(Supplier<C> supply, Consumer<C> consume, Unit<C> unit) {
			this.supply = supply;
			this.consume = consume;
			this.unit = unit;
		}

		@Override
		public C extractableAmount() {
			return supply.get();
		}

		@Override
		public void extract(C amount) {
			consume.accept(amount);
		}

		@Override
		public Unit<C> unit() {
			return unit;
		}
	}

	class LimitedDefaultInput<C> extends DefaultInput<C> {

		private C limit;

		public LimitedDefaultInput(DefaultInput<C> input, C limit) {
			super(input.supply, input.consume, input.unit);
			this.limit = limit;
		}

		public LimitedDefaultInput(Supplier<C> supply, Consumer<C> consume, Unit<C> unit, C limit) {
			super(supply, consume, unit);
			this.limit = limit;
		}

		@Override
		public C extractableAmount() {
			return unit.type().min(super.extractableAmount(), limit);
		}
	}

	interface Output<C> extends Unitable<C>, Networkable<C> {
		C desiredAmount();
		void provide(C amount);
	}

	class DefaultOutput<C> implements Output<C> {
		protected Supplier<C> desired;
		protected Consumer<C> provided;
		protected Unit<C> unit;

		public DefaultOutput(Supplier<C> desired, Consumer<C> provided, Unit<C> unit) {
			this.desired = desired;
			this.provided = provided;
			this.unit = unit;
		}

		@Override
		public C desiredAmount() {
			return desired.get();
		}

		@Override
		public void provide(C amount) {
			provided.accept(amount);
		}

		@Override
		public Unit<C> unit() {
			return unit;
		}
	}

	class LimitedDefaultOutput<C> extends DefaultOutput<C> {

		private C limit;

		public LimitedDefaultOutput(DefaultOutput<C> output, C limit) {
			super(output.desired, output.provided, output.unit);
			this.limit = limit;
		}

		public LimitedDefaultOutput(Supplier<C> desired, Consumer<C> provided, Unit<C> unit, C limit) {
			super(desired, provided, unit);
			this.limit = limit;
		}

		@Override
		public C desiredAmount() {
			return unit.type().min(super.desiredAmount(), limit);
		}
	}

	interface Store<C> extends Input<C>, Output<C> {
	}

	class DefaultStore<C> implements Store<C> {

		protected Input<C> input;
		protected Output<C> output;
		protected Unit<C> unit;

		public DefaultStore(Input<C> input, Output<C> output) {
			this.input = input;
			this.output = output;
			if (input.unit() != output.unit()) {
				throw new IllegalArgumentException("Input and output unit must be the same");
			}
			this.unit = input.unit();
		}

		@Override
		public C extractableAmount() {
			return input.extractableAmount();
		}

		@Override
		public void extract(C amount) {
			input.extract(amount);
		}

		@Override
		public C desiredAmount() {
			return output.desiredAmount();
		}

		@Override
		public void provide(C amount) {
			output.provide(amount);
		}

		@Override
		public Unit<C> unit() {
			return unit;
		}
	}
}
