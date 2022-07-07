package de.flow.test.blocks;

import com.google.common.util.concurrent.AtomicDouble;
import de.flow.api.NetworkBlock;
import de.flow.api.RegisterToNetwork;
import de.flow.api.Unit;
import de.flow.test.BlockEntityInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SolarPanelEntity extends BlockEntity implements NetworkBlock {

	private double storedAmount = 0;

	private static final Direction[] PORTS = new Direction[] { Direction.DOWN };

	@Override
	public Direction[] ports() {
		return PORTS;
	}

	public static void tick(World world, BlockPos pos, BlockState state, SolarPanelEntity solarPanelEntity) {
		if (world.isSkyVisible(pos.up()) && world.getTimeOfDay() > 0 && world.getTimeOfDay() <= 12000) {
			double sunlight = Math.min(Math.max(world.getTimeOfDay(), 0), 12000) / 1000.0;

			double solarFactor = (-0.12) * ((sunlight - 6) * (sunlight - 6)) + 5;
			solarPanelEntity.storedAmount = solarFactor * 10;
		}
	}

	@RegisterToNetwork
	private Input<AtomicDouble> input = new LimitedDefaultInput<>(() -> new AtomicDouble(storedAmount), aDouble -> storedAmount += aDouble.get(), Unit.energyUnit(1), new AtomicDouble(600.0));

	public SolarPanelEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityInit.SOLAR_PANEL_ENTITY, blockPos, blockState);
	}
}
