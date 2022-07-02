package de.flow.impl;

import de.flow.api.*;

import java.util.ArrayList;
import java.util.List;

public class NetworkImpl<T, C> implements Network<T, C> {

	private Type<T, C> type;

	private List<Inputs.Input<T, C>> inputs = new ArrayList<>();
	private List<Outputs.Output<T, C>> outputs = new ArrayList<>();
	private List<Outputs.Output<T, C>> storageOutputs = new ArrayList<>();

	public NetworkImpl(Type<T, C> type) {
		this.type = type;
	}

	@Override
	public void tick() {
		if (outputs.isEmpty() && storageOutputs.isEmpty()) return;
		if (outputs.size() > 1) outputs.add(outputs.remove(0));
		if (storageOutputs.size() > 1) storageOutputs.add(storageOutputs.remove(0));

		C neededAmount = type.container();
		C totalNeededAmount = type.container();
		for (Outputs.Output<T, C> output : outputs) {
			T amount = output.unit().convertToBaseUnit(output.desiredAmount());
			type.add(totalNeededAmount, amount);
			if (!(output instanceof Inputs.Input)) {
				type.add(neededAmount, amount);
			}
		}
		for (Outputs.Output<T, C> output : storageOutputs) {
			T amount = output.unit().convertToBaseUnit(output.desiredAmount());
			type.add(totalNeededAmount, amount);
		}

		if (type.isEmpty(totalNeededAmount)) return;

		C nonStorageProvidedAmount = type.container();
		C totalProvidedAmount = type.container();
		for (Inputs.Input<T, C> input : inputs) {
			T amount = input.unit().convertToBaseUnit(input.extractableAmount());
			type.add(totalProvidedAmount, amount);
			if (!(input instanceof Outputs.Output)) {
				type.add(nonStorageProvidedAmount, amount);
			}
		}

		if (type.isEmpty(totalProvidedAmount)) return;

		boolean storage = type.containsAll(nonStorageProvidedAmount, neededAmount);
		C availableAmount = storage ? nonStorageProvidedAmount : totalProvidedAmount;

		if (storageOutputs.isEmpty() || !storage) {
			availableAmount = type.min(availableAmount, neededAmount);
		} else {
			availableAmount = type.min(availableAmount, totalNeededAmount);
		}

		// System.out.println("neededAmount: " + neededAmount + " totalNeededAmount: " + totalNeededAmount + " availableAmount: " + availableAmount + " storage: " + storage + " nonStorageProvidedAmount: " + nonStorageProvidedAmount + " totalProvidedAmount: " + totalProvidedAmount);

		C toRemove = type.copy(availableAmount);
		for (Inputs.Input<T, C> input : inputs) {
			T amount = input.extractableAmount();
			T baseAmount = input.unit().convertToBaseUnit(amount);

			T available = type.available(toRemove, baseAmount);
			if (available != null) {
				input.extract(input.unit().convertFromBaseUnit(available));
				type.subtract(toRemove, baseAmount);
			}
			if (type.isEmpty(toRemove)) break;
		}

		distribute(availableAmount, outputs);
		if (storage && !type.isEmpty(availableAmount)) distribute(availableAmount, storageOutputs);
	}

	private void distribute(C availableAmount, List<Outputs.Output<T, C>> outputs) {
		for (Outputs.Output<T, C> output : outputs) {
			T baseAmount = output.unit().convertToBaseUnit(output.desiredAmount());

			T available = type.available(availableAmount, baseAmount);
			if (available != null) {
				output.provide(output.unit().convertFromBaseUnit(available));
				type.subtract(availableAmount, baseAmount);
			}
		}
	}

	@Override
	public boolean add(Networkable<T, C> networkable) {
		if (networkable.unit().type() != type) return false;
		if (networkable instanceof Outputs.Output<T, C>) {
			if (networkable instanceof Inputs.Input<T, C>) {
				if (!inputs.contains(networkable)) {
					inputs.add((Inputs.Input<T, C>) networkable);
				}
				if (!storageOutputs.contains(networkable)) {
					storageOutputs.add((Outputs.Output<T, C>) networkable);
				}
			} else {
				if (!outputs.contains(networkable)) {
					outputs.add((Outputs.Output<T, C>) networkable);
				}
			}
		} else if (networkable instanceof Inputs.Input<T, C>) {
			if (!inputs.contains(networkable)) {
				inputs.add(0, (Inputs.Input<T, C>) networkable);
			}
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean remove(Networkable<T, C> networkable) {
		if (networkable.unit().type() != type) return false;
		if (networkable instanceof Outputs.Output<T, C>) {
			outputs.remove(networkable);
			storageOutputs.remove(networkable);
		}
		if (networkable instanceof Inputs.Input<T, C>) {
			inputs.remove(networkable);
		}
		return true;
	}

	@Override
	public Type<T, C> type() {
		return type;
	}
}
