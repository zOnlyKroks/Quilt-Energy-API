package de.flow2.api.cables;

import de.flow2.api.Type;
import de.flow2.api.machines.MachineEntity;
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
import java.util.List;
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
		// TODO: Implement
	}

	private void breakBlock(World world, BlockPos pos, BlockState state) {
		// TODO: Implement
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
