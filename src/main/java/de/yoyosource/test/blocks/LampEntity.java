package de.yoyosource.test.blocks;

import de.yoyosource.energy.api.EnergyOutput;
import de.yoyosource.energy.api.EnergyUnit;
import de.yoyosource.test.ModInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class LampEntity extends BlockEntity implements EnergyOutput {

	// public static IntProperty LIGHT_LEVEL = IntProperty.of("lightLevel", 0, 15);
	public static IntProperty LIGHT_LEVEL = Properties.LEVEL_15;

	private double gotAmount = 0;

	public static void tick(World world, BlockPos pos, BlockState state, LampEntity lampEntity) {
		state = state.with(LIGHT_LEVEL, Math.max(Math.min((int) lampEntity.gotAmount, 15), 0));
		world.setBlockState(pos, state);
		markDirty(world, pos, state);
		lampEntity.gotAmount = 0;
	}

	@Override
	public double desiredAmount() {
		return 15;
	}

	@Override
	public void provide(double amount) {
		gotAmount = amount;
	}

	@Override
	public EnergyUnit unit() {
		return EnergyUnit.BASE_UNIT;
	}

	public LampEntity(BlockPos blockPos, BlockState blockState) {
		super(ModInit.LAMP_ENTITY, blockPos, blockState);
		ModInit.network.add(this);
	}
}
