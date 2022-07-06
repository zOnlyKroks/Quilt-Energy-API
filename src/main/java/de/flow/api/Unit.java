package de.flow.api;

import com.google.common.util.concurrent.AtomicDouble;
import de.flow.impl.UnitImpl;

public interface Unit<C> extends Typeable<C> {
	C convertToBaseUnit(C amount);
	C convertFromBaseUnit(C amount);

	static Unit<AtomicDouble> energyUnit(double factor) {
		return numberUnit(Utils.ENERGY_TYPE, factor);
	}

	static Unit<AtomicDouble> numberUnit(Type<AtomicDouble> type, double factor) {
		return new UnitImpl<>(type, o -> new AtomicDouble(o.get() * factor), o -> new AtomicDouble(o.get() / factor));
	}
}
