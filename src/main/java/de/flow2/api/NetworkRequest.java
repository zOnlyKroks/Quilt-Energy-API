package de.flow2.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

public interface NetworkRequest {

	List<NetworkRequestComponent<?>> components();

	int priority();
	default boolean supplyAtOnce() {
		return false;
	}

	@AllArgsConstructor
	@Getter
	class NetworkRequestComponent<T> {
		private @NonNull Type<T> type; // TODO: Add multiple types

		@Setter
		private @NonNull T value;
	}
}
