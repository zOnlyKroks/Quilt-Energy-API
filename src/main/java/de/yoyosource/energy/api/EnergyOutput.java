package de.yoyosource.energy.api;

import lombok.Getter;

public interface EnergyOutput extends Unitable {

	static class EnergyOutputSink implements EnergyOutput {
		private long rate;
		private EnergyUnit unit;

		@Getter
		private long provided = 0;

		public EnergyOutputSink(long rate, EnergyUnit unit) {
			this.rate = rate;
			this.unit = unit;
		}

		@Override
		public long desiredAmount() {
			return rate;
		}

		@Override
		public void provide(long amount) {
			provided += amount;
		}

		@Override
		public EnergyUnit unit() {
			return unit;
		}
	}

	class EnergyStore implements EnergyInput, EnergyOutput {

		private long maxStorage;
		@Getter
		private long currentStorage;
		private long maxPassthrough;
		private EnergyUnit unit;

		public EnergyStore(long maxStorage, long maxPassthrough, EnergyUnit unit) {
			this.maxStorage = maxStorage;
			this.unit = unit;
			this.maxPassthrough = maxPassthrough;
		}

		@Override
		public long extractableAmount() {
			return Math.min(currentStorage, maxPassthrough);
		}

		@Override
		public void extract(long amount) {
			currentStorage -= amount;
		}

		@Override
		public long desiredAmount() {
			return Math.min(maxStorage - currentStorage, maxPassthrough);
		}

		@Override
		public void provide(long amount) {
			currentStorage += amount;
		}

		@Override
		public EnergyUnit unit() {
			return unit;
		}
	}

	long desiredAmount(); // in unit as specified by unit()
	void provide(long amount);
}
