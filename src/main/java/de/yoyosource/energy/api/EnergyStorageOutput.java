package de.yoyosource.energy.api;

public interface EnergyStorageOutput extends EnergyOutput {

	class EnergyStore implements EnergyInput, EnergyStorageOutput {

		private long maxStorage;
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
}
