package de.flow.test.item;

import de.flow.FlowApi;
import de.flow.test.item.blocks.ItemCableBlock;
import de.flow.test.item.blocks.ItemInputBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class ItemBlockInit {

	public static final Block CABLE_BLOCK;
	public static final BlockItem CABLE_ITEM;

	public static final Block ITEM_INPUT_BLOCK;
	public static final BlockItem ITEM_INPUT_ITEM;

	static {
		CABLE_BLOCK = registerBlock("item_cable_block", new ItemCableBlock());
		CABLE_ITEM = registerBlockItem("item_cable_block", CABLE_BLOCK);

		ITEM_INPUT_BLOCK = registerBlock("item_input_block", new ItemInputBlock());
		ITEM_INPUT_ITEM = registerBlockItem("item_input_block", ITEM_INPUT_BLOCK);
	}

	private static Block registerBlock(String id, Block value) {
		return Registry.register(Registry.BLOCK, new Identifier(FlowApi.MODID, id), value);
	}

	private static BlockItem registerBlockItem(String id, Block value) {
		return Registry.register(Registry.ITEM, new Identifier(FlowApi.MODID, id), new BlockItem(value, new QuiltItemSettings().group(FlowApi.ITEM_GROUP)));
	}

	public static void onInitialize() {

	}
}
