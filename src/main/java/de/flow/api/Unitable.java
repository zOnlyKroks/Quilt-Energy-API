package de.flow.api;

/**
 * Unitable is used for elements of the flow api that need a unit to be attached to them.
 *
 * @param <C> The generic-type of the {@link Unit}
 */
public interface Unitable<C> {
	/**
	 * Returns the {@link Unit} of the element.
	 *
	 * @return non-null {@link Unit}
	 */
	Unit<C> unit();
}
