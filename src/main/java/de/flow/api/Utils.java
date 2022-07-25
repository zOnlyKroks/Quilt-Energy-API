package de.flow.api;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.experimental.UtilityClass;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for the Flow API.
 */
@UtilityClass
public class Utils {

	/**
	 * A default type implementation used for energy.
	 */
	public static final Type<AtomicDouble> ENERGY_TYPE = new Type.NumberType();

	/**
	 * A default type implementation used for redstone.
	 */
	public static final Type<AtomicInteger> REDSTONE_TYPE = new RedstoneType();

	/**
	 * A default type implementation used for items.
	 */
	public static final Type<Map<ItemStackContainer, BigInteger>> ITEM_TYPE = new ItemType();
}
