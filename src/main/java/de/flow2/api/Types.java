package de.flow2.api;

import lombok.experimental.UtilityClass;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

@UtilityClass
public class Types {

	public static List<Type<?>> types() {
		return null; // TODO: Implement
	}

	public <T extends Serializable> void register(Type<T> type, String name) {
		// TODO: Implement
	}

	public <T extends Serializable> boolean isRegistered(Type<T> type) {
		return false; // TODO: Implement
	}

	public boolean isRegistered(String name) {
		return false; // TODO: Implement
	}

	public <T extends Serializable> @Nullable Type<T> type(String name) {
		return null; // TODO: Implement
	}

	public <T extends Serializable> @Nullable String type(Type<T> type) {
		return null; // TODO: Implement
	}
}
