package de.flow.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.function.BooleanSupplier;
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

	class LimitedInput<C> implements Input<C> {

		private Input<C> delegate;
		private C limit;

		public LimitedInput(Input<C> input, C limit) {
			this.delegate = input;
			this.limit = limit;
		}

		public LimitedInput(Supplier<C> supply, Consumer<C> consume, Unit<C> unit, C limit) {
			this(new DefaultInput<>(supply, consume, unit), limit);
		}

		@Override
		public C extractableAmount() {
			return delegate.unit().type().min(delegate.extractableAmount(), limit);
		}

		@Override
		public void extract(C amount) {
			delegate.extract(amount);
		}

		@Override
		public Unit<C> unit() {
			return delegate.unit();
		}
	}

	class LockableInput<C> implements Input<C> {

		private Input<C> delegate;
		private BooleanSupplier lock;

		public LockableInput(Input<C> input, BooleanSupplier lock) {
			this.delegate = input;
			this.lock = lock;
		}

		@Override
		public C extractableAmount() {
			if (lock.getAsBoolean()) return delegate.unit().type().container();
			return delegate.extractableAmount();
		}

		@Override
		public void extract(C amount) {
			delegate.extract(amount);
		}

		@Override
		public Unit<C> unit() {
			return delegate.unit();
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

	class LimitedOutput<C> implements Output<C> {

		private Output<C> delegate;
		private C limit;

		public LimitedOutput(Output<C> output, C limit) {
			this.delegate = output;
			this.limit = limit;
		}

		public LimitedOutput(Supplier<C> desired, Consumer<C> provided, Unit<C> unit, C limit) {
			this(new DefaultOutput<>(desired, provided, unit), limit);
		}

		@Override
		public C desiredAmount() {
			return delegate.unit().type().min(delegate.desiredAmount(), limit);
		}

		@Override
		public void provide(C amount) {
			delegate.provide(amount);
		}

		@Override
		public Unit<C> unit() {
			return delegate.unit();
		}
	}

	class LockableOutput<C> implements Output<C> {

		private Output<C> delegate;
		private BooleanSupplier lock;

		public LockableOutput(Output<C> output, BooleanSupplier lock) {
			this.delegate = output;
			this.lock = lock;
		}

		public LockableOutput(Supplier<C> desired, Consumer<C> provided, Unit<C> unit, BooleanSupplier lock) {
			this(new DefaultOutput<>(desired, provided, unit), lock);
		}

		@Override
		public C desiredAmount() {
			if (lock.getAsBoolean()) return delegate.unit().type().container();
			return delegate.desiredAmount();
		}

		@Override
		public void provide(C amount) {
			delegate.provide(amount);
		}

		@Override
		public Unit<C> unit() {
			return delegate.unit();
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

	class ComparatorUpdateStore<C> implements Store<C> {
		protected NetworkBlock networkBlock;
		protected Store<C> delegate;

		public ComparatorUpdateStore(NetworkBlock networkBlock, Store<C> store) {
			this.networkBlock = networkBlock;
			this.delegate = store;
		}

		public ComparatorUpdateStore(NetworkBlock networkBlock, Input<C> input, Output<C> output) {
			this(networkBlock, new DefaultStore<>(input, output));
		}

		@Override
		public C extractableAmount() {
			return delegate.extractableAmount();
		}

		@Override
		public void extract(C amount) {
			delegate.extract(amount);
			networkBlock.getWorld().updateComparators(networkBlock.getPos(), networkBlock.getWorld().getBlockState(networkBlock.getPos()).getBlock());
		}

		@Override
		public C desiredAmount() {
			return delegate.desiredAmount();
		}

		@Override
		public void provide(C amount) {
			delegate.provide(amount);
			networkBlock.getWorld().updateComparators(networkBlock.getPos(), networkBlock.getWorld().getBlockState(networkBlock.getPos()).getBlock());
		}

		@Override
		public Unit<C> unit() {
			return delegate.unit();
		}
	}

	class LockableStore<C> implements Store<C> {
		private Store<C> delegate;
		private BooleanSupplier lock;

		public LockableStore(Store<C> delegate, BooleanSupplier lock) {
			this.delegate = delegate;
			this.lock = lock;
		}

		@Override
		public C extractableAmount() {
			if (lock.getAsBoolean()) return delegate.unit().type().container();
			return delegate.extractableAmount();
		}

		@Override
		public void extract(C amount) {
			delegate.extract(amount);
		}

		@Override
		public C desiredAmount() {
			if (lock.getAsBoolean()) return delegate.unit().type().container();
			return delegate.desiredAmount();
		}

		@Override
		public void provide(C amount) {
			delegate.provide(amount);
		}

		@Override
		public Unit<C> unit() {
			return delegate.unit();
		}
	}

	interface TransmitterIdentifier {}

	interface Transmitter<C> extends Unitable<C>, Networkable<C> {

		TransmitterIdentifier identifier();

		default C transferLimit() {
			return null;
		}

		default boolean isLocked() {
			return false;
		}

		default boolean isNotLocked() {
			return !isLocked();
		}
	}

	class DefaultTransmitter<C> implements Transmitter<C> {

		private TransmitterIdentifier identifier;
		private C limit;
		private BooleanSupplier lock;
		private Unit<C> unit;

		public DefaultTransmitter(TransmitterIdentifier identifier, Unit<C> unit) {
			this(identifier, unit, null, () -> false);
		}

		public DefaultTransmitter(TransmitterIdentifier identifier, Unit<C> unit, C limit) {
			this(identifier, unit, limit, () -> false);
		}

		public DefaultTransmitter(TransmitterIdentifier identifier, Unit<C> unit, BooleanSupplier lock) {
			this(identifier, unit, null, lock);
		}

		public DefaultTransmitter(TransmitterIdentifier identifier, Unit<C> unit, C limit, BooleanSupplier lock) {
			this.identifier = identifier;
			this.limit = limit;
			this.lock = lock;
			this.unit = unit;
		}

		@Override
		public TransmitterIdentifier identifier() {
			return identifier;
		}

		@Override
		public C transferLimit() {
			return limit;
		}

		@Override
		public boolean isLocked() {
			return lock.getAsBoolean();
		}

		@Override
		public Unit<C> unit() {
			return unit;
		}
	}
}
