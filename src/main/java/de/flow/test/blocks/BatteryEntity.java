package de.flow.test.blocks;

import com.google.common.util.concurrent.AtomicDouble;
import de.flow.api.NetworkBlock;
import de.flow.api.RegisterToNetwork;
import de.flow.api.Unit;
import de.flow.test.BlockEntityInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BatteryEntity extends BlockEntity implements NetworkBlock {

	private double storedAmount = 0;

	public static void tick(World world, BlockPos pos, BlockState state, BatteryEntity lampEntity) {
		lampEntity.setWorld(world);
	}

	@RegisterToNetwork
	private Store<Double, AtomicDouble> store = new DefaultStore<>(
			new LimitedDefaultInput<>(() -> 500000 - storedAmount, aDouble -> storedAmount += aDouble, Unit.energyUnit(1), 600.0),
			new LimitedDefaultOutput<>(() -> storedAmount, aDouble -> storedAmount -= aDouble, Unit.energyUnit(1), 600.0)
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

	@Override
	public void setWorld(World world) {
		if (getWorld() != null) return;
		super.setWorld(world);
		if (!world.isClient()) {
			BlockEntityInit.network.add(this);
		}
	}
}
