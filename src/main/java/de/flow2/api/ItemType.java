package de.flow2.api;

import de.flow2.api.utils.ItemStackContainer;
import lombok.NonNull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.HashMap;
import java.util.Map;

class ItemType implements Type<Map<ItemStackContainer, Long>> {

	@Override
	public @NonNull Map<ItemStackContainer, Long> defaultValue() {
		return new HashMap<>();
	}

	@Override
	public @NonNull Map<ItemStackContainer, Long> add(Map<ItemStackContainer, Long> a, Map<ItemStackContainer, Long> b) {
		for (Map.Entry<ItemStackContainer, Long> entry : b.entrySet()) {
			a.merge(entry.getKey(), entry.getValue(), Long::sum);
		}
		return a;
	}

	@Override
	public @NonNull Map<ItemStackContainer, Long> subtract(Map<ItemStackContainer, Long> a, Map<ItemStackContainer, Long> b) {
		for (Map.Entry<ItemStackContainer, Long> entry : b.entrySet()) {
			if (!a.containsKey(entry.getKey())) continue;
			long nValue = a.get(entry.getKey()) - entry.getValue();
			if (nValue == 0) {
				a.remove(entry.getKey());
			} else {
				a.put(entry.getKey(), nValue);
			}
		}
		return a;
	}

	@Override
	public boolean containsAll(Map<ItemStackContainer, Long> container, Map<ItemStackContainer, Long> shouldContain) {
		for (Map.Entry<ItemStackContainer, Long> entry : shouldContain.entrySet()) {
			if (!container.containsKey(entry.getKey())) return false;
			if (container.get(entry.getKey()) < entry.getValue()) return false;
		}
		return true;
	}

	@Override
	public @NonNull Map<ItemStackContainer, Long> available(Map<ItemStackContainer, Long> container, Map<ItemStackContainer, Long> needed) {
		Map<ItemStackContainer, Long> available = new HashMap<>();
		for (Map.Entry<ItemStackContainer, Long> entry : needed.entrySet()) {
			if (!container.containsKey(entry.getKey())) continue;
			long has = container.get(entry.getKey());
			if (has < entry.getValue()) {
				available.put(entry.getKey(), has);
			} else {
				available.put(entry.getKey(), entry.getValue());
			}
		}
		return available;
	}

	@Override
	public void writeNBT(Map<ItemStackContainer, Long> value, NbtCompound nbt) {
		for (Map.Entry<ItemStackContainer, Long> entry : value.entrySet()) {
			NbtList elementList = nbt.getList("elements", NbtCompound.COMPOUND_TYPE);
			NbtCompound savedEntry = new NbtCompound();
			savedEntry.put("item", entry.getKey().getValue().writeNbt(new NbtCompound()));
			savedEntry.putLong("amount", entry.getValue());
			elementList.add(savedEntry);
		}
	}

	@Override
	public Map<ItemStackContainer, Long> readNBT(NbtCompound nbt) {
		Map<ItemStackContainer, Long> value = new HashMap<>();
		NbtList elementList = nbt.getList("elements", NbtCompound.COMPOUND_TYPE);
		for (int i = 0; i < elementList.size(); i++) {
			NbtCompound savedEntry = elementList.getCompound(i);
			value.put(new ItemStackContainer(ItemStack.fromNbt(savedEntry.getCompound("item"))), savedEntry.getLong("amount"));
		}
		return value;
	}
}
