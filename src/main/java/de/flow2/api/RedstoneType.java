package de.flow2.api;

import lombok.NonNull;
import net.minecraft.nbt.NbtCompound;

class RedstoneType implements Type<Integer> {

	@Override
	public @NonNull Integer defaultValue() {
		return 0;
	}

	@Override
	public @NonNull Integer add(Integer a, Integer b) {
		return Math.max(a, b);
	}

	@Override
	public @NonNull Integer subtract(Integer a, Integer b) {
		return a;
	}

	@Override
	public boolean containsAll(Integer container, Integer shouldContain) {
		return container != 0;
	}

	@Override
	public @NonNull Integer available(Integer container, Integer needed) {
		return container;
	}

	@Override
	public void writeNBT(Integer value, NbtCompound nbt) {
		nbt.putInt("value", value);
	}

	@Override
	public Integer readNBT(NbtCompound nbt) {
		return nbt.getInt("value");
	}
}
