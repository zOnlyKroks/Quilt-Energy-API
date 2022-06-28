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

	long desiredAmount(); // in unit as specified by unit()
	void provide(long amount);
}
