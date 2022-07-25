package de.flow.impl;

import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

public class Container<C> {

	private C c;
	private BiPredicate<C, C> equals;
	private ToIntFunction<C> hashCode;

	public Container(C value, BiPredicate<C, C> equals, ToIntFunction<C> hashCode) {
		this.c = value;
		this.equals = equals;
		this.hashCode = hashCode;
	}

	@Override
	public String toString() {
		return "Container{" + c.toString() + "}";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Container container) {
			try {
				return equals.test(c, (C) container.c);
			} catch (ClassCastException e) {
				return false;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return hashCode.applyAsInt(c);
	}
}
