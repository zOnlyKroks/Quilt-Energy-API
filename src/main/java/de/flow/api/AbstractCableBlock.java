package de.flow.api;

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
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCableBlock<C> extends Block implements NetworkCable<C> {

	protected static final BooleanProperty CONNECTION_NORTH = BooleanProperty.of("connection_north");
	protected static final BooleanProperty CONNECTION_EAST = BooleanProperty.of("connection_east");
	protected static final BooleanProperty CONNECTION_SOUTH = BooleanProperty.of("connection_south");
	protected static final BooleanProperty CONNECTION_WEST = BooleanProperty.of("connection_west");
	protected static final BooleanProperty CONNECTION_UP = BooleanProperty.of("connection_up");
	protected static final BooleanProperty CONNECTION_DOWN = BooleanProperty.of("connection_down");
	protected static final BooleanProperty SHOW_BASE = BooleanProperty.of("show_base");

	public AbstractCableBlock(Settings settings) {
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
		for (Direction direction : Direction.values()) {
			BlockPos blockPos = pos.offset(direction);
			if (tryAddingAndCheckingConnection(world, blockPos, direction(direction.getOpposite()), true, direction, type())) {
				state = state.with(direction(direction), true);
			}
		}
		state = state.with(SHOW_BASE, shouldShowBase(state));
		world.setBlockState(pos, state);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		for (Direction direction : Direction.values()) {
			BlockPos blockPos = pos.offset(direction);
			if (tryAddingAndCheckingConnection(world, blockPos, direction(direction.getOpposite()), false, direction, type())) {
				state = state.with(direction(direction), false);
			}
		}
		state = state.with(SHOW_BASE, shouldShowBase(state));
		world.setBlockState(pos, state);
		super.onBreak(world, pos, state, player);
	}

	public boolean recalculateDirection(World world, BlockPos pos, Direction direction, boolean place) {
		return tryAddingAndCheckingConnection(world, pos, direction(direction.getOpposite()), place, direction, type());
	}

	public static boolean tryAddingAndCheckingConnection(World world, BlockPos pos, BooleanProperty property, boolean value, Direction direction, Type<?> type) {
		BlockState state = world.getBlockState(pos);

		Block block = state.getBlock();
		if (block instanceof NetworkCable<?> networkCable) {
			if (networkCable.type() != type) {
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
		if (blockEntity instanceof NetworkBlock networkBlock) {
			if (!contains(networkBlock.ports(), direction.getOpposite())) {
				return false;
			}
			return networkBlock.hasType(type);
		} else {
			return false;
		}
	}

	private static boolean contains(Direction[] directions, Direction direction) {
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
