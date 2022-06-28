package de.yoyosource.test.blocks;

import de.yoyosource.energy.api.EnergyInput;
import de.yoyosource.energy.api.EnergyOutput;
import de.yoyosource.energy.api.EnergyUnit;
import de.yoyosource.test.ModInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BatteryEntity extends BlockEntity implements EnergyInput, EnergyOutput {

	private double storedAmount = 0;

	public static void tick(World world, BlockPos pos, BlockState state, BatteryEntity lampEntity) {
		lampEntity.setWorld(world);
	}

	@Override
	public double extractableAmount() {
		return Math.min(storedAmount, 600);
	}

	@Override
	public void extract(double amount) {
		storedAmount -= amount;
	}

	@Override
	public double desiredAmount() {
		return Math.min(500000 - storedAmount, 600);
	}

	@Override
	public void provide(double amount) {
		storedAmount += amount;
	}

	@Override
	public EnergyUnit unit() {
		return EnergyUnit.BASE_UNIT;
	}

	public BatteryEntity(BlockPos blockPos, BlockState blockState) {
		super(ModInit.BATTERY_ENTITY, blockPos, blockState);
	}

	@Override
	public void setWorld(World world) {
		if (getWorld() != null) return;
		super.setWorld(world);
		if (!world.isClient()) {
			ModInit.network.add(this);
		}
	}
}
