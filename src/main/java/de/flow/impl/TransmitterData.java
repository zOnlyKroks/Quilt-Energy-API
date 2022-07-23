package de.flow.impl;

import de.flow.api.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ToString
@RequiredArgsConstructor
public class TransmitterData<C> {

	@AllArgsConstructor
	@Getter
	public static class TransmitterPair<C> {
		private Consumer<C> consumer;
		private C amount;
	}

	private final Type<C> type;

	@Getter
	private List<TransmitterPair<C>> consumers = new ArrayList<>();

	@Getter
	private List<TransmitterPair<C>> suppliers = new ArrayList<>();

	@Getter
	private boolean storage = true;

	public void balance() {
		if (suppliers.isEmpty() && consumers.isEmpty()) {
			return;
		}
		if (suppliers.isEmpty()) {
			storage = false;
			return;
		}
		if (consumers.isEmpty()) return;

		C provided = type.container();
		for (TransmitterPair<C> supplier : suppliers) {
			type.add(provided, supplier.getAmount());
		}

		C needed = type.container();
		for (TransmitterPair<C> consumer : consumers) {
			type.subtract(needed, consumer.getAmount());
		}

		if (type.isEmpty(provided) && type.isEmpty(needed)) return;
		if (type.isEmpty(provided)) {
			storage = false;
			return;
		}
		if (type.isEmpty(needed)) return;

		C available = type.available(provided, needed);
		System.out.println("available: " + available);
		// TODO: Implement balance method
	}
}
