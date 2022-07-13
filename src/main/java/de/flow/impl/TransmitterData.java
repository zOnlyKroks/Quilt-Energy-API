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
		// TODO: Implement balance method
	}
}
