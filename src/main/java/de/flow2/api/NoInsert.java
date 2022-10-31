package de.flow2.api;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;

/**
 * <h1>NoInsert</h1>
 * NoInsert defined types that cannot be inserted into this block.
 *
 * <br><br><b>Contract:</b>
 * <ul>
 * <li>Calling {@link #noInsertTypes()} twice should return the same object comparable by {@code ==}</li>
 * <li>Calling {@link #noInsertTypes()} should never return null</li>
 * </ul>
 */
public interface NoInsert {

	/**
	 * @return types that should not be allowed to be inserted
	 */
	@NonNull ImmutableList<Type<?>> noInsertTypes();

	default boolean hasNoInsertType(Type<?> type) {
		return noInsertTypes().stream()
				.anyMatch(t -> t == type);
	}
}
