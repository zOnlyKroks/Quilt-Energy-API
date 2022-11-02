package de.flow2.api.cables;

import de.flow2.api.Type;
import de.flow2.api.machines.MachineEntity;
import de.flow2.api.networks.Network;
import de.flow2.impl.NetworkImpl;
import de.flow2.impl.NetworkManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractCableBlock extends Block implements CableBlock {

	protected static final BooleanProperty CONNECTION_NORTH = BooleanProperty.of("connection_north");
	protected static final BooleanProperty CONNECTION_EAST = BooleanProperty.of("connection_east");
	protected static final BooleanProperty CONNECTION_SOUTH = BooleanProperty.of("connection_south");
	protected static final BooleanProperty CONNECTION_WEST = BooleanProperty.of("connection_west");
	protected static final BooleanProperty CONNECTION_UP = BooleanProperty.of("connection_up");
	protected static final BooleanProperty CONNECTION_DOWN = BooleanProperty.of("connection_down");
	protected static final BooleanProperty SHOW_BASE = BooleanProperty.of("show_base");

	protected AbstractCableBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(CONNECTION_NORTH, false).with(CONNECTION_EAST, false).with(CONNECTION_SOUTH, false).with(CONNECTION_WEST, false).with(CONNECTION_UP, false).with(CONNECTION_DOWN, false).with(SHOW_BASE, false));
	}

	private static BooleanProperty direction(Direction direction) {
		switch (direction) {
			case NORTH:
				return CONNECTION_NORTH;
			case EAST:
				return CONNECTION_EAST;
			case SOUTH:
				return CONNECTION_SOUTH;
			case WEST:
				return CONNECTION_WEST;
			case UP:
				return CONNECTION_UP;
			case DOWN:
				return CONNECTION_DOWN;
			default:
				throw new IllegalArgumentException("Unknown direction: " + direction);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(SHOW_BASE, CONNECTION_NORTH, CONNECTION_EAST, CONNECTION_SOUTH, CONNECTION_WEST, CONNECTION_UP, CONNECTION_DOWN);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		Map<Type<?>, List<Network<?>>> surroundingNetworks = new HashMap<>();
		List<MachineEntity> machineEntities = new ArrayList<>();

		for (Direction direction : Direction.values()) {
			BlockPos blockPos = pos.offset(direction);
			if (!world.isClient) {
				types().forEach(type -> {
					Network<?> network = NetworkManager.INSTANCE.get(type, world, blockPos);
					if (network == null) return;
					List<Network<?>> networks = surroundingNetworks.computeIfAbsent(type, t -> new ArrayList<>());
					if (networks.contains(network)) return;
					networks.add(network);
				});
			}
			for (Type<?> type : types()) {
				if (tryAddingAndCheckingConnection(world, blockPos, direction(direction.getOpposite()), true, direction, type, machineEntities)) {
					state = state.with(direction(direction), true);
				}
			}
		}

		if (!world.isClient) {
			surroundingNetworks.forEach((type, networks) -> {
				Network network;
				if (networks.isEmpty()) {
					network = new NetworkImpl<>(type);
					NetworkManager.INSTANCE.add(network);
				} else {
					network = networks.get(0);
					for (int i = 1; i < networks.size(); i++) {
						Network<?> surroundingNetwork = networks.get(i);
						network.merge(surroundingNetwork);
						NetworkManager.INSTANCE.remove(surroundingNetwork);
					}
				}
				network.add(world, pos, this);
				machineEntities.forEach(network::add);
			});
		}

		state = state.with(SHOW_BASE, shouldShowBase(state));
		world.setBlockState(pos, state);
	}

	private void breakBlock(World world, BlockPos pos, BlockState state) {
		Map<Type<?>, Network<?>> surroundingNetworks = new HashMap<>();
		Map<Type<?>, List<BlockPos>> blockPoss = new HashMap<>();
		List<MachineEntity> machineEntities = new ArrayList<>();
		int cableCount = 0;
		for (Direction direction: Direction.values()) {
			BlockPos blockPos = pos.offset(direction);
			if (!world.isClient) {
				for (Type<?> type : types()) {
					Network<?> network = NetworkManager.INSTANCE.get(type, world, blockPos);
					if (network == null) continue;
					surroundingNetworks.put(type, network);
					cableCount++;
					blockPoss.computeIfAbsent(type, __ -> new ArrayList<>()).add(blockPos);
				}
			}
			for (Type<?> type : types()) {
				if (tryAddingAndCheckingConnection(world, blockPos, direction(direction.getOpposite()), false, direction, type, machineEntities)) {
					try {
						state = state.with(direction(direction), false);
					} catch (IllegalArgumentException e) {
						// ignore
					}
				}
			}
		}

		if (!world.isClient) {
			for (Type<?> type : types()) {
				Network<?> surroundingNetwork = surroundingNetworks.get(type);
				if (surroundingNetwork == null) {
					surroundingNetwork = NetworkManager.INSTANCE.get(type, world, pos);
				}
				if (surroundingNetwork != null) {
					machineEntities.forEach(surroundingNetwork::remove);
				}
				if (cableCount == 0) {
					NetworkManager.INSTANCE.remove(surroundingNetwork);
				} else {
					surroundingNetwork.remove(world, pos, this);
					List<BlockPos> elements = blockPoss.get(type);
					if (cableCount > 1) {
						surroundingNetwork.split(world, pos, elements);
					}
					machineEntities.forEach(machineEntity -> {
						for (Direction direction : machineEntity.ports()) {
							BlockPos blockPos = machineEntity.getPos().offset(direction);
							if (blockPos.equals(pos)) continue;
							Block block = world.getBlockState(blockPos).getBlock();
							if (block instanceof CableBlock) {
								NetworkManager.INSTANCE.get(type, world, blockPos).add(machineEntity);
							}
						}
					});
				}
			}
		}

		try {
			state = state.with(SHOW_BASE, shouldShowBase(state));
			world.setBlockState(pos, state);
		} catch (IllegalArgumentException e) {
			// ignore
		}
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		breakBlock(world, pos, state);
		super.onBreak(world, pos, state, player);
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
		breakBlock(world, pos, world.getBlockState(pos));
		super.onDestroyedByExplosion(world, pos, explosion);
	}

	public boolean recalculateDirection(World world, BlockPos pos, Direction direction, boolean place) {
		AtomicBoolean any = new AtomicBoolean(false);
		types().forEach(type -> {
			any.compareAndSet(false, tryAddingAndCheckingConnection(world, pos, direction(direction.getOpposite()), place, direction, type, new ArrayList<>()));
		});
		return any.get();
	}

	public static boolean tryAddingAndCheckingConnection(World world, BlockPos pos, BooleanProperty property, boolean value, Direction direction, Type<?> type, List<MachineEntity> machineEntities) {
		BlockState state = world.getBlockState(pos);

		Block block = state.getBlock();
		if (block instanceof CableBlock cableBlock) {
			if (cableBlock.hasType(type)) {
				return false;
			}
			state = state.with(property, value);

			if (!property.getName().equals(SHOW_BASE.getName())) {
				state = state.with(SHOW_BASE, shouldShowBase(state));
			}
			world.setBlockState(pos, state);
			return true;
		}
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof MachineEntity machineEntity) {
			if (!contains(machineEntity.ports(), direction.getOpposite())) {
				return false;
			}
			if (machineEntity.hasType(type)) {
				machineEntities.add(machineEntity);
				return true;
			}
		}
		return false;
	}

	public static boolean contains(Direction[] directions, Direction direction) {
		for (Direction d : directions) {
			if (d == direction) {
				return true;
			}
		}
		return false;
	}

	public static boolean shouldShowBase(BlockState state) {
		final boolean east = state.get(CONNECTION_EAST);
		final boolean west = state.get(CONNECTION_WEST);
		final boolean north = state.get(CONNECTION_NORTH);
		final boolean south = state.get(CONNECTION_SOUTH);
		final boolean up = state.get(CONNECTION_UP);
		final boolean down = state.get(CONNECTION_DOWN);

		if ((east && west) && !(north || south) && !(up || down)) {
			return false;
		} else if (!(east || west) && (north && south) && !(up || down)) {
			return false;
		} else if (!(east || west) && !(north || south) && (up && down)) {
			return false;
		}
		return true;
	}
}
