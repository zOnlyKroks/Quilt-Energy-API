package de.flow.test.energy.blocks;

import com.google.common.util.concurrent.AtomicDouble;
import de.flow.api.NetworkBlock;
import de.flow.api.RegisterToNetwork;
import de.flow.api.Unit;
import de.flow.test.energy.EnergyBlockEntityInit;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BatteryEntity extends BlockEntity implements NetworkBlock {

	@Getter
	private double storedAmount = 0;
	private boolean noOutput = false;

	public static void tick(World world, BlockPos pos, BlockState state, BatteryEntity batteryEntity) {
		batteryEntity.noOutput = world.isReceivingRedstonePower(pos);
	}

	private Unit<AtomicDouble> unit = Unit.energyUnit(1);

	@RegisterToNetwork
	private Store<AtomicDouble> store = new ComparatorUpdateStore<>(
			this,
			new LockableInput<>(new LimitedInput<>(() -> new AtomicDouble(storedAmount), aDouble -> storedAmount -= aDouble.get(), unit, new AtomicDouble(600.0)), () -> noOutput),
			new LimitedOutput<>(() -> new AtomicDouble(500000 - storedAmount), aDouble -> storedAmount += aDouble.get(), unit, new AtomicDouble(600.0))
	);

	public BatteryEntity(BlockPos blockPos, BlockState blockState) {
		super(EnergyBlockEntityInit.BATTERY_ENTITY, blockPos, blockState);
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
