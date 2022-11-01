package de.flow2.api.machines;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

// TODO: Add JavaDoc
public interface MachineBlock {

	default void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		// TODO: Implement
	}

	default void breakBlock(World world, BlockPos pos, BlockState state) {
		// TODO: Implement
	}

	default void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		breakBlock(world, pos, state);
	}

	default void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
		breakBlock(world, pos, world.getBlockState(pos));
	}
}
