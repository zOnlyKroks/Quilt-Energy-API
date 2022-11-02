package de.flow2.api.machines;

import de.flow2.api.Type;
import de.flow2.api.cables.CableBlock;
import de.flow2.api.networks.Network;
import de.flow2.impl.NetworkManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

// TODO: Add JavaDoc
public interface MachineBlock {

	default void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		List<Network<?>> networks = new ArrayList<>();
		MachineEntity machineEntity = (MachineEntity) world.getBlockEntity(pos);
		for (Direction direction : machineEntity != null ? machineEntity.ports() : Direction.values()) {
			BlockPos blockPos = pos.offset(direction);
			Block block = world.getBlockState(blockPos).getBlock();
			if (block instanceof CableBlock cableBlock && machineEntity != null) {
				for (Type<?> type : cableBlock.types()) {
					if (machineEntity.hasType(type)) {
						cableBlock.recalculateDirection(world, blockPos, direction, true);
						if (!world.isClient) {
							networks.add(NetworkManager.INSTANCE.get(type, world, blockPos));
						}
					}
				}
			}
		}
		networks.forEach(network -> {
			if (network == null) return;
			network.add(machineEntity);
		});
	}

	default void breakBlock(World world, BlockPos pos, BlockState state) {
		List<Network<?>> networks = new ArrayList<>();
		MachineEntity machineEntity = (MachineEntity) world.getBlockEntity(pos);
		for (Direction direction : machineEntity != null ? machineEntity.ports() : Direction.values()) {
			BlockPos blockPos = pos.offset(direction);
			Block block = world.getBlockState(blockPos).getBlock();
			if (block instanceof CableBlock cableBlock && machineEntity != null) {
				for (Type<?> type : cableBlock.types()) {
					if (machineEntity.hasType(type)) {
						cableBlock.recalculateDirection(world, blockPos, direction, false);
						if (!world.isClient) {
							networks.add(NetworkManager.INSTANCE.get(type, world, blockPos));
						}
					}
				}
			}
		}
		networks.forEach(network -> {
			if (network == null) return;
			network.remove(machineEntity);
		});
	}

	default void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		breakBlock(world, pos, state);
	}

	default void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
		breakBlock(world, pos, world.getBlockState(pos));
	}
}
