package de.flow2.api;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;

/**
 * <h1>Typeable</h1>
 * Typeable is used for the cables and defined what a cable can transfer.
 *
 * <br><br><b>Contract:</b>
 * <ul>
 * <li>Calling {@link #types()} twice should return the same object comparable by {@code ==}</li>
 * <li>Calling {@link #types()} should never return null</li>
 * </ul>
 */
public interface Typeable {

	/**
	 *
	 * @return the types the cable should be able to transfer
	 */
	@NonNull ImmutableList<Type<?>> types();
}
