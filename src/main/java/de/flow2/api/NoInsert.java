package de.flow2.api;

import com.google.common.collect.ImmutableList;
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
	@NonNull ImmutableList<Type<?>> types();

	@Override
	default boolean hasType(Type<?> type) {
		return types().stream()
				.anyMatch(t -> t == type);
	}
}
