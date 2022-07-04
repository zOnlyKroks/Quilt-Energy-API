package de.flow.test.blocks;

import com.google.common.util.concurrent.AtomicDouble;
import de.flow.api.NetworkBlock;
import de.flow.api.RegisterToNetwork;
import de.flow.api.Unit;
import de.flow.test.BlockEntityInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SolarPanelEntity extends BlockEntity implements NetworkBlock {

	private double storedAmount = 0;

	public static void tick(World world, BlockPos pos, BlockState state, SolarPanelEntity solarPanelEntity) {
		solarPanelEntity.setWorld(world);
		if (world.isSkyVisible(pos.up()) && world.getTimeOfDay() > 0 && world.getTimeOfDay() <= 12000) {
			double sunlight = Math.min(Math.max(world.getTimeOfDay(), 0), 12000) / 1000.0;

			double solarFactor = (-0.12) * ((sunlight - 6) * (sunlight - 6)) + 5;
			solarPanelEntity.storedAmount = solarFactor * 10;
		}

		// System.out.println("ENERGY: " + solarPanelEntity.storedAmount);
	}

	@RegisterToNetwork
	private Input<Double, AtomicDouble> input = new LimitedDefaultInput<>(() -> storedAmount, aDouble -> storedAmount += aDouble, Unit.energyUnit(1), 600.0);

	public SolarPanelEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityInit.SOLAR_PANEL_ENTITY, blockPos, blockState);
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
