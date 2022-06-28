package de.zonlykroks.energy.impl;

import de.zonlykroks.energy.api.EnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class EnergyImpl {

	public static final EnergyStorage EMPTY = new EnergyStorage() {
		@Override
		public boolean supportsInsertion() {
			return false;
		}

		@Override
		public long insert(long maxAmount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public boolean supportsExtraction() {
			return false;
		}

		@Override
		public long extract(long maxAmount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public long getAmount() {
			return 0;
		}

		@Override
		public long getCapacity() {
			return 0;
		}
	};

}
