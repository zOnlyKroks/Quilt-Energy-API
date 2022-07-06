package de.flow.api;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class Networks {
	private Map<Type<?>, List<Network<?>>> networks = new HashMap<>();
}
