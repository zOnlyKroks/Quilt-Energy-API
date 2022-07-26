package de.flow.test.item.iteminput;

import de.flow.api.AbstractNetworkBlock;
import de.flow.test.item.ItemBlockEntityInit;
import de.flow.test.item.itemcable.ItemCableBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

import java.util.IdentityHashMap;
import java.util.Map;

public class ItemInputBlock extends AbstractNetworkBlock {

	//private static final VoxelShape shape = getShape();

	public ItemInputBlock() {
		super(QuiltBlockSettings.of(Material.GLASS).collidable(true).strength(2).hardness(2));
	}

	/*
	private static VoxelShape getShape() {
		VoxelShape shape = VoxelShapes.empty();
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.875, 0, 0, 1, 1, 0.125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.875, 0, 0.875, 1, 1, 1));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0, 0, 0.875, 0.125, 0.125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0, 0.875, 0.875, 0.125, 1));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.875, 0, 0.875, 1, 0.125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.875, 0.875, 0.875, 1, 1));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.875, 0, 0.125, 1, 0.125, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.875, 0.875, 0.125, 1, 1, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.125, 0.125, 0.125, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.875, 0.125, 0.125, 1, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 0.125, 1, 0.125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.875, 0.125, 1, 1));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.125, 0.125, 0.1875, 0.1875, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.8125, 0.125, 0.1875, 0.875, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.8125, 0.8125, 0.125, 0.875, 0.875, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.8125, 0.125, 0.125, 0.875, 0.1875, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.125, 0.125, 0.8125, 0.1875, 0.1875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.125, 0.8125, 0.8125, 0.1875, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.8125, 0.8125, 0.8125, 0.875, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.8125, 0.125, 0.8125, 0.875, 0.1875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.1875, 0.8125, 0.1875, 0.8125, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.1875, 0.125, 0.1875, 0.8125, 0.1875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.8125, 0.1875, 0.125, 0.875, 0.8125, 0.1875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.8125, 0.1875, 0.8125, 0.875, 0.8125, 0.875));
		return shape;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return shape;
	}
	*/

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ItemInputEntity(pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? null : checkType(type, ItemBlockEntityInit.ITEM_INPUT_ENTITY, ItemInputEntity::tick);
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.BLOCK;
	}
}
