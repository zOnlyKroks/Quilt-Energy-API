package de.flow.test;

import de.flow.FlowApi;
import de.flow.test.blocks.*;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class BlockInit {
	public static final Block SOLAR_PANEL_BLOCK = new SolarPanelBlock();

	public static final Block LAMP_BLOCK = new LampBlock();

	public static final Block BATTERY_BLOCK = new BatteryBlock();

	static {
		Registry.register(Registry.BLOCK, new Identifier("quilt-flow-api", "solar_panel_block"), SOLAR_PANEL_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("quilt-flow-api", "solar_panel_block"), new BlockItem(SOLAR_PANEL_BLOCK, new QuiltItemSettings().group(FlowApi.ITEM_GROUP)));

		Registry.register(Registry.BLOCK, new Identifier("quilt-flow-api", "lamp_block"), LAMP_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("quilt-flow-api", "lamp_block"), new BlockItem(LAMP_BLOCK, new QuiltItemSettings().group(FlowApi.ITEM_GROUP)));

		Registry.register(Registry.BLOCK, new Identifier("quilt-flow-api", "battery_block"), BATTERY_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("quilt-flow-api", "battery_block"), new BlockItem(BATTERY_BLOCK, new QuiltItemSettings().group(FlowApi.ITEM_GROUP)));
	}

	public static void onInitialize() {

	}
}
