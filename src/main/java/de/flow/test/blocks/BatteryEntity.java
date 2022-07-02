package de.flow.test.blocks;

import com.google.common.util.concurrent.AtomicDouble;
import de.flow.api.*;
import de.flow.test.BlockEntityInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class BatteryEntity extends BlockEntity implements Inputs, Outputs {

	private double storedAmount = 0;

	public static void tick(World world, BlockPos pos, BlockState state, BatteryEntity lampEntity) {
		lampEntity.setWorld(world);
	}

	@RegisterToNetwork
	private Store<Double, AtomicDouble> store = new Store<>() {
		@Override
		public Double extractableAmount() {
			return Math.min(storedAmount, 600);
		}

		@Override
		public void extract(Double amount) {
			storedAmount -= amount;
		}

		@Override
		public Double desiredAmount() {
			return Math.min(500000 - storedAmount, 600);
		}

		@Override
		public void provide(Double amount) {
			storedAmount += amount;
		}

		@Override
		public Unit<Double, AtomicDouble> unit() {
			return Unit.energyUnit(1);
		}
	};

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
			BlockEntityInit.network.iterate(this, BlockEntityInit.network::add);
		}
	}
}
