package de.flow.api;

import com.google.common.util.concurrent.AtomicDouble;
import de.flow.impl.UnitImpl;

public interface Unit<T, C> extends Typeable<T, C> {
	T convertToBaseUnit(T amount);
	T convertFromBaseUnit(T amount);

	static Unit<Double, AtomicDouble> energyUnit(double factor) {
		return numberUnit(Utils.ENERGY_TYPE, factor);
	}

	static Unit<Double, AtomicDouble> numberUnit(Type<Double, AtomicDouble> type, double factor) {
		return new UnitImpl<>(type, o -> o * factor, o -> o / factor);
	}
}
