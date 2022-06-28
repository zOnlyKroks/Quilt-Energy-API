package de.yoyosource.test.blocks;

import de.yoyosource.test.ModInit;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

public class LampBlock extends BlockWithEntity {
	public LampBlock() {
		super(QuiltBlockSettings.of(Material.GLASS).collidable(true).strength(2).hardness(2).luminance(value -> {
			System.out.println(value);
			return value.get(LampEntity.LIGHT_LEVEL);
		}));
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
		return checkType(type, ModInit.LAMP_ENTITY, LampEntity::tick);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		ModInit.network.remove((LampEntity) world.getBlockEntity(pos));
		super.onBreak(world, pos, state, player);
	}
}
