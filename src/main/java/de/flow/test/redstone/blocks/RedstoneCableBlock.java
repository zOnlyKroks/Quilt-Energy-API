package de.flow.test.redstone.blocks;

import de.flow.api.AbstractCableBlock;
import de.flow.api.Type;
import de.flow.api.Utils;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class RedstoneCableBlock extends AbstractCableBlock<AtomicInteger> implements Waterloggable {
	private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	private static final Map<BlockState, VoxelShape> SHAPE_CACHE = new IdentityHashMap<>();


	public RedstoneCableBlock() {
		super(QuiltBlockSettings.of(Material.METAL).strength(6).hardness(6).requiresTool());
		this.setDefaultState(getDefaultState().with(WATERLOGGED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(WATERLOGGED);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
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
		return SHAPE_CACHE.computeIfAbsent(state, RedstoneCableBlock::getStateShape);
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
	public Type<AtomicInteger> type() {
		return Utils.REDSTONE_TYPE;
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.BLOCK;
	}
}
