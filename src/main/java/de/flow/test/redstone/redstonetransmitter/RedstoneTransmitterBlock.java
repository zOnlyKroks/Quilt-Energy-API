package de.flow.test.redstone.redstonetransmitter;

import de.flow.api.AbstractNetworkBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

public class RedstoneTransmitterBlock extends AbstractNetworkBlock {

	static {
		RedstoneTransmitterEntity.onInitialize();
	}

	public RedstoneTransmitterBlock() {
		super(QuiltBlockSettings.of(Material.METAL).collidable(true).strength(6).hardness(6).requiresTool());
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new RedstoneTransmitterEntity(pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.BLOCK;
	}
}