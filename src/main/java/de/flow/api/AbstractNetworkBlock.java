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

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNetworkBlock extends BlockWithEntity {

	protected AbstractNetworkBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		List<Network<?>> networks = new ArrayList<>();
		for (Direction direction : Direction.values()) {
			BlockPos blockPos = pos.offset(direction);
			Block block = world.getBlockState(blockPos).getBlock();
			if (block instanceof AbstractCableBlock<?> abstractCableBlock) {
				abstractCableBlock.recalculateDirection(world, blockPos, direction, true);
				if (!world.isClient) {
					networks.add(Networks.get(world, blockPos));
				}
			}
		}
		NetworkBlock networkBlock = (NetworkBlock) world.getBlockEntity(pos);
		networks.forEach(network -> {
			if (network == null) return;
			network.add(networkBlock);
		});
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		List<Network<?>> networks = new ArrayList<>();
		for (Direction direction : Direction.values()) {
			BlockPos blockPos = pos.offset(direction);
			Block block = world.getBlockState(blockPos).getBlock();
			if (block instanceof AbstractCableBlock<?> abstractCableBlock) {
				abstractCableBlock.recalculateDirection(world, blockPos, direction, false);
				if (!world.isClient) {
					networks.add(Networks.get(world, blockPos));
				}
			}
		}
		NetworkBlock networkBlock = (NetworkBlock) world.getBlockEntity(pos);
		networks.forEach(network -> {
			if (network == null) return;
			network.remove(networkBlock);
		});
		super.onBreak(world, pos, state, player);
	}
}
