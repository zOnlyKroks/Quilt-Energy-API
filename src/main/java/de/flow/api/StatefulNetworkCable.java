package de.flow.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Used to change the cable appearance based on the available amount in the current tick.
 *
 * @param <C> generic-type of the network cable
 */
public interface StatefulNetworkCable<C> extends NetworkCable<C> {

	/**
	 * Change the block state of the cable based on the available amount in the current tick.
	 *
	 * @param world the world
	 * @param blockPos the block position
	 * @param blockState the current block state
	 * @param availableAmount the available amount in the current tick
	 */
	void changeCableState(World world, BlockPos blockPos, BlockState blockState, C availableAmount);
}
