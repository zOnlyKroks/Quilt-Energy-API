package de.flow.impl;

import lombok.Getter;

import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

public class Container<C> {

	@Getter
	private C value;
	private BiPredicate<C, C> equals;
	private ToIntFunction<C> hashCode;

	public Container(C value, BiPredicate<C, C> equals, ToIntFunction<C> hashCode) {
		this.value = value;
		this.equals = equals;
		this.hashCode = hashCode;
	}

	@Override
	public String toString() {
		return "Container{" + value.toString() + "}";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Container container) {
			try {
				return equals.test(value, (C) container.value);
			} catch (ClassCastException e) {
				return false;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return hashCode.applyAsInt(value);
	}
}
