package de.flow.impl;

import de.flow.api.Type;
import de.flow.api.Unit;

import java.util.function.UnaryOperator;

public class UnitImpl<T, C> implements Unit<T, C> {

	private Type<T, C> type;
	private UnaryOperator<T> conversionToBaseUnit;
	private UnaryOperator<T> conversionFromBaseUnit;

	public UnitImpl(Type<T, C> type, UnaryOperator<T> conversionToBaseUnit, UnaryOperator<T> conversionFromBaseUnit) {
		this.type = type;
		this.conversionToBaseUnit = conversionToBaseUnit;
		this.conversionFromBaseUnit = conversionFromBaseUnit;
	}

	@Override
	public Type<T, C> type() {
		return type;
	}

	@Override
	public T convertToBaseUnit(T amount) {
		return conversionToBaseUnit.apply(amount);
	}

	@Override
	public T convertFromBaseUnit(T amount) {
		return conversionFromBaseUnit.apply(amount);
	}
}
