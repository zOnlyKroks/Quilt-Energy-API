package de.flow.test.blocks;

import net.minecraft.block.*;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

import java.util.IdentityHashMap;
import java.util.Map;


public class CableBlock extends Block implements Waterloggable {

	private static final BooleanProperty CONNECTION_NORTH = BooleanProperty.of("connection_north");
	private static final BooleanProperty CONNECTION_EAST = BooleanProperty.of("connection_east");
	private static final BooleanProperty CONNECTION_SOUTH = BooleanProperty.of("connection_south");
	private static final BooleanProperty CONNECTION_WEST = BooleanProperty.of("connection_west");
	private static final BooleanProperty CONNECTION_UP = BooleanProperty.of("connection_up");
	private static final BooleanProperty CONNECTION_DOWN = BooleanProperty.of("connection_down");
	private static final BooleanProperty SHOW_BASE = BooleanProperty.of("show_base");
	private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

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
		BlockPos.Mutable temp_pos = new BlockPos.Mutable();
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		// Up
		temp_pos.set(x, y + 1, z);
		updateConnectionState(world, temp_pos, CONNECTION_DOWN, false);
		// Down
		temp_pos.set(x, y - 1, z);
		updateConnectionState(world, temp_pos, CONNECTION_UP, false);
		// North
		temp_pos.set(x, y, z + 1);
		updateConnectionState(world, temp_pos, CONNECTION_NORTH, false);
		// South
		temp_pos.set(x, y, z - 1);
		updateConnectionState(world, temp_pos, CONNECTION_SOUTH, false);
		// East
		temp_pos.set(x + 1, y, z);
		updateConnectionState(world, temp_pos, CONNECTION_WEST, false);
		// West
		temp_pos.set(x - 1, y, z);
		updateConnectionState(world, temp_pos, CONNECTION_EAST, false);
		super.onBreak(world, pos, state, player);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		BlockPos.Mutable temp_pos = new BlockPos.Mutable();
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		// Up
		temp_pos.set(x, y + 1, z);
		if (updateConnectionState(world, temp_pos, CONNECTION_DOWN, true)) {
			state = state.with(CONNECTION_UP, true);
		}
		// Down
		temp_pos.set(x, y - 1, z);
		if (updateConnectionState(world, temp_pos, CONNECTION_UP, true)) {
			state = state.with(CONNECTION_DOWN, true);
		}
		// North
		temp_pos.set(x, y, z + 1);
		if (updateConnectionState(world, temp_pos, CONNECTION_NORTH, true)) {
			state = state.with(CONNECTION_SOUTH, true);
		}
		// South
		temp_pos.set(x, y, z - 1);
		if (updateConnectionState(world, temp_pos, CONNECTION_SOUTH, true)) {
			state = state.with(CONNECTION_NORTH, true);
		}
		// East
		temp_pos.set(x + 1, y, z);
		if (updateConnectionState(world, temp_pos, CONNECTION_WEST, true)) {
			state = state.with(CONNECTION_EAST, true);
		}
		// West
		temp_pos.set(x - 1, y, z);
		if (updateConnectionState(world, temp_pos, CONNECTION_EAST, true)) {
			state = state.with(CONNECTION_WEST, true);
		}

		final boolean east = state.get(CONNECTION_EAST);
		final boolean west = state.get(CONNECTION_WEST);
		final boolean north = state.get(CONNECTION_NORTH);
		final boolean south = state.get(CONNECTION_SOUTH);
		final boolean up = state.get(CONNECTION_UP);
		final boolean down = state.get(CONNECTION_DOWN);
		boolean show_base = true;

		if ((east && west) && !(north || south) && !(up || down)) {
			show_base = false;
		}
		else if (!(east || west) && (north && south) && !(up || down)) {
			show_base = false;
		}
		else if (!(east || west) && !(north || south) && (up && down)) {
			show_base = false;
		}
		state = state.with(SHOW_BASE, show_base);
		world.setBlockState(pos, state);
		super.onPlaced(world, pos, state, placer, itemStack);
	}

	public static boolean updateConnectionState(World world, BlockPos.Mutable pos, BooleanProperty property, boolean value) {
		BlockState state = world.getBlockState(pos);

		if (!(state.getBlock() instanceof CableBlock)) {
			return false;
		}
		state = state.with(property, value);

		if (!property.getName().equals(SHOW_BASE.getName())) {
			final boolean east = state.get(CONNECTION_EAST);
			final boolean west = state.get(CONNECTION_WEST);
			final boolean north = state.get(CONNECTION_NORTH);
			final boolean south = state.get(CONNECTION_SOUTH);
			final boolean up = state.get(CONNECTION_UP);
			final boolean down = state.get(CONNECTION_DOWN);
			boolean show_base = true;

			if ((east && west) && !(north || south) && !(up || down)) {
				show_base = false;
			}
			else if (!(east || west) && (north && south) && !(up || down)) {
				show_base = false;
			}
			else if (!(east || west) && !(north || south) && (up && down)) {
				show_base = false;
			}
			state = state.with(SHOW_BASE, show_base);
		}
		world.setBlockState(pos, state);
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
}
