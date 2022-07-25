package de.flow.impl;

import de.flow.api.Type;
import de.flow.api.Unit;

import java.util.function.UnaryOperator;

public class UnitImpl<C> implements Unit<C> {

	private Type<C> type;
	private UnaryOperator<C> conversionToBaseUnit;
	private UnaryOperator<C> conversionFromBaseUnit;

	public UnitImpl(Type<C> type) {
		this(type, UnaryOperator.identity(), UnaryOperator.identity());
	}

	public UnitImpl(Type<C> type, UnaryOperator<C> conversionToBaseUnit, UnaryOperator<C> conversionFromBaseUnit) {
		this.type = type;
		this.conversionToBaseUnit = conversionToBaseUnit;
		this.conversionFromBaseUnit = conversionFromBaseUnit;
	}

	@Override
	public Type<C> type() {
		return type;
	}

	@Override
	public C convertToBaseUnit(C amount) {
		return conversionToBaseUnit.apply(amount);
	}

	@Override
	public C convertFromBaseUnit(C amount) {
		return conversionFromBaseUnit.apply(amount);
	}
}
