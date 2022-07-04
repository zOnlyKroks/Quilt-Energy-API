package de.flow.api;

import java.lang.reflect.Field;
import java.util.function.Predicate;

public interface Network<T, C> extends Typeable<T, C> {

	void tick();
	boolean add(Networkable<T, C> networkable);
	boolean remove(Networkable<T, C> networkable);

	default void add(NetworkBlock networkBlock) {
		iterate(networkBlock, this::add);
	}
	default void remove(NetworkBlock networkBlock) {
		iterate(networkBlock, this::remove);
	}

	default void iterate(NetworkBlock networkBlock, Predicate<Networkable<T, C>> consumer) {
		Field[] fields = networkBlock.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(RegisterToNetwork.class)) {
				try {
					field.setAccessible(true);
					Networkable<T, C> networkable = (Networkable<T, C>) field.get(networkBlock);
					consumer.test(networkable);
				} catch (Exception e) {
					// Ignore
				}
			}
		}
	}
}
