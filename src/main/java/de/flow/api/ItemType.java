package de.flow.api;

import net.minecraft.item.ItemStack;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

class ItemType implements Type<Map<ItemStack, BigInteger>> {

	@Override
	public Map<ItemStack, BigInteger> container() {
		return new HashMap<>();
	}

	@Override
	public void add(Map<ItemStack, BigInteger> container, Map<ItemStack, BigInteger> element) {
		for (Map.Entry<ItemStack, BigInteger> entry : element.entrySet()) {
			container.merge(entry.getKey(), entry.getValue(), BigInteger::add);
		}
	}

	@Override
	public void subtract(Map<ItemStack, BigInteger> container, Map<ItemStack, BigInteger> element) {
		for (Map.Entry<ItemStack, BigInteger> entry : element.entrySet()) {
			if (!container.containsKey(entry.getKey())) continue;
			BigInteger nValue = container.get(entry.getKey()).subtract(entry.getValue());
			if (nValue.equals(BigInteger.ZERO)) {
				container.remove(entry.getKey());
			} else {
				container.put(entry.getKey(), nValue);
			}
		}
	}

	@Override
	public boolean containsAll(Map<ItemStack, BigInteger> container, Map<ItemStack, BigInteger> shouldContain) {
		for (Map.Entry<ItemStack, BigInteger> entry : shouldContain.entrySet()) {
			if (!container.containsKey(entry.getKey())) return false;
			if (container.get(entry.getKey()).compareTo(entry.getValue()) < 0) return false;
		}
		return true;
	}

	@Override
	public Map<ItemStack, BigInteger> available(Map<ItemStack, BigInteger> container, Map<ItemStack, BigInteger> needed) {
		Map<ItemStack, BigInteger> available = new HashMap<>();
		for (Map.Entry<ItemStack, BigInteger> entry : needed.entrySet()) {
			if (!container.containsKey(entry.getKey())) continue;
			BigInteger has = container.get(entry.getKey());
			if (has.compareTo(entry.getValue()) < 0) {
				available.put(entry.getKey(), has);
			} else {
				available.put(entry.getKey(), entry.getValue());
			}
		}
		return available;
	}

	@Override
	public boolean isEmpty(Map<ItemStack, BigInteger> container) {
		return container.isEmpty();
	}

	@Override
	public Map<ItemStack, BigInteger> min(Map<ItemStack, BigInteger> c1, Map<ItemStack, BigInteger> c2) {
		Map<ItemStack, BigInteger> min = new HashMap<>(c1);
		for (Map.Entry<ItemStack, BigInteger> entry : c2.entrySet()) {
			min.merge(entry.getKey(), entry.getValue(), BigInteger::min);
		}
		return min;
	}

	@Override
	public Map<ItemStack, BigInteger> copy(Map<ItemStack, BigInteger> container) {
		return new HashMap<>(container);
	}
}
