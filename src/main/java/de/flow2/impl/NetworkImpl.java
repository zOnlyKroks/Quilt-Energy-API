package de.flow2.impl;

import de.flow.api.AbstractCableBlock;
import de.flow.api.Network.TriPredicate;
import de.flow2.api.Type;
import de.flow2.api.cables.CableBlock;
import de.flow2.api.machines.MachineEntity;
import de.flow2.api.machines.Typed;
import de.flow2.api.machines.input.Input;
import de.flow2.api.machines.output.Output;
import de.flow2.api.networks.Network;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.*;

public class NetworkImpl<T> extends PersistentState implements Network<T> {

	private Type<T> type;
	private UUID id;

	private List<Input<T>> inputs = new ArrayList<>();
	private List<Output<T>> outputs = new ArrayList<>();

	private Map<World, Set<BlockPos>> cables = new HashMap<>();
	private Map<World, Set<BlockPos>> machines = new HashMap<>();

	public NetworkImpl(Type<?> type) {
		this.type = (Type<T>) type;
		this.id = UUID.randomUUID();
		markDirty();
	}

	public NetworkImpl(UUID uuid, NbtCompound nbtCompound, Map<String, World> worlds) {
		this.type = TypeManager.INSTANCE.type(Identifier.tryParse(nbtCompound.getString("type")));
		this.id = uuid;

		Map<Byte, Map<World, Set<BlockPos>>> positions = convert(nbtCompound, worlds);
		cables = positions.getOrDefault((byte) 0, new HashMap<>());
		machines = positions.getOrDefault((byte) 1, new HashMap<>());

		for (Map.Entry<World, Set<BlockPos>> ioEntry : machines.entrySet()) {
			for (BlockPos pos : ioEntry.getValue()) {
				BlockEntity blockEntity = ioEntry.getKey().getBlockEntity(pos);
				if (blockEntity instanceof MachineEntity machineEntity) {
					machineEntity.iterate((world, blockPos, typed) -> {
						if (typed.type() == type) internalAdd((Typed<T>) typed);
					});
				}
			}
		}
	}

	@Override
	public Type<T> type() {
		return type;
	}

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public void tick() {
		// TODO: Implement
	}

	private boolean internalAdd(Typed<T> type) {
		if (type.type() != this.type) return false;
		boolean result = false;
		if (type instanceof Output<T> output) {
			outputs.add(output);
			result = true;
		}
		if (type instanceof Input<T> input) {
			inputs.add(input);
			result = true;
		}
		return result;
	}

	@Override
	public boolean add(World world, BlockPos blockPos, Typed<T> machineIOPort) {
		if (machineIOPort.type() != type) return false;
		if (machines.containsKey(world) && machines.get(world).contains(blockPos)) return false;
		if (!cables.isEmpty() && !cables.containsKey(world)) return false;
		if (!internalAdd(machineIOPort)) return false;
		markDirty();
		machines.computeIfAbsent(world, __ -> new HashSet<>()).add(blockPos);
		return true;
	}

	@Override
	public boolean remove(World world, BlockPos blockPos, Typed<T> machineIOPort) {
		if (machineIOPort.type() != type) return false;
		if (machines.containsKey(world)) {
			Set<BlockPos> posSet = machines.get(world);
			if (posSet.remove(blockPos)) markDirty();
			if (posSet.isEmpty()) machines.remove(world);
		}
		if (machineIOPort instanceof Output<T> output) {
			outputs.remove(output);
		}
		if (machineIOPort instanceof Input<T> input) {
			inputs.remove(input);
		}
		return true;
	}

	@Override
	public Map<World, Set<BlockPos>> cablePositions() {
		return cables;
	}

	@Override
	public boolean add(World world, BlockPos pos, CableBlock cableBlock) {
		if (!cableBlock.hasType(type)) return false;
		if (cables.containsKey(world) && cables.get(world).contains(pos)) return false;
		if (!machines.isEmpty() && !machines.containsKey(world)) return false;
		markDirty();
		cables.computeIfAbsent(world, __ -> new HashSet<>()).add(pos);
		return true;
	}

	@Override
	public boolean remove(World world, BlockPos pos, CableBlock cableBlock) {
		if (!cableBlock.hasType(type)) return false;
		if (cables.containsKey(world)) {
			Set<BlockPos> posSet = cables.get(world);
			if (posSet.remove(pos)) markDirty();
			if (posSet.isEmpty()) cables.remove(world);
		}
		return true;
	}

	@Override
	public void merge(Network<T> network) {
		if (network.type() != type) return;
		NetworkImpl<T> networkImpl = (NetworkImpl<T>) network;
		inputs.addAll(networkImpl.inputs);
		outputs.addAll(networkImpl.outputs);
		cables.putAll(networkImpl.cables);
		networkImpl.machines.forEach((world, blockPosSet) -> machines.computeIfAbsent(world, __ -> new HashSet<>()).addAll(blockPosSet));
		networkImpl.cables.forEach((world, blockPosSet) -> cables.computeIfAbsent(world, __ -> new HashSet<>()).addAll(blockPosSet));
		markDirty();
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
					return world1.getBlockState(blockPos).getBlock() instanceof CableBlock cableBlock && cableBlock.hasType(type);
				});
				adjacent.remove(splitPos);
				blockPosList.removeIf(adjacent::contains);
				if (blockPosList.isEmpty() && first) return;

				adjacent.forEach(blockPos -> {
					if (!left.contains(blockPos) && !networkBlocks.contains(blockPos)) left.add(blockPos);
				});
				adjacent(world, current, (world1, blockPos, direction) -> {
					if (world1.getBlockEntity(blockPos) instanceof MachineEntity machineEntity && machineEntity.hasType(type)) {
						return AbstractCableBlock.contains(machineEntity.ports(), direction.getOpposite());
					} else {
						return false;
					}
				}).forEach(blockPos -> {
					if (!ioBlocks.contains(blockPos)) ioBlocks.add(blockPos);
				});
			}

			NetworkImpl<T> network = new NetworkImpl<>(type);
			for (BlockPos blockPos : networkBlocks) {
				network.add(world, blockPos, (CableBlock) world.getBlockState(blockPos).getBlock());
			}
			for (BlockPos blockPos : ioBlocks) {
				network.add((MachineEntity) world.getBlockEntity(blockPos));
			}
			NetworkManager.INSTANCE.add(network);
			first = false;
		}
		NetworkManager.INSTANCE.remove(this);
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

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putString("type", TypeManager.INSTANCE.type(type).toString());
		convert(nbt, cables, (byte) 0);
		convert(nbt, machines, (byte) 1);
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
			if (key.equals("type")) continue;
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
}
