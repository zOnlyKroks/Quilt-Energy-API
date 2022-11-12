package de.flow2.api;

import lombok.NonNull;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class FluidType implements Type<Map<FlowableFluid, Double>> {

	@Override
	public @NonNull Map<FlowableFluid, Double> defaultValue() {
		return new HashMap<>();
	}

	@Override
	public @NonNull Map<FlowableFluid, Double> add(Map<FlowableFluid, Double> a, Map<FlowableFluid, Double> b) {
		if (a.isEmpty()) {
			if (b.size() == 1) {
				return b;
			} else {
				Map.Entry<FlowableFluid, Double> entry = b.entrySet().iterator().next();
				Map<FlowableFluid, Double> map = new HashMap<>();
				map.put(entry.getKey(), entry.getValue());
				return map;
			}
		}
		Map.Entry<FlowableFluid, Double> entry = a.entrySet().iterator().next();
		FlowableFluid fluid = entry.getKey();
		double amount = entry.getValue();
		if (b.containsKey(fluid)) {
			amount += b.get(fluid);
		}
		Map<FlowableFluid, Double> map = new HashMap<>();
		map.put(fluid, amount);
		return map;
	}

	@Override
	public @NonNull Map<FlowableFluid, Double> subtract(Map<FlowableFluid, Double> a, Map<FlowableFluid, Double> b) {
		if (a.isEmpty()) {
			return a;
		}
		Map.Entry<FlowableFluid, Double> entry = a.entrySet().iterator().next();
		FlowableFluid fluid = entry.getKey();
		double amount = entry.getValue();
		if (b.containsKey(fluid)) {
			amount -= b.get(fluid);
		}
		Map<FlowableFluid, Double> map = new HashMap<>();
		map.put(fluid, amount);
		return map;
	}

	@Override
	public boolean containsAll(Map<FlowableFluid, Double> container, Map<FlowableFluid, Double> shouldContain) {
		if (container.isEmpty()) {
			return shouldContain.isEmpty();
		}
		Map.Entry<FlowableFluid, Double> entry = container.entrySet().iterator().next();
		FlowableFluid fluid = entry.getKey();
		double amount = entry.getValue();
		if (shouldContain.containsKey(fluid)) {
			return amount >= shouldContain.get(fluid);
		}
		return false;
	}

	@Override
	public @NonNull Map<FlowableFluid, Double> available(Map<FlowableFluid, Double> container, Map<FlowableFluid, Double> needed) {
		if (container.isEmpty()) {
			return container;
		}
		Map.Entry<FlowableFluid, Double> entry = container.entrySet().iterator().next();
		FlowableFluid fluid = entry.getKey();
		double amount = entry.getValue();
		if (needed.containsKey(fluid)) {
			amount = Math.min(amount, needed.get(fluid));
		}
		Map<FlowableFluid, Double> map = new HashMap<>();
		map.put(fluid, amount);
		return map;
	}

	@Override
	public void writeNBT(Map<FlowableFluid, Double> value, NbtCompound nbt) {
		if (value.isEmpty()) {
			return;
		}
		Map.Entry<FlowableFluid, Double> entry = value.entrySet().iterator().next();
		FlowableFluid fluid = entry.getKey();
		double amount = entry.getValue();
		nbt.putString("fluid", fluid.getBuiltInRegistryHolder().getRegistryKey().getValue().toString());
		nbt.putDouble("amount", amount);
	}

	@Override
	public Map<FlowableFluid, Double> readNBT(NbtCompound nbt) {
		if (!nbt.contains("fluid")) {
			return defaultValue();
		}
		String fluid = nbt.getString("fluid");
		double amount = nbt.getDouble("amount");
		Map<FlowableFluid, Double> map = new HashMap<>();
		map.put((FlowableFluid) Registry.FLUID.get(new Identifier(fluid)), amount);
		return map;
	}
}
