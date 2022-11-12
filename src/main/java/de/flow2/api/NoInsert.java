package de.flow2.api;

import com.google.common.collect.ImmutableSet;
import lombok.NonNull;

/**
 * <h1>NoInsert</h1>
 * NoInsert defined types that cannot be inserted into this block.
 * This should be implemented by the {@link de.flow2.api.machines.MachineEntity}.
 *
 * <br><br><b>Contract:</b>
 * <ul>
 * <li>Calling {@link #types()} twice should return the same object comparable by {@code ==}</li>
 * <li>Calling {@link #types()} should never return null</li>
 * </ul>
 */
public interface NoInsert extends TypeCheck {

	/**
	 * @return types that should not be allowed to be inserted
	 */
	@NonNull ImmutableSet<Type<?>> types();

	/**
	 * Checks if the type is not allowed to be inserted.
	 *
	 * @param type the type to check
	 * @return true if the type is not allowed to be inserted, false otherwise
	 */
	@Override
	default boolean hasType(Type<?> type) {
		return types().contains(type);
	}
}
