package de.flow.api;

/**
 * Typeable is used for elements of the flow api that need a type to be attached to them.
 *
 * @param <C> The generic-type of the {@link Type}
 */
public interface Typeable<C> {
	/**
	 * Returns the {@link Type} of the element.
	 *
	 * @return non-null {@link Type}
	 */
	Type<C> type();
}
