package de.flow.test.energy.blocks;

import com.google.common.util.concurrent.AtomicDouble;
import de.flow.api.*;
import de.flow.test.energy.EnergyBlockEntityInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.concurrent.atomic.AtomicInteger;

public class EnergyTransmitterEntity extends BlockEntity implements NetworkBlock {

	private static Store<AtomicDouble> transmitterStore = null;
	private static double transmitterAmount = 0;

	private boolean noOutput = false;
	private boolean redstoneNoOutput = false;

	public static void tick(World world, BlockPos pos, BlockState state, EnergyTransmitterEntity energyTransmitterEntity) {
		energyTransmitterEntity.noOutput = energyTransmitterEntity.redstoneNoOutput;
		energyTransmitterEntity.redstoneNoOutput = false;
	}

	public static void onInitialize() {
		Unit<AtomicDouble> unit = Unit.energyUnit(1);
		Networks.loadCallback(() -> {
			System.out.println("Loading energy transmitter store");
			transmitterStore = new DefaultStore<>(
					new LimitedInput<>(() -> new AtomicDouble(transmitterAmount), aDouble -> transmitterAmount -= aDouble.get(), unit, new AtomicDouble(20000.0)),
					new LimitedOutput<>(() -> new AtomicDouble(20000 - transmitterAmount), aDouble -> transmitterAmount += aDouble.get(), unit, new AtomicDouble(20000.0))
			);
		});
		Networks.unloadCallback(() -> {
			System.out.println("Unloading energy transmitter store");
			transmitterStore = null;
		});
	}

	public EnergyTransmitterEntity(BlockPos blockPos, BlockState blockState) {
		super(EnergyBlockEntityInit.ENERGY_TRANSMITTER_ENTITY, blockPos, blockState);
	}

	@RegisterToNetwork
	private Store<AtomicDouble> store = new LockableStore<>(transmitterStore, () -> noOutput);

	@RegisterToNetwork
	private Output<AtomicInteger> redstoneOutput = new DefaultOutput<>(() -> new AtomicInteger(1), value -> redstoneNoOutput = value.get() > 0, Unit.numberUnit(Utils.REDSTONE_TYPE, 1));
}
