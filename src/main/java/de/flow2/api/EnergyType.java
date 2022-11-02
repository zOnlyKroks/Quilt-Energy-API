package de.flow2.api;

import lombok.NonNull;
import net.minecraft.nbt.NbtCompound;

class EnergyType implements Type<Double> {

	@Override
	public @NonNull Double defaultValue() {
		return 0.0;
	}

	@Override
	public @NonNull Double add(Double a, Double b) {
		return a + b;
	}

	@Override
	public @NonNull Double subtract(Double a, Double b) {
		return a - b;
	}

	@Override
	public boolean containsAll(Double container, Double shouldContain) {
		return container >= shouldContain;
	}

	@Override
	public @NonNull Double available(Double container, Double needed) {
		return Math.min(container, needed);
	}

	@Override
	public void writeNBT(Double value, NbtCompound nbt) {
		nbt.putDouble("value", value);
	}

	@Override
	public Double readNBT(NbtCompound nbt) {
		return nbt.getDouble("value");
	}
}
