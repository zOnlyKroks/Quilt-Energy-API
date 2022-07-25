package de.flow.test.energy.solarpanel;

import com.google.common.util.concurrent.AtomicDouble;
import de.flow.api.NetworkBlock;
import de.flow.api.RegisterToNetwork;
import de.flow.api.Unit;
import de.flow.test.energy.EnergyBlockEntityInit;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SolarPanelEntity extends BlockEntity implements NetworkBlock {

	@Getter
	private double storedAmount = 0;

	private static final Direction[] PORTS = new Direction[] { Direction.DOWN };

	@Override
	public Direction[] ports() {
		return PORTS;
	}

	public static void tick(World world, BlockPos pos, BlockState state, SolarPanelEntity solarPanelEntity) {
		long timeOfDay = world.getTimeOfDay() % 24000;
		if (world.isSkyVisible(pos.up()) && timeOfDay > 0 && timeOfDay <= 12000) {
			double sunlight = timeOfDay / 1000.0;

			double solarFactor = (-0.12) * ((sunlight - 6) * (sunlight - 6)) + 5;
			solarPanelEntity.storedAmount = solarFactor * 10;
			world.updateComparators(pos, state.getBlock());
		} else if (solarPanelEntity.storedAmount != 0) {
			solarPanelEntity.storedAmount = 0;
			world.updateComparators(pos, state.getBlock());
		}
	}

	@RegisterToNetwork
	private Input<AtomicDouble> input = new LimitedInput<>(() -> new AtomicDouble(storedAmount), aDouble -> storedAmount -= aDouble.get(), Unit.energyUnit(1), new AtomicDouble(600.0));

	public SolarPanelEntity(BlockPos blockPos, BlockState blockState) {
		super(EnergyBlockEntityInit.SOLAR_PANEL_ENTITY, blockPos, blockState);
	}
}