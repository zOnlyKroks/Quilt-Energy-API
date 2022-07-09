package de.flow.test.blocks;

import com.google.common.util.concurrent.AtomicDouble;
import de.flow.api.NetworkBlock;
import de.flow.api.RegisterToNetwork;
import de.flow.api.Unit;
import de.flow.test.BlockEntityInit;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class BatteryEntity extends BlockEntity implements NetworkBlock {

	@Getter
	private double storedAmount = 0;

	private Unit<AtomicDouble> unit = Unit.energyUnit(1);

	@RegisterToNetwork
	private Store<AtomicDouble> store = new DefaultStore<>(
			new LimitedDefaultInput<>(() -> new AtomicDouble(500000 - storedAmount), aDouble -> storedAmount -= aDouble.get(), unit, new AtomicDouble(600.0)),
			new LimitedDefaultOutput<>(() -> new AtomicDouble(storedAmount), aDouble -> storedAmount += aDouble.get(), unit, new AtomicDouble(600.0))
	);

	public BatteryEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityInit.BATTERY_ENTITY, blockPos, blockState);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		if (nbt.contains("storedAmount")) {
			storedAmount = nbt.getDouble("storedAmount");
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		nbt.putDouble("storedAmount", storedAmount);
	}
}
