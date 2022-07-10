package de.flow.test.redstone.blocks;

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
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.concurrent.atomic.AtomicInteger;

public class RedstoneEmitterBlockEntity extends BlockEntity implements NetworkBlock {
	@Getter
	private int redstonePower = 0;
	@RegisterToNetwork
	private Output<AtomicInteger> consumedPower = new DefaultOutput<>(() -> new AtomicInteger(15), value -> redstonePower = value.get(), Unit.numberUnit(Utils.REDSTONE_TYPE, 1));
	public RedstoneEmitterBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(RedstoneBlockEntityInit.EMITTER_ENTITY, blockPos, blockState);
	}

	public static void tick(World world, BlockPos blockPos, BlockState blockState, RedstoneEmitterBlockEntity entity) {
		blockState = blockState.with(Properties.POWERED, entity.redstonePower > 0);
		world.setBlockState(blockPos, blockState);
		entity.redstonePower = 0;
	}
}
