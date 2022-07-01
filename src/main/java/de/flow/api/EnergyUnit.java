package de.flow.api;

@FunctionalInterface
public interface EnergyUnit {
	EnergyUnit BASE_UNIT = new LosingEnergyUnit(1, 1);

	class LosingEnergyUnit implements EnergyUnit {
		private final double conversionFactor;
		private final double lossFactor;

		public LosingEnergyUnit(double conversionFactor, double lossFactor) {
			this.conversionFactor = conversionFactor;
			this.lossFactor = lossFactor;
		}

		@Override
		public double conversionToBaseUnit() {
			return conversionFactor;
		}

		@Override
		public double conversionToBase(double amount) {
			return amount * conversionFactor * lossFactor;
		}

		@Override
		public double conversionFromBase(double amount) {
			return amount / conversionFactor * lossFactor;
		}
	}

	double conversionToBaseUnit();

	default double conversionToBase(double amount) {
		return amount * conversionToBaseUnit();
	}

	default double conversionFromBase(double amount) {
		return amount / conversionToBaseUnit();
	}
}
