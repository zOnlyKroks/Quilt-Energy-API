package de.flow.impl;

import de.flow.impl.Container;
import net.minecraft.item.ItemStack;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

public class ItemStackContainer extends Container<ItemStack> {

	private static final BiPredicate<ItemStack, ItemStack> EQUALS = (first, second) -> {
		if (first.isEmpty() && second.isEmpty()) return true;
		if (first.isEmpty() || second.isEmpty()) return false;
		return first.isOf(second.getItem());
	};

	private static final ToIntFunction<ItemStack> HASH_CODE = (itemStack) -> {
		return Objects.hash(itemStack.getItem(), itemStack.getNbt());
	};

	public ItemStackContainer(ItemStack value) {
		super(value, EQUALS, HASH_CODE);
	}
}
