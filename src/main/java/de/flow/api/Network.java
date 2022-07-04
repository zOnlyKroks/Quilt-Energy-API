package de.flow.api;

import java.lang.reflect.Field;
import java.util.function.Predicate;

public interface Network<T, C> extends Typeable<T, C> {

	void tick();
	boolean add(Networkable<T, C> networkable);
	boolean remove(Networkable<T, C> networkable);

	default void add(NetworkableCollection networkableCollection) {
		iterate(networkableCollection, this::add);
	}
	default void remove(NetworkableCollection networkableCollection) {
		iterate(networkableCollection, this::remove);
	}

	default void iterate(NetworkableCollection networkableCollection, Predicate<Networkable<T, C>> consumer) {
		Field[] fields = networkableCollection.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(RegisterToNetwork.class)) {
				try {
					Networkable<T, C> networkable = (Networkable<T, C>) field.get(networkableCollection);
					consumer.test(networkable);
				} catch (Exception e) {
					// Ignore
				}
			}
		}
	}
}
