package de.flow2.api.machines;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

// TODO: Add JavaDoc
public abstract class AbstractNetworkEntity extends BlockEntity implements NetworkEntity {

	protected AbstractNetworkEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}
}
