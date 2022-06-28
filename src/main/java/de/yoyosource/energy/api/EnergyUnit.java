package de.yoyosource.energy.api;

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
		public long conversionToBase(long amount) {
			return (long) (amount * conversionFactor * lossFactor);
		}

		@Override
		public long conversionFromBase(long amount) {
			return (long) (amount / conversionFactor * lossFactor);
		}
	}

	double conversionToBaseUnit();

	default long conversionToBase(long amount) {
		return (long) (amount * conversionToBaseUnit());
	}

	default long conversionFromBase(long amount) {
		return (long) (amount / conversionToBaseUnit());
	}
}
