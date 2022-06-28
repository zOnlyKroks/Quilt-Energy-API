package de.yoyosource.energy.api;

import lombok.Getter;

public interface EnergyOutput extends Unitable {

	static class EnergyOutputSink implements EnergyOutput {
		private double rate;
		private EnergyUnit unit;

		@Getter
		private long provided = 0;

		public EnergyOutputSink(double rate, EnergyUnit unit) {
			this.rate = rate;
			this.unit = unit;
		}

		@Override
		public double desiredAmount() {
			return rate;
		}

		@Override
		public void provide(double amount) {
			provided += amount;
		}

		@Override
		public EnergyUnit unit() {
			return unit;
		}
	}

	class EnergyStore implements EnergyInput, EnergyOutput {

		private double maxStorage;
		@Getter
		private double currentStorage;
		private double maxPassthrough;
		private EnergyUnit unit;

		public EnergyStore(double maxStorage, double maxPassthrough, EnergyUnit unit) {
			this.maxStorage = maxStorage;
			this.unit = unit;
			this.maxPassthrough = maxPassthrough;
		}

		@Override
		public double extractableAmount() {
			return Math.min(currentStorage, maxPassthrough);
		}

		@Override
		public void extract(double amount) {
			currentStorage -= amount;
		}

		@Override
		public double desiredAmount() {
			return Math.min(maxStorage - currentStorage, maxPassthrough);
		}

		@Override
		public void provide(double amount) {
			currentStorage += amount;
		}

		@Override
		public EnergyUnit unit() {
			return unit;
		}
	}

	double desiredAmount(); // in unit as specified by unit()
	void provide(double amount);
}
