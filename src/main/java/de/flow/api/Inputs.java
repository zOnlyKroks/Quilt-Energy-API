package de.flow.api;

public interface Inputs extends NetworkableCollection {

	interface Input<T, C> extends Unitable<T, C>, Networkable<T, C> {
		T extractableAmount();
		void extract(T amount);
	}
}
