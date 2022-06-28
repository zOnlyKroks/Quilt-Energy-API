package de.yoyosource.energy.api;

import lombok.Getter;

public interface EnergyInput extends Unitable {

	static class EnergyInputCreator implements EnergyInput {
		private double rate;
		private EnergyUnit unit;

		@Getter
		private double extracted = 0;

		public EnergyInputCreator(double rate, EnergyUnit unit) {
			this.rate = rate;
			this.unit = unit;
		}

		@Override
		public double extractableAmount() {
			return rate;
		}

		@Override
		public void extract(double amount) {
			extracted += amount;
		}

		@Override
		public EnergyUnit unit() {
			return unit;
		}
	}

	double extractableAmount(); // in unit as specified by unit()
	void extract(double amount);
}
