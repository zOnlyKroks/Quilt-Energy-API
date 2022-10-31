package de.flow2.api;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

// TODO: Add JavaDoc
public interface NetworkRequest {

	List<NetworkRequestComponent<?>> components();

	int priority();
	default boolean allowPartial() {
		return true;
	}
	default boolean supplyInSameTick() { // Currently this does not work when issuing multiple types in one request
		return false;
	}

	@Getter
	class NetworkRequestComponent<T> {
		private @NonNull Type<T> type; // TODO: Add multiple types

		@Setter
		private @NonNull T value;

		public NetworkRequestComponent(@NonNull Type<T> type, @NonNull T value) {
			this.type = type;
			this.value = value;
		}
	}
}
