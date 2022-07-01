package de.flow.api;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {

	public static final Type<Double, AtomicDouble> ENERGY_TYPE = new Type.NumberType();
}
