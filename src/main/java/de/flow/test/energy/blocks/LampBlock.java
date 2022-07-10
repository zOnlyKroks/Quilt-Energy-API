package de.flow.test.energy.blocks;

import de.flow.api.AbstractNetworkBlock;
import de.flow.test.energy.EnergyBlockEntityInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

public class LampBlock extends AbstractNetworkBlock {
	public LampBlock() {
		super(QuiltBlockSettings.of(Material.GLASS).collidable(true).strength(2).hardness(2).luminance(value -> value.get(LampEntity.LIGHT_LEVEL)));
		this.setDefaultState(this.getStateManager().getDefaultState().with(LampEntity.LIGHT_LEVEL, 0));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LampEntity.LIGHT_LEVEL);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(LampEntity.LIGHT_LEVEL, 0);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new LampEntity(pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? null : checkType(type, EnergyBlockEntityInit.LAMP_ENTITY, LampEntity::tick);
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.BLOCK;
	}
}
