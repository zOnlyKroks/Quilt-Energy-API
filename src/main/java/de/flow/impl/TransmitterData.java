package de.flow.impl;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TransmitterData<C> {

	@Getter
	private Map<Consumer<C>, C> consumers = new HashMap<>();

	@Getter
	private Map<Consumer<C>, C> suppliers = new HashMap<>();
}
