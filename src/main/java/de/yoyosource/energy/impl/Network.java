package de.yoyosource.energy.impl;

import de.yoyosource.energy.api.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Network {

	private int ticks = 0;

	private List<EnergyInput> energyInputs = new ArrayList<>();
	private List<EnergyOutput> energyOutputs = new ArrayList<>();
	private List<EnergyStorageOutput> energyStorageOutputs = new ArrayList<>();

	public void tick() {
		ticks++;
		if (ticks % 5 == 0) {
			ticks = 0;
			Collections.shuffle(energyOutputs);
			Collections.shuffle(energyStorageOutputs);
		}

		long neededAmount = energyOutputs.stream().mapToLong(value -> value.unit().conversionToBase(value.desiredAmount())).sum();
		boolean storage = true;

		long availableAmount = 0;
		for (EnergyInput energyInput : energyInputs) {
			if (energyInput instanceof EnergyStorageOutput) storage = false;
			long amount = energyInput.extractableAmount();
			long baseAmount = energyInput.unit().conversionToBase(amount);

			if (baseAmount > neededAmount) {
				energyInput.extract(neededAmount);
				availableAmount += neededAmount;
				neededAmount = 0;
				break;
			} else {
				energyInput.extract(baseAmount);
				availableAmount += baseAmount;
				neededAmount -= baseAmount;
			}
		}

		for (EnergyOutput output : energyOutputs) {
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
		if (storage && availableAmount > 0) {
			for (EnergyStorageOutput output : energyStorageOutputs) {
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
		}
	}

	public void add(Unitable unitable) {
		if (unitable instanceof EnergyStorageOutput) {
			if (unitable instanceof EnergyInput) {
				energyInputs.add((EnergyInput) unitable);
			}
			energyStorageOutputs.add((EnergyStorageOutput) unitable);
		} else if (unitable instanceof EnergyOutput) {
			energyOutputs.add((EnergyOutput) unitable);
		} else if (unitable instanceof EnergyInput) {
			energyInputs.add(0, (EnergyInput) unitable);
		}
	}

	public static void main(String[] args) {
		Network network = new Network();

		if (false) {
			EnergyInput.EnergyInputCreator inputCreator = new EnergyInput.EnergyInputCreator(10, EnergyUnit.BASE_UNIT);
			EnergyOutput.EnergyOutputSink outputSink = new EnergyOutput.EnergyOutputSink(100, new EnergyUnit.LosingEnergyUnit(0.01, 1));
			network.energyInputs.add(inputCreator);
			network.energyOutputs.add(outputSink);

			EnergyOutput.EnergyOutputSink outputSink2 = new EnergyOutput.EnergyOutputSink(10, EnergyUnit.BASE_UNIT);
			network.energyOutputs.add(outputSink2);

			network.tick();

			System.out.println(inputCreator.getExtracted());
			System.out.println(outputSink.getProvided());
			System.out.println(outputSink2.getProvided());
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
