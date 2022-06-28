package de.yoyosource.energy.api;

import lombok.Getter;

public interface EnergyInput extends Unitable {

	static class EnergyInputCreator implements EnergyInput {
		private long rate;
		private EnergyUnit unit;

		@Getter
		private long extracted = 0;

		public EnergyInputCreator(long rate, EnergyUnit unit) {
			this.rate = rate;
			this.unit = unit;
		}

		@Override
		public long extractableAmount() {
			return rate;
		}

		@Override
		public void extract(long amount) {
			extracted += amount;
		}

		@Override
		public EnergyUnit unit() {
			return unit;
		}
	}

	long extractableAmount(); // in unit as specified by unit()
	void extract(long amount);
}
