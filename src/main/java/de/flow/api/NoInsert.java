package de.flow.api;

public interface NoInsert {

	Type<?>[] types();

	default boolean hasNoInsertType(Type<?> type) {
		for (Type<?> t : types()) {
			if (t == type) {
				return true;
			}
		}
		return false;
	}
}
