package de.flow2.api.cables;

import com.google.common.collect.ImmutableList;
import de.flow2.api.Type;
import lombok.NonNull;

// TODO: Add JavaDoc
public interface NetworkCableBlock {

	/**
	 * <b>Contract:</b>
	 * <ul>
	 * <li>Calling {@link #types()} twice should return the same object comparable by {@code ==}</li>
	 * <li>Calling {@link #types()} should never return null</li>
	 * </ul>
	 *
	 * @return the types the cable should be able to transfer
	 */
	@NonNull ImmutableList<Type<?>> types();
}
