package de.flow.impl;

import de.flow.api.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
	private List<NetworkBlock.Transmitter<C>> transmitters = new ArrayList<>();

	private Map<World, Set<BlockPos>> cablePositions = new HashMap<>();
	private Map<World, Set<BlockPos>> io = new HashMap<>();

	public NetworkImpl(Type<C> type) {
		this.type = type;
		this.uuid = UUID.randomUUID();
		markDirty();
	}

	NetworkImpl(UUID uuid, NbtCompound nbtCompound, Map<String, World> worlds) {
		this.uuid = uuid;
		Optional<Block> block = Registry.BLOCK.getOrEmpty(new Identifier(nbtCompound.getString("network-type")));
		if (block.isEmpty() && !(block.get() instanceof NetworkCable<?>)) {
			throw new IllegalArgumentException("Block not found: " + nbtCompound.getString("network-type"));
		}
		this.type = ((NetworkCable<C>) block.get()).type();

		Map<Byte, Map<World, Set<BlockPos>>> positions = convert(nbtCompound, worlds);
		cablePositions = positions.getOrDefault((byte) 0, new HashMap<>());
		io = positions.getOrDefault((byte) 1, new HashMap<>());

		for (Map.Entry<World, Set<BlockPos>> ioEntry : io.entrySet()) {
			for (BlockPos pos : ioEntry.getValue()) {
				BlockEntity blockEntity = ioEntry.getKey().getBlockEntity(pos);
				if (blockEntity instanceof NetworkBlock networkBlock) {
					iterate(networkBlock, (world, blockPos, cNetworkable) -> {
						internalAdd(cNetworkable);
						return true;
					});
				}
			}
		}
	}

	@Override
	public UUID getId() {
		return uuid;
	}

	@Override
	public boolean needsTick() { // TODO: Optimize this for networks without any StatefulNetworkCables
		if (!inputs.isEmpty()) {
			return true;
		}
		if (!transmitters.isEmpty()) {
			return true;
		}
		if (outputs.isEmpty() && storageOutputs.isEmpty()) {
			return false;
		}
		return true;
	}

	private C neededOutput;
	private C storageNeededOutput;
	private C totalNeededOutput;
	private C providedInput;
	private C storageProvidedInput;
	private C totalProvidedInput;

	@Override
	public void calculateAmounts() {
		if (outputs.size() > 1) outputs.add(outputs.remove(0));
		if (storageOutputs.size() > 1) storageOutputs.add(storageOutputs.remove(0));

		neededOutput = type.container();
		storageNeededOutput = type.container();
		totalNeededOutput = type.container();
		for (NetworkBlock.Output<C> output : outputs) {
			C baseAmount = output.unit().convertToBaseUnit(output.desiredAmount());
			type.add(neededOutput, baseAmount);
			type.add(totalNeededOutput, baseAmount);
		}
		for (NetworkBlock.Output<C> output : storageOutputs) {
			C baseAmount = output.unit().convertToBaseUnit(output.desiredAmount());
			type.add(storageNeededOutput, baseAmount);
			type.add(totalNeededOutput, baseAmount);
		}

		providedInput = type.container();
		storageProvidedInput = type.container();
		totalProvidedInput = type.container();
		for (NetworkBlock.Input<C> input : inputs) {
			C totalAmount = input.unit().convertToBaseUnit(input.extractableAmount());
			if (!(input instanceof NetworkBlock.Output<?>)) {
				type.add(providedInput, totalAmount);
			} else {
				type.add(storageProvidedInput, totalAmount);
			}
			type.add(totalProvidedInput, totalAmount);
		}
	}

	@Override
	public boolean hasTransmitter() {
		return transmitters.stream().anyMatch(NetworkBlock.Transmitter::isNotLocked);
	}

	@Override
	public void calculateWithoutTransmitter() {
		boolean storage = type.containsAll(providedInput, neededOutput);
		C availableAmount = storage ? providedInput : totalProvidedInput;

		C availableAmountCopy = type.copy(availableAmount);
		cablePositions.forEach((world, blockPos) -> {
			blockPos.forEach(pos -> {
				BlockState blockState = world.getBlockState(pos);
				Block block = blockState.getBlock();
				if (block instanceof StatefulNetworkCable) {
					((StatefulNetworkCable<C>) block).changeCableState(world, pos, blockState, availableAmountCopy);
				}
			});
		});
		if (type.isEmpty(totalNeededOutput)) return;
		if (type.isEmpty(totalProvidedInput)) return;

		if (storageOutputs.isEmpty() || !storage) {
			availableAmount = type.min(availableAmount, neededOutput);
		} else {
			availableAmount = type.min(availableAmount, totalNeededOutput);
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

	private Map<NetworkBlock.TransmitterIdentifier, C> transmitterLimits;

	@Override
	public void calculateTransmitterLimits() {
		if (transmitters.size() > 1) transmitters.add(transmitters.remove(0));

		transmitterLimits = new HashMap<>();
		Set<NetworkBlock.TransmitterIdentifier> toRemove = new HashSet<>();
		for (NetworkBlock.Transmitter<C> transmitter : transmitters) {
			if (transmitter.isLocked()) continue;
			if (transmitter.transferLimit() == null) {
				toRemove.add(transmitter.identifier());
				continue;
			}
			C limit = transmitterLimits.computeIfAbsent(transmitter.identifier(), id -> type.container());
			type().add(limit, transmitter.transferLimit());
		}
		toRemove.forEach(transmitterLimits::remove);
	}

	@Override
	public void calculateTransmitterNeededOrProvided(Map<NetworkBlock.TransmitterIdentifier, TransmitterData<C>> data) {
		C amount;
		boolean supply;
		if (type.containsAll(providedInput, neededOutput)) {
			amount = type.copy(providedInput);
			type.subtract(amount, neededOutput);
			supply = true;
		} else {
			amount = type.copy(neededOutput);
			type.subtract(amount, providedInput);
			supply = false;
		}

		System.out.println("Network: " + this + " " + amount + " " + supply);
	}

	// Only with transmitter:

	// neededOutput -> 0, providedInput
	// has transmitter » neededOutput left should be requested from transmitters, only max transmitter limit
	//                   providedInput left should be provided to transmitters, only max transmitter limit

	// transmitters stuff will be computed

	// if transmitter balance is negative (so even more is needed) -> provide storageProvidedInput until balance is 0
	// if transmitter balance is positive (so more is provided then needed) -> provided storageNeededOutput until balance is 0

	private boolean internalAdd(Networkable<C> networkable) {
		if (networkable.unit().type() != type) return false;
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
		} else if (networkable instanceof NetworkBlock.Transmitter<C> transmitter) {
			if (!transmitters.contains(transmitter)) {
				transmitters.add(transmitter);
			}
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean add(World world, BlockPos pos, Networkable<C> networkable) {
		if (networkable.unit().type() != type) return false;
		if (io.containsKey(world) && io.get(world).contains(pos)) return false;
		if (!cablePositions.isEmpty() && !cablePositions.containsKey(world)) return false;
		if (!internalAdd(networkable)) return false;
		markDirty();
		io.computeIfAbsent(world, k -> new HashSet<>()).add(pos);
		return true;
	}

	@Override
	public boolean remove(World world, BlockPos pos, Networkable<C> networkable) {
		if (networkable.unit().type() != type) return false;
		if (io.containsKey(world)) {
			Set<BlockPos> blockPos = io.get(world);
			if (blockPos.remove(pos)) markDirty();
			if (blockPos.isEmpty()) io.remove(world);
		}
		if (networkable instanceof NetworkBlock.Output<C>) {
			outputs.remove(networkable);
			storageOutputs.remove(networkable);
		}
		if (networkable instanceof NetworkBlock.Input<C>) {
			inputs.remove(networkable);
		}
		if (networkable instanceof NetworkBlock.Transmitter<C>) {
			transmitters.remove(networkable);
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
		if (!cablePositions.isEmpty() && !cablePositions.containsKey(world)) return false;
		markDirty();
		cablePositions.computeIfAbsent(world, k -> new HashSet<>()).add(pos);
		return true;
	}

	@Override
	public boolean remove(World world, BlockPos pos, NetworkCable<C> networkCable) {
		if (networkCable.type() != type) return false;
		if (cablePositions.containsKey(world)) {
			Set<BlockPos> blockPos = cablePositions.get(world);
			if (blockPos.remove(pos)) markDirty();
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
		convert(nbt, cablePositions, (byte) 0);
		convert(nbt, io, (byte) 1);
		return nbt;
	}

	private void convert(NbtCompound nbt, Map<World, Set<BlockPos>> positions, byte identifier) {
		positions.forEach((world, blockPos) -> {
			String key = world.getRegistryKey().getValue().toString();
			NbtCompound worldNbt = nbt.getCompound(key);
			NbtList x = worldNbt.getList("x", NbtElement.INT_TYPE);
			worldNbt.put("x", x);
			NbtList y = worldNbt.getList("y", NbtElement.INT_TYPE);
			worldNbt.put("y", y);
			NbtList z = worldNbt.getList("z", NbtElement.INT_TYPE);
			worldNbt.put("z", z);
			NbtList type = worldNbt.getList("type", NbtElement.BYTE_TYPE);
			worldNbt.put("type", type);
			nbt.put(key, worldNbt);
			for (BlockPos pos : blockPos) {
				x.add(NbtInt.of(pos.getX()));
				y.add(NbtInt.of(pos.getY()));
				z.add(NbtInt.of(pos.getZ()));
				type.add(NbtByte.of(identifier));
			}
		});
	}

	private Map<Byte, Map<World, Set<BlockPos>>> convert(NbtCompound nbt, Map<String, World> worlds) {
		Map<Byte, Map<World, Set<BlockPos>>> positions = new HashMap<>();
		for (String key : nbt.getKeys()) {
			if (key.equals("network-type")) continue;
			World world = worlds.get(key);
			if (world == null) continue;
			NbtCompound worldNbt = nbt.getCompound(key);
			NbtList x = worldNbt.getList("x", NbtElement.INT_TYPE);
			NbtList y = worldNbt.getList("y", NbtElement.INT_TYPE);
			NbtList z = worldNbt.getList("z", NbtElement.INT_TYPE);
			NbtList type = worldNbt.getList("type", NbtElement.BYTE_TYPE);
			for (int i = 0; i < x.size(); i++) {
				BlockPos pos = new BlockPos(x.getInt(i), y.getInt(i), z.getInt(i));
				byte typeByte = ((NbtByte) type.get(i)).byteValue();
				positions.computeIfAbsent(typeByte, k -> new HashMap<>()).computeIfAbsent(world, k -> new HashSet<>()).add(pos);
			}
		}
		return positions;
	}

	@Override
	public void merge(Network<C> network) {
		if (network.type() != type) return;
		NetworkImpl<C> networkImpl = (NetworkImpl<C>) network;
		inputs.addAll(networkImpl.inputs);
		outputs.addAll(networkImpl.outputs);
		storageOutputs.addAll(networkImpl.storageOutputs);
		networkImpl.cablePositions.forEach((world, blockPositions) -> {
			cablePositions.computeIfAbsent(world, k -> new HashSet<>()).addAll(blockPositions);
		});
		networkImpl.io.forEach((world, blockPositions) -> {
			io.computeIfAbsent(world, k -> new HashSet<>()).addAll(blockPositions);
		});
		this.markDirty();
	}

	@Override
	public void split(World world, BlockPos splitPos, List<BlockPos> blockPosList) {
		boolean first = true;
		while (!blockPosList.isEmpty()) {
			BlockPos currentPeer = blockPosList.remove(0);

			List<BlockPos> networkBlocks = new ArrayList<>();
			List<BlockPos> ioBlocks = new ArrayList<>();
			List<BlockPos> left = new ArrayList<>();
			left.add(currentPeer);

			while (!left.isEmpty()) {
				BlockPos current = left.remove(0);
				if (!networkBlocks.contains(current)) networkBlocks.add(current);
				List<BlockPos> adjacent = adjacent(world, current, (world1, blockPos, direction) -> {
					return world1.getBlockState(blockPos).getBlock() instanceof NetworkCable<?> networkCable && type() == networkCable.type();
				});
				adjacent.remove(splitPos);
				blockPosList.removeIf(adjacent::contains);
				if (blockPosList.isEmpty() && first) return;

				adjacent.forEach(blockPos -> {
					if (!left.contains(blockPos) && !networkBlocks.contains(blockPos)) left.add(blockPos);
				});
				adjacent(world, current, (world1, blockPos, direction) -> {
					if (world1.getBlockEntity(blockPos) instanceof NetworkBlock networkBlock && networkBlock.hasType(type)) {
						return AbstractCableBlock.contains(networkBlock.ports(), direction.getOpposite());
					} else {
						return false;
					}
				}).forEach(blockPos -> {
					if (!ioBlocks.contains(blockPos)) ioBlocks.add(blockPos);
				});
			}

			NetworkImpl<C> network = new NetworkImpl<>(type);
			for (BlockPos blockPos : networkBlocks) {
				network.add(world, blockPos, (NetworkCable<C>) world.getBlockState(blockPos).getBlock());
			}
			for (BlockPos blockPos : ioBlocks) {
				network.add((NetworkBlock) world.getBlockEntity(blockPos));
			}
			NetworkManager.add(network);
			first = false;
		}
		NetworkManager.remove(this);
	}

	private List<BlockPos> adjacent(World world, BlockPos blockPos, TriPredicate<World, BlockPos, Direction> include) {
		List<BlockPos> blockPosList = new ArrayList<>();
		for (Direction direction : Direction.values()) {
			BlockPos offset = blockPos.offset(direction);
			if (include.test(world, offset, direction)) {
				blockPosList.add(offset);
			}
		}
		return blockPosList;
	}
}
