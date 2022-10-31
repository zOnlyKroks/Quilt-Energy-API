package de.flow2.api.machines;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

// TODO: Add JavaDoc
public interface NetworkEntity {

	default Direction[] ports() {
		return Direction.values();
	}

	BlockPos getPos();

	World getWorld();

}
