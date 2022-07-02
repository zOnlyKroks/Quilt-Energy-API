package de.flow.api;

public interface Outputs extends NetworkableCollection {

	interface Output<T, C> extends Unitable<T, C>, Networkable<T, C> {
		T desiredAmount();
		void provide(T amount);
	}
}
