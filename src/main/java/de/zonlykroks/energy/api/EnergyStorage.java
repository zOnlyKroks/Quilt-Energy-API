package de.zonlykroks.energy.api;

import de.zonlykroks.energy.impl.EnergyImpl;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

/**
 * An object that can store energy.
 *
 * <p><ul>
 *     <li>{@link #supportsInsertion} and {@link #supportsExtraction} can be used to tell if insertion and extraction
 *     functionality are possibly supported by this storage.</li>
 *     <li>{@link #insert} and {@link #extract} can be used to insert or extract resources from this storage.</li>
 *     <li>{@link #getAmount} and {@link #getCapacity} can be used to query the current amount and capacity of this storage.
 *     There is no guarantee that the current amount of energy can be extracted,
 *     nor that something can be inserted if capacity > amount.
 *     If you want to know, you can simulate the operation with {@link #insert} and {@link #extract}.
 *     </li>
 * </ul>
 *
 * @see Transaction
 */
@SuppressWarnings({"unused", "deprecation", "UnstableApiUsage"})
public interface EnergyStorage {

	BlockApiLookup<EnergyStorage, Direction> SIDED =
			BlockApiLookup.get(new Identifier("quiltenergy:sided_energy"), EnergyStorage.class, Direction.class);

	ItemApiLookup<EnergyStorage, ContainerItemContext> ITEM =
			ItemApiLookup.get(new Identifier("quiltenergy:energy"), EnergyStorage.class, ContainerItemContext.class);

	/**
	 * Always empty energy storage.
	 */
	EnergyStorage EMPTY = EnergyImpl.EMPTY;

	/**
	 * Return false if calling {@link #insert} will absolutely always return 0, or true otherwise or in doubt.
	 *
	 * <p>Note: This function is meant to be used by cables or other devices that can transfer energy to know if
	 * they should interact with this storage at all.
	 */
	default boolean supportsInsertion() {
		return true;
	}

	/**
	 * Try to insert up to some amount of energy into this storage.
	 *
	 * @param maxAmount The maximum amount of energy to insert. May not be negative.
	 * @param transaction The transaction this operation is part of.
	 * @return A nonnegative integer not greater than maxAmount: the amount that was inserted.
	 */
	long insert(long maxAmount, TransactionContext transaction);

	/**
	 * Return false if calling {@link #extract} will absolutely always return 0, or true otherwise or in doubt.
	 *
	 * <p>Note: This function is meant to be used by cables or other devices that can transfer energy to know if
	 * they should interact with this storage at all.
	 */
	default boolean supportsExtraction() {
		return true;
	}

	/**
	 * Try to extract up to some amount of energy from this storage.
	 *
	 * @param maxAmount The maximum amount of energy to extract. May not be negative.
	 * @param transaction The transaction this operation is part of.
	 * @return A nonnegative integer not greater than maxAmount: the amount that was extracted.
	 */
	long extract(long maxAmount, TransactionContext transaction);

	/**
	 * Return the current amount of energy that is stored.
	 */
	long getAmount();

	/**
	 * Return the maximum amount of energy that could be stored.
	 */
	long getCapacity();
}
