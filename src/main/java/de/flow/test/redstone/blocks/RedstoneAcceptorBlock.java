package de.flow.test.redstone.blocks;

import de.flow.api.AbstractNetworkBlock;
import de.flow.test.redstone.RedstoneBlockEntityInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

public class RedstoneAcceptorBlock extends AbstractNetworkBlock {
	private static final BooleanProperty POWERED = Properties.POWERED;
	public RedstoneAcceptorBlock() {
		super(QuiltBlockSettings.of(Material.METAL).strength(6).hardness(6).requiresTool().luminance(value -> value.get(POWERED) ? 2 : 0));
		this.setDefaultState(getDefaultState().with(POWERED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(POWERED);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new RedstoneAcceptorEntity(pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? null : checkType(type, RedstoneBlockEntityInit.ACCEPTOR_ENTITY, RedstoneAcceptorEntity::tick);
	}
}
