package de.flow.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractNetworkBlock extends BlockWithEntity {

	public AbstractNetworkBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		// System.out.println("PLACE: " + world.getBlockEntity(pos));
		for (Direction direction : Direction.values()) {
			BlockPos blockPos = pos.offset(direction);
			Block block = world.getBlockState(blockPos).getBlock();
			if (block instanceof AbstractCableBlock<?> abstractCableBlock) {
				abstractCableBlock.recalculateDirection(world, blockPos, direction, true);
			}
		}
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		// System.out.println("BREAK: " + world.getBlockEntity(pos));
		for (Direction direction : Direction.values()) {
			BlockPos blockPos = pos.offset(direction);
			Block block = world.getBlockState(blockPos).getBlock();
			if (block instanceof AbstractCableBlock<?> abstractCableBlock) {
				abstractCableBlock.recalculateDirection(world, blockPos, direction, false);
			}
		}
		super.onBreak(world, pos, state, player);
	}
}
