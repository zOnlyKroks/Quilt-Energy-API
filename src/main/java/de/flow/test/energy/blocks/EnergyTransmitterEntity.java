package de.flow.test.energy.blocks;

import com.google.common.util.concurrent.AtomicDouble;
import de.flow.api.NetworkBlock;
import de.flow.api.Networks;
import de.flow.api.RegisterToNetwork;
import de.flow.api.Unit;
import de.flow.test.energy.EnergyBlockEntityInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyTransmitterEntity extends BlockEntity implements NetworkBlock {

	private static Store<AtomicDouble> transmitterStore = null;
	private static double transmitterAmount = 0;

	static {
		Unit<AtomicDouble> unit = Unit.energyUnit(1);
		Networks.loadCallback(() -> {
			new DefaultStore<>(
					new LimitedInput<>(() -> new AtomicDouble(transmitterAmount), aDouble -> transmitterAmount -= aDouble.get(), unit, new AtomicDouble(20000.0)),
					new LimitedOutput<>(() -> new AtomicDouble(20000 - transmitterAmount), aDouble -> transmitterAmount += aDouble.get(), unit, new AtomicDouble(600.0))
			);
		});
		Networks.unloadCallback(() -> {
			transmitterStore = null;
		});
	}

	public EnergyTransmitterEntity(BlockPos blockPos, BlockState blockState) {
		super(EnergyBlockEntityInit.ENERGY_TRANSMITTER_ENTITY, blockPos, blockState);
	}

	@RegisterToNetwork
	private Store<AtomicDouble> store = transmitterStore;

	public static void tick(World world, BlockPos pos, BlockState state, EnergyTransmitterEntity energyTransmitterEntity) {
		transmitterAmount = 0;
	}
}
