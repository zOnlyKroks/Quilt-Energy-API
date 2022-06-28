package de.yoyosource.energy.impl;

import de.yoyosource.energy.api.EnergyInput;
import de.yoyosource.energy.api.EnergyOutput;
import de.yoyosource.energy.api.EnergyUnit;
import de.yoyosource.energy.api.Unitable;

import java.util.ArrayList;
import java.util.List;

public class Network {

	private List<EnergyInput> energyInputs = new ArrayList<>();
	private List<EnergyOutput> energyOutputs = new ArrayList<>();
	private List<EnergyOutput> energyStorageOutputs = new ArrayList<>();

	public void tick() {
		if (energyOutputs.size() > 1) energyOutputs.add(energyOutputs.remove(0));
		if (energyStorageOutputs.size() > 1) energyStorageOutputs.add(energyStorageOutputs.remove(0));

		long neededAmount = energyOutputs.stream().mapToLong(value -> value.unit().conversionToBase(value.desiredAmount())).sum();
		long nonStorageProvidedAmount = 0;
		long totalProvidedAmount = 0;
		for (EnergyInput energyInput : energyInputs) {
			long amount = energyInput.unit().conversionToBase(energyInput.extractableAmount());
			totalProvidedAmount += amount;
			if (!(energyInput instanceof EnergyOutput)) {
				nonStorageProvidedAmount += amount;
			}
		}
		boolean storage = neededAmount <= nonStorageProvidedAmount;
		long availableAmount = storage ? nonStorageProvidedAmount : totalProvidedAmount;

		long toRemove = availableAmount;
		for (EnergyInput input : energyInputs) {
			long amount = input.extractableAmount();
			long baseAmount = input.unit().conversionToBase(amount);

			if (toRemove > baseAmount) {
				input.extract(baseAmount);
				toRemove -= baseAmount;
			} else {
				input.extract(toRemove);
				break;
			}
		}

		availableAmount = distribute(availableAmount, energyOutputs);
		if (storage && availableAmount > 0) distribute(availableAmount, energyStorageOutputs);
	}

	private long distribute(long availableAmount, List<EnergyOutput> outputs) {
		for (EnergyOutput output : outputs) {
			long amount = output.desiredAmount();
			long baseAmount = output.unit().conversionToBase(amount);
			if (baseAmount > availableAmount) {
				output.provide(output.unit().conversionFromBase(availableAmount));
				availableAmount = 0;
				break;
			} else {
				output.provide(output.unit().conversionFromBase(baseAmount));
				availableAmount -= baseAmount;
			}
		}
		return availableAmount;
	}

	public void add(Unitable unitable) {
		if (unitable instanceof EnergyOutput) {
			if (unitable instanceof EnergyInput) {
				energyInputs.add((EnergyInput) unitable);
				energyStorageOutputs.add((EnergyOutput) unitable);
			} else {
				energyOutputs.add((EnergyOutput) unitable);
			}
		} else if (unitable instanceof EnergyInput) {
			energyInputs.add(0, (EnergyInput) unitable);
		}
	}

	public static void main(String[] args) {
		Network network = new Network();

		if (false) {
			EnergyInput.EnergyInputCreator inputCreator = new EnergyInput.EnergyInputCreator(11, EnergyUnit.BASE_UNIT);
			EnergyOutput.EnergyOutputSink outputSink = new EnergyOutput.EnergyOutputSink(100, new EnergyUnit.LosingEnergyUnit(0.01, 1));
			network.add(inputCreator);
			network.add(outputSink);

			EnergyOutput.EnergyOutputSink outputSink2 = new EnergyOutput.EnergyOutputSink(10, EnergyUnit.BASE_UNIT);
			network.add(outputSink2);

			EnergyOutput.EnergyStore energyStore = new EnergyOutput.EnergyStore(100, 10, EnergyUnit.BASE_UNIT);
			energyStore.provide(1);
			network.add(energyStore);

			network.tick();

			System.out.println(inputCreator.getExtracted());
			System.out.println(outputSink.getProvided());
			System.out.println(outputSink2.getProvided());
			System.out.println(energyStore.getCurrentStorage());
		} else {
			for (int i = 0; i < 10000; i++) {
				network.add(new EnergyInput.EnergyInputCreator(1, EnergyUnit.BASE_UNIT));
				network.add(new EnergyOutput.EnergyOutputSink(1, EnergyUnit.BASE_UNIT));
			}

			for (int i = 0; i < 200; i++) {
				// System.out.println();
				long nanos = System.nanoTime();
				for (int j = 0; j < 50; j++) {
					network.tick();
				}
				System.out.println(((System.nanoTime() - nanos) / 10000L) / 100.0 / 50.0 + "ms");
				// System.out.println(inputCreator.getExtracted());
				// System.out.println(outputSink.getProvided());
				// System.out.println(outputSink2.getProvided());
			}
		}
	}
}
