package de.flow.test.blocks;

import de.flow.api.EnergyOutput;
import de.flow.api.EnergyUnit;
import de.flow.test.BlockEntityInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LampEntity extends BlockEntity implements EnergyOutput {

	public static IntProperty LIGHT_LEVEL = IntProperty.of("light_level", 0, 15);

	private double gotAmount = 0;

	public static void tick(World world, BlockPos pos, BlockState state, LampEntity lampEntity) {
		lampEntity.setWorld(world);
		if (!world.isClient) {
			state = state.with(LIGHT_LEVEL, Math.max(Math.min((int) lampEntity.gotAmount, 15), 0));
			world.setBlockState(pos, state);
			markDirty(world, pos, state);
			lampEntity.gotAmount = 0;
		}
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
		super(BlockEntityInit.LAMP_ENTITY, blockPos, blockState);
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
