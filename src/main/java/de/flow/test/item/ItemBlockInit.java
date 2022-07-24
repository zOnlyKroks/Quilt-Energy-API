package de.flow.test.item;

import de.flow.FlowApi;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class ItemBlockInit {

	private static Block registerBlock(String id, Block value) {
		return Registry.register(Registry.BLOCK, new Identifier(FlowApi.MODID, id), value);
	}

	private static BlockItem registerBlockItem(String id, Block value) {
		return Registry.register(Registry.ITEM, new Identifier(FlowApi.MODID, id), new BlockItem(value, new QuiltItemSettings().group(FlowApi.ITEM_GROUP)));
	}

	public static void onInitialize() {

	}
}
