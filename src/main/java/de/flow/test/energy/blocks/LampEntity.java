package de.flow.test.energy.blocks;

import com.google.common.util.concurrent.AtomicDouble;
import de.flow.api.NetworkBlock;
import de.flow.api.RegisterToNetwork;
import de.flow.api.Unit;
import de.flow.test.energy.EnergyBlockEntityInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LampEntity extends BlockEntity implements NetworkBlock {

	public static IntProperty LIGHT_LEVEL = IntProperty.of("light_level", 0, 15);

	private double gotAmount = 0;

	public static void tick(World world, BlockPos pos, BlockState state, LampEntity lampEntity) {
		state = state.with(LIGHT_LEVEL, Math.max(Math.min((int) lampEntity.gotAmount, 15), 0));
		world.setBlockState(pos, state);
		markDirty(world, pos, state);
		lampEntity.gotAmount = 0;
	}

	@RegisterToNetwork
	public Output<AtomicDouble> output = new DefaultOutput<>(() -> new AtomicDouble(15.0), aDouble -> gotAmount = aDouble.get(), Unit.energyUnit(1));

	public LampEntity(BlockPos blockPos, BlockState blockState) {
		super(EnergyBlockEntityInit.LAMP_ENTITY, blockPos, blockState);
	}
}
