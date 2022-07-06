package de.flow.test.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

public class CableBlock extends Block {

	public static final BooleanProperty NORTH = BooleanProperty.of("north");
	public static final BooleanProperty EAST = BooleanProperty.of("east");
	public static final BooleanProperty SOUTH = BooleanProperty.of("south");
	public static final BooleanProperty WEST = BooleanProperty.of("west");
	public static final BooleanProperty UP = BooleanProperty.of("up");
	public static final BooleanProperty DOWN = BooleanProperty.of("down");

	public CableBlock() {
		super(QuiltBlockSettings.of(Material.METAL).collidable(true).strength(6).hardness(6).requiresTool());
		this.setDefaultState(this.getStateManager().getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(NORTH);
		builder.add(EAST);
		builder.add(SOUTH);
		builder.add(WEST);
		builder.add(UP);
		builder.add(DOWN);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false);
	}
}
