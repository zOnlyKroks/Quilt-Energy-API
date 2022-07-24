package de.flow.api;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.experimental.UtilityClass;
import net.minecraft.item.ItemStack;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class Utils {

	public static final Type<AtomicDouble> ENERGY_TYPE = new Type.NumberType();

	public static final Type<AtomicInteger> REDSTONE_TYPE = new RedstoneType();

	public static final Type<Map<ItemStack, BigInteger>> ITEM_TYPE = new ItemType();
}
