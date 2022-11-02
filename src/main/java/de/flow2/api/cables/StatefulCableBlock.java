package de.flow2.api.cables;

import de.flow2.api.Type;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.Serializable;

// TODO: Add JavaDoc
public interface StatefulCableBlock extends CableBlock {

	/**
	 * Change the block state of the cable based on the available amount in the current tick.
	 *
	 * @param world the world
	 * @param blockPos the block position
	 * @param blockState the current block state
	 * @param availableAmount the available amount in the current tick
	 */
	<T extends Serializable> void changeCableState(World world, BlockPos blockPos, BlockState blockState, Type<T> type, T availableAmount);
}
