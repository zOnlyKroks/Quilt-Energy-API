package de.flow.impl;

import de.flow.api.*;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.*;

public class NetworkImpl<C> extends PersistentState implements Network<C> {

	private Type<C> type;
	private UUID uuid;

	private List<NetworkBlock.Input<C>> inputs = new ArrayList<>();
	private List<NetworkBlock.Output<C>> outputs = new ArrayList<>();
	private List<NetworkBlock.Output<C>> storageOutputs = new ArrayList<>();

	private Map<World, Set<BlockPos>> cablePositions = new HashMap<>();
	private Map<World, Set<BlockPos>> io = new HashMap<>();

	public NetworkImpl(Type<C> type) {
		this(type, UUID.randomUUID());
	}

	NetworkImpl(Type<C> type, UUID uuid) {
		this.type = type;
		this.uuid = uuid;
	}

	NetworkImpl(UUID uuid, NbtCompound nbtCompound, MinecraftServer minecraftServer) {
		this.uuid = uuid;
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public UUID getId() {
		return uuid;
	}

	@Override
	public void tick() {
		if (outputs.isEmpty() && storageOutputs.isEmpty()) return;
		if (outputs.size() > 1) outputs.add(outputs.remove(0));
		if (storageOutputs.size() > 1) storageOutputs.add(storageOutputs.remove(0));

		C neededAmount = type.container();
		C totalNeededAmount = type.container();
		for (NetworkBlock.Output<C> output : outputs) {
			C amount = output.unit().convertToBaseUnit(output.desiredAmount());
			type.add(totalNeededAmount, amount);
			if (!(output instanceof NetworkBlock.Input)) {
				type.add(neededAmount, amount);
			}
		}
		for (NetworkBlock.Output<C> output : storageOutputs) {
			C amount = output.unit().convertToBaseUnit(output.desiredAmount());
			type.add(totalNeededAmount, amount);
		}

		if (type.isEmpty(totalNeededAmount)) return;

		C nonStorageProvidedAmount = type.container();
		C totalProvidedAmount = type.container();
		for (NetworkBlock.Input<C> input : inputs) {
			C amount = input.unit().convertToBaseUnit(input.extractableAmount());
			type.add(totalProvidedAmount, amount);
			if (!(input instanceof NetworkBlock.Output)) {
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
		for (NetworkBlock.Input<C> input : inputs) {
			C amount = input.extractableAmount();
			C baseAmount = input.unit().convertToBaseUnit(amount);

			C available = type.available(toRemove, baseAmount);
			if (available != null) {
				input.extract(input.unit().convertFromBaseUnit(available));
				type.subtract(toRemove, baseAmount);
			}
			if (type.isEmpty(toRemove)) break;
		}

		distribute(availableAmount, outputs);
		if (storage && !type.isEmpty(availableAmount)) distribute(availableAmount, storageOutputs);
	}

	private void distribute(C availableAmount, List<NetworkBlock.Output<C>> outputs) {
		for (NetworkBlock.Output<C> output : outputs) {
			C baseAmount = output.unit().convertToBaseUnit(output.desiredAmount());

			C available = type.available(availableAmount, baseAmount);
			if (available != null) {
				output.provide(output.unit().convertFromBaseUnit(available));
				type.subtract(availableAmount, baseAmount);
			}
		}
	}

	@Override
	public boolean add(World world, BlockPos pos, Networkable<C> networkable) {
		if (networkable.unit().type() != type) return false;
		if (io.containsKey(world) && io.get(world).contains(pos)) return false;
		if (networkable instanceof NetworkBlock.Output<C> output) {
			if (networkable instanceof NetworkBlock.Input<C> input) {
				if (!inputs.contains(input)) {
					inputs.add(input);
				}
				if (!storageOutputs.contains(output)) {
					storageOutputs.add(output);
				}
			} else {
				if (!outputs.contains(output)) {
					outputs.add(output);
				}
			}
		} else if (networkable instanceof NetworkBlock.Input<C> input) {
			if (!inputs.contains(input)) {
				inputs.add(0, input);
			}
		} else {
			return false;
		}
		markDirty();
		io.computeIfAbsent(world, k -> new HashSet<>()).add(pos);
		return true;
	}

	@Override
	public boolean remove(World world, BlockPos pos, Networkable<C> networkable) {
		if (networkable.unit().type() != type) return false;
		markDirty();
		if (io.containsKey(world)) {
			Set<BlockPos> blockPos = io.get(world);
			blockPos.remove(pos);
			if (blockPos.isEmpty()) io.remove(world);
		}
		if (networkable instanceof NetworkBlock.Output<C>) {
			outputs.remove(networkable);
			storageOutputs.remove(networkable);
		}
		if (networkable instanceof NetworkBlock.Input<C>) {
			inputs.remove(networkable);
		}
		return true;
	}

	@Override
	public Map<World, Set<BlockPos>> cablePositions() {
		return cablePositions;
	}

	@Override
	public boolean add(World world, BlockPos pos, NetworkCable<C> networkCable) {
		if (networkCable.type() != type) return false;
		if (cablePositions.containsKey(world) && cablePositions.get(world).contains(pos)) return false;
		markDirty();
		cablePositions.computeIfAbsent(world, k -> new HashSet<>()).add(pos);
		return true;
	}

	@Override
	public boolean remove(World world, BlockPos pos, NetworkCable<C> networkCable) {
		if (networkCable.type() != type) return false;
		markDirty();
		if (cablePositions.containsKey(world)) {
			Set<BlockPos> blockPos = cablePositions.get(world);
			blockPos.remove(pos);
			if (blockPos.isEmpty()) cablePositions.remove(world);
		}
		return true;
	}

	@Override
	public Type<C> type() {
		return type;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		{
			World world = cablePositions.keySet().iterator().next();
			BlockPos blockPos = cablePositions.get(world).iterator().next();
			Block block = world.getBlockState(blockPos).getBlock();
			if (block instanceof NetworkCable) {
				nbt.putString("network-type", Registry.BLOCK.getId(block).toString());
			}
		}
		nbt.put("cable-positions", convertBlocks(cablePositions));
		nbt.put("io", convertBlocks(io));
		return nbt;
	}

	private NbtList convertBlocks(Map<World, Set<BlockPos>> locations) {
		NbtList blockPositionsList = new NbtList();
		for (Map.Entry<World, Set<BlockPos>> cablePositionsEntry : locations.entrySet()) {
			for (BlockPos blockPos : cablePositionsEntry.getValue()) {
				NbtCompound element = new NbtCompound();
				element.putString("world", cablePositionsEntry.getKey().getRegistryKey().getValue().toString());
				element.putInt("x", blockPos.getX());
				element.putInt("y", blockPos.getY());
				element.putInt("z", blockPos.getZ());
				blockPositionsList.add(element);
			}
		}
		return blockPositionsList;
	}
}
