package de.flow.test.redstone.blocks;

import de.flow.api.NetworkBlock;
import de.flow.api.RegisterToNetwork;
import de.flow.api.Unit;
import de.flow.api.Utils;
import de.flow.test.redstone.RedstoneBlockEntityInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.concurrent.atomic.AtomicInteger;

public class RedstoneAcceptorBlockEntity extends BlockEntity implements NetworkBlock {
	private int redstonePower = 0;
	@RegisterToNetwork
	private Input<AtomicInteger> providedPower = new DefaultInput<>(() -> new AtomicInteger(redstonePower), value -> {}, Unit.numberUnit(Utils.REDSTONE_TYPE, 1));
	public RedstoneAcceptorBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(RedstoneBlockEntityInit.ACCEPTOR_ENTITY, blockPos, blockState);
	}

	public static void tick(World world, BlockPos blockPos, BlockState blockState, RedstoneAcceptorBlockEntity entity) {
		int redstonePower = 0;
		for (Direction direction : Direction.values()) {
			redstonePower = Math.max(world.getEmittedRedstonePower(blockPos, direction), redstonePower);
		}
		entity.redstonePower = redstonePower;
		blockState = blockState.with(Properties.POWERED, redstonePower > 0);
		world.setBlockState(blockPos, blockState);
	}
}
