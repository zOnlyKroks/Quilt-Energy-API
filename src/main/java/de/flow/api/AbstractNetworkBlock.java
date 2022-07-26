package de.flow.api;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractNetworkBlock extends BlockWithEntity implements INetworkBlock {

	protected AbstractNetworkBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		INetworkBlock.super.onPlaced(world, pos, state, placer, itemStack);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		INetworkBlock.super.onBreak(world, pos, state, player);
		super.onBreak(world, pos, state, player);
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
		INetworkBlock.super.onDestroyedByExplosion(world, pos, explosion);
		super.onDestroyedByExplosion(world, pos, explosion);
	}
}
