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

	public static final BooleanProperty CONNECTION_NORTH = BooleanProperty.of("connection_north");
	public static final BooleanProperty CONNECTION_EAST = BooleanProperty.of("connection_east");
	public static final BooleanProperty CONNECTION_SOUTH = BooleanProperty.of("connection_south");
	public static final BooleanProperty CONNECTION_WEST = BooleanProperty.of("connection_west");
	public static final BooleanProperty CONNECTION_UP = BooleanProperty.of("connection_up");
	public static final BooleanProperty CONNECTION_DOWN = BooleanProperty.of("connection_down");

	public CableBlock() {
		super(QuiltBlockSettings.of(Material.METAL).collidable(true).strength(6).hardness(6).requiresTool());
		this.setDefaultState(this.getStateManager().getDefaultState().with(CONNECTION_NORTH, false).with(CONNECTION_EAST, false).with(CONNECTION_SOUTH, false).with(CONNECTION_WEST, false).with(CONNECTION_UP, false).with(CONNECTION_DOWN, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(CONNECTION_NORTH);
		builder.add(CONNECTION_EAST);
		builder.add(CONNECTION_SOUTH);
		builder.add(CONNECTION_WEST);
		builder.add(CONNECTION_UP);
		builder.add(CONNECTION_DOWN);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(CONNECTION_NORTH, false).with(CONNECTION_EAST, false).with(CONNECTION_SOUTH, false).with(CONNECTION_WEST, false).with(CONNECTION_UP, false).with(CONNECTION_DOWN, false);
	}
}
