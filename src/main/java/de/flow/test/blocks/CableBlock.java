package de.flow.test.blocks;

import com.google.common.util.concurrent.AtomicDouble;
import de.flow.api.NetworkBlock;
import de.flow.api.NetworkCable;
import de.flow.api.Type;
import de.flow.api.Utils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

import java.util.IdentityHashMap;
import java.util.Map;


public class CableBlock extends Block implements NetworkCable<AtomicDouble>, Waterloggable {

	private static final BooleanProperty CONNECTION_NORTH = BooleanProperty.of("connection_north");
	private static final BooleanProperty CONNECTION_EAST = BooleanProperty.of("connection_east");
	private static final BooleanProperty CONNECTION_SOUTH = BooleanProperty.of("connection_south");
	private static final BooleanProperty CONNECTION_WEST = BooleanProperty.of("connection_west");
	private static final BooleanProperty CONNECTION_UP = BooleanProperty.of("connection_up");
	private static final BooleanProperty CONNECTION_DOWN = BooleanProperty.of("connection_down");
	private static final BooleanProperty SHOW_BASE = BooleanProperty.of("show_base");
	private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

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

	private static final Map<BlockState, VoxelShape> SHAPE_CACHE = new IdentityHashMap<>();


	public CableBlock() {
		super(QuiltBlockSettings.of(Material.METAL).strength(6).hardness(6).requiresTool());
		this.setDefaultState(this.getStateManager().getDefaultState().with(SHOW_BASE, true).with(CONNECTION_NORTH, false).with(CONNECTION_EAST, false).with(CONNECTION_SOUTH, false).with(CONNECTION_WEST, false).with(CONNECTION_UP, false).with(CONNECTION_DOWN, false).with(WATERLOGGED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(SHOW_BASE, CONNECTION_NORTH, CONNECTION_EAST, CONNECTION_SOUTH, CONNECTION_WEST, CONNECTION_UP, CONNECTION_DOWN, WATERLOGGED);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
	}

	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
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
		super.onPlaced(world, pos, state, placer, itemStack);
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

	private static VoxelShape getStateShape(BlockState state) {
		VoxelShape shape = VoxelShapes.empty();
		if (state.get(SHOW_BASE)) {
			shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.375, 0.375, 0.625, 0.625, 0.625));
		}
		if (state.get(CONNECTION_UP)) {
			shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.5, 0.4375, 0.5625, 1, 0.5625));
		}
		if (state.get(CONNECTION_DOWN)) {
			shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0, 0.4375, 0.5625, 0.5, 0.5625));
		}
		if (state.get(CONNECTION_NORTH)) {
			shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.4375, 0, 0.5625, 0.5625, 0.5));
		}
		if (state.get(CONNECTION_SOUTH)) {
			shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.4375, 0.5, 0.5625, 0.5625, 1));
		}
		if (state.get(CONNECTION_EAST)) {
			shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5, 0.4375, 0.4375, 1, 0.5625, 0.5625));
		}
		if (state.get(CONNECTION_WEST)) {
			shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.4375, 0.4375, 0.5, 0.5625, 0.5625));
		}
		return shape;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE_CACHE.computeIfAbsent(state, CableBlock::getStateShape);
	}

	@Override
	public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
		return Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState);
	}

	@Override
	public boolean canFillWithFluid(BlockView view, BlockPos pos, BlockState state, Fluid fluid) {
		return Waterloggable.super.canFillWithFluid(view, pos, state, fluid);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public Type<AtomicDouble> type() {
		return Utils.ENERGY_TYPE;
	}
}
