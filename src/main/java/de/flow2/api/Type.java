package de.flow2.api;

import lombok.NonNull;

import java.io.Serializable;

/**
 * <h1>Type</h1>
 * Type is used to define what kind of elements can be transferred
 * by a cable or network. A network itself can only have one type.
 * Contrary to that, a cable can have multiple types.
 *
 * <br><br><b>Contract:</b>
 * <ul>
 * <li>the generic {@code T} should never be null</li>
 * <li>networks will never input null into the methods</li>
 * <li>if {@code T} is a container (e.g. List, Map, Set, etc.) the network never checks internal values of those containers, they can have null values stored inside</li>
 * <li>Every method should be treated as a pure function, meaning that the output should only depend on the input and not on the state of any other object</li>
 * <li>{@code T} should never be negative in case of numbers</li>
 * </ul>
 *
 * @param <T> The type of the elements that can be transferred by the network.
 */
public interface Type<T extends Serializable> {

	/**
	 * Returns a new instance of the container that is used to store the elements.
	 *
	 * @return non-null instance of the container
	 */
	@NonNull T defaultValue();

	/**
	 * Adds the element to the container.
	 *
	 * @param a The container to add the element to
	 * @param b The element to add to the container
	 * @return non-null container with the sum
	 */
	@NonNull T add(T a, T b);

	/**
	 * Subtracts the element from the container.
	 *
	 * @param a The container to subtract the element from
	 * @param b The element to subtract from the container
	 * @return non-null container with the difference
	 */
	@NonNull T subtract(T a, T b);

	/**
	 * Checks if the container is empty.
	 *
	 * @param a The container to check
	 * @return true if the container is empty, false otherwise
	 */
	default boolean isEmpty(T a) {
		return a.equals(defaultValue());
	}

	/**
	 * Checks if the container contains at least the elements requested.
	 *
	 * @param container The container to check
	 * @param shouldContain The element to check for
	 * @return true if the container contains the element, false otherwise
	 */
	boolean containsAll(T container, T shouldContain);

	/**
	 * Return the needed if the container contains at least the elements requested or the container itself if it contains less.
	 *
	 * @param container The container to check
	 * @param needed The element to check for
	 * @return non-null container with the available elements
	 */
	@NonNull T available(T container, T needed);

	class IntegerType implements Type<Integer> {

		@Override
		public @NonNull Integer defaultValue() {
			return 0;
		}

		@Override
		public @NonNull Integer add(Integer a, Integer b) {
			return a + b;
		}

		@Override
		public @NonNull Integer subtract(Integer a, Integer b) {
			return a - b;
		}

		@Override
		public boolean containsAll(Integer container, Integer shouldContain) {
			return container >= shouldContain;
		}

		@Override
		public @NonNull Integer available(Integer container, Integer needed) {
			if (container >= needed) {
				return needed;
			} else {
				return container;
			}
		}
	}
}
