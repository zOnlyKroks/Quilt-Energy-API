package de.flow.test.redstone.blocks;

import de.flow.api.*;
import de.flow.test.redstone.RedstoneBlockEntityInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.atomic.AtomicInteger;

public class RedstoneTransmitterEntity extends BlockEntity implements NetworkBlock {

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

	public RedstoneTransmitterEntity(BlockPos blockPos, BlockState blockState) {
		super(RedstoneBlockEntityInit.REDSTONE_TRANSMITTER_ENTITY, blockPos, blockState);
	}

	@RegisterToNetwork
	private Transmitter<AtomicInteger> transmitter = new DefaultTransmitter<>(transmitterIdentifier, Unit.numberUnit(Utils.REDSTONE_TYPE, 1));
}
