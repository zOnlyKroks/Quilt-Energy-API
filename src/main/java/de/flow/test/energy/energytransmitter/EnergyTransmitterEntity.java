package de.flow.test.energy.energytransmitter;

import com.google.common.util.concurrent.AtomicDouble;
import de.flow.api.*;
import de.flow.test.energy.EnergyBlockEntityInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.concurrent.atomic.AtomicInteger;

public class EnergyTransmitterEntity extends BlockEntity implements NetworkBlock {

	private static TransmitterIdentifier transmitterIdentifier;

	public static void onInitialize() {
		Networks.loadCallback(() -> {
			transmitterIdentifier = new TransmitterIdentifier() {
			};
		});
		Networks.unloadCallback(() -> {
			transmitterIdentifier = null;
		});
	}

	private boolean noOutput = false;
	private boolean redstoneNoOutput = false;

	public static void tick(World world, BlockPos pos, BlockState state, EnergyTransmitterEntity energyTransmitterEntity) {
		energyTransmitterEntity.noOutput = energyTransmitterEntity.redstoneNoOutput;
		energyTransmitterEntity.redstoneNoOutput = false;
	}

	public EnergyTransmitterEntity(BlockPos blockPos, BlockState blockState) {
		super(EnergyBlockEntityInit.ENERGY_TRANSMITTER_ENTITY, blockPos, blockState);
	}

	@RegisterToNetwork
	private Transmitter<AtomicDouble> transmitter = new DefaultTransmitter<>(transmitterIdentifier, Unit.energyUnit(1), new AtomicDouble(600), () -> noOutput);

	@RegisterToNetwork
	private Output<AtomicInteger> redstoneOutput = new DefaultOutput<>(() -> new AtomicInteger(1), value -> redstoneNoOutput = value.get() > 0, Unit.numberUnit(Utils.REDSTONE_TYPE, 1));
}
