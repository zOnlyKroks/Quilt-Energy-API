package de.flow.api;

import java.lang.reflect.Field;
import java.util.function.Predicate;

public interface Network<C> extends Typeable<C> {

	void tick();
	boolean add(Networkable<C> networkable);
	boolean remove(Networkable<C> networkable);

	default void add(NetworkBlock networkBlock) {
		iterate(networkBlock, this::add);
	}
	default void remove(NetworkBlock networkBlock) {
		iterate(networkBlock, this::remove);
	}

	default void iterate(NetworkBlock networkBlock, Predicate<Networkable<C>> consumer) {
		Field[] fields = networkBlock.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(RegisterToNetwork.class)) {
				try {
					field.setAccessible(true);
					Networkable<C> networkable = (Networkable<C>) field.get(networkBlock);
					consumer.test(networkable);
				} catch (Exception e) {
					// Ignore
				}
			}
		}
	}
}
