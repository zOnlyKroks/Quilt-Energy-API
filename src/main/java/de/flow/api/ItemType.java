package de.flow.api;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Internal API used in {@link Utils}
 */
class ItemType implements Type<Map<ItemStackContainer, BigInteger>> {

	@Override
	public Map<ItemStackContainer, BigInteger> container() {
		return new HashMap<>();
	}

	@Override
	public void add(Map<ItemStackContainer, BigInteger> container, Map<ItemStackContainer, BigInteger> element) {
		for (Map.Entry<ItemStackContainer, BigInteger> entry : element.entrySet()) {
			container.merge(entry.getKey(), entry.getValue(), BigInteger::add);
		}
	}

	@Override
	public void subtract(Map<ItemStackContainer, BigInteger> container, Map<ItemStackContainer, BigInteger> element) {
		for (Map.Entry<ItemStackContainer, BigInteger> entry : element.entrySet()) {
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
	public boolean containsAll(Map<ItemStackContainer, BigInteger> container, Map<ItemStackContainer, BigInteger> shouldContain) {
		for (Map.Entry<ItemStackContainer, BigInteger> entry : shouldContain.entrySet()) {
			if (!container.containsKey(entry.getKey())) return false;
			if (container.get(entry.getKey()).compareTo(entry.getValue()) < 0) return false;
		}
		return true;
	}

	@Override
	public Map<ItemStackContainer, BigInteger> available(Map<ItemStackContainer, BigInteger> container, Map<ItemStackContainer, BigInteger> needed) {
		Map<ItemStackContainer, BigInteger> available = new HashMap<>();
		for (Map.Entry<ItemStackContainer, BigInteger> entry : needed.entrySet()) {
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
	public boolean isEmpty(Map<ItemStackContainer, BigInteger> container) {
		return container.isEmpty();
	}

	@Override
	public Map<ItemStackContainer, BigInteger> copy(Map<ItemStackContainer, BigInteger> container) {
		return new HashMap<>(container);
	}
}
