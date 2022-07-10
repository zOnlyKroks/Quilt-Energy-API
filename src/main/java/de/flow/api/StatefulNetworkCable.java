package de.flow.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface StatefulNetworkCable<C> extends NetworkCable<C> {
	void changeCableState(World world, BlockPos blockPos, BlockState blockState, C availableAmount);
}
