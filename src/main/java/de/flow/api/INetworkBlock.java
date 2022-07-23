package de.flow.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface INetworkBlock {

	default void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		List<Network<?>> networks = new ArrayList<>();
		NetworkBlock networkBlock = (NetworkBlock) world.getBlockEntity(pos);
		for (Direction direction : networkBlock != null ? networkBlock.ports() : Direction.values()) {
			BlockPos blockPos = pos.offset(direction);
			Block block = world.getBlockState(blockPos).getBlock();
			if (block instanceof AbstractCableBlock<?> abstractCableBlock && networkBlock.hasType(abstractCableBlock.type())) {
				abstractCableBlock.recalculateDirection(world, blockPos, direction, true);
				if (!world.isClient) {
					networks.add(Networks.get(world, blockPos));
				}
			}
		}
		networks.forEach(network -> {
			if (network == null) return;
			network.add(networkBlock);
		});
	}

	default void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		List<Network<?>> networks = new ArrayList<>();
		NetworkBlock networkBlock = (NetworkBlock) world.getBlockEntity(pos);
		for (Direction direction : networkBlock != null ? networkBlock.ports() : Direction.values()) {
			BlockPos blockPos = pos.offset(direction);
			Block block = world.getBlockState(blockPos).getBlock();
			if (block instanceof AbstractCableBlock<?> abstractCableBlock && networkBlock.hasType(abstractCableBlock.type())) {
				abstractCableBlock.recalculateDirection(world, blockPos, direction, false);
				if (!world.isClient) {
					networks.add(Networks.get(world, blockPos));
				}
			}
		}
		networks.forEach(network -> {
			if (network == null) return;
			network.remove(networkBlock);
		});
	}
}
