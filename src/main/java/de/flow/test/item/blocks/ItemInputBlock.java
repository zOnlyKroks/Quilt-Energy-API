package de.flow.test.item.blocks;

import de.flow.api.AbstractNetworkBlock;
import de.flow.test.energy.blocks.LampEntity;
import de.flow.test.item.ItemBlockEntityInit;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

public class ItemInputBlock extends AbstractNetworkBlock {

	public ItemInputBlock() {
		super(QuiltBlockSettings.of(Material.GLASS).collidable(true).strength(2).hardness(2));
	}

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
