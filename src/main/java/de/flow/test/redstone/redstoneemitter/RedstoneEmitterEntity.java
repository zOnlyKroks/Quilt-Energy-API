package de.flow.test.redstone.redstoneemitter;

import de.flow.api.NetworkBlock;
import de.flow.api.RegisterToNetwork;
import de.flow.api.Unit;
import de.flow.api.Utils;
import de.flow.test.redstone.RedstoneBlockEntityInit;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.concurrent.atomic.AtomicInteger;

public class RedstoneEmitterEntity extends BlockEntity implements NetworkBlock {
	@Getter
	private int redstonePower = 0;

	@RegisterToNetwork
	private Output<AtomicInteger> consumedPower = new DefaultOutput<>(() -> new AtomicInteger(15), value -> redstonePower = value.get(), Unit.numberUnit(Utils.REDSTONE_TYPE, 1));

	public RedstoneEmitterEntity(BlockPos blockPos, BlockState blockState) {
		super(RedstoneBlockEntityInit.EMITTER_ENTITY, blockPos, blockState);
	}

	public static void tick(World world, BlockPos blockPos, BlockState blockState, RedstoneEmitterEntity entity) {
		world.setBlockState(blockPos, blockState.with(Properties.POWERED, entity.redstonePower > 0), 2);
		world.updateNeighbors(blockPos, blockState.getBlock());
		entity.redstonePower = 0;
	}
}
