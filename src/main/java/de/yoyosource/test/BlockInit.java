package de.yoyosource.test;

import de.yoyosource.energy.impl.Network;
import de.yoyosource.test.blocks.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import org.quiltmc.qsl.lifecycle.api.event.ServerTickEvents;

public class BlockInit {
	public static final Block SOLAR_PANEL_BLOCK = new SolarPanelBlock();

	public static final Block LAMP_BLOCK = new LampBlock();

	public static final Block BATTERY_BLOCK = new BatteryBlock();

	static {
		Registry.register(Registry.BLOCK, new Identifier("quilt-energy-api", "solar_panel_block"), SOLAR_PANEL_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("quilt-energy-api", "solar_panel_block"), new BlockItem(SOLAR_PANEL_BLOCK, new QuiltItemSettings().group(ItemGroup.MISC)));

		Registry.register(Registry.BLOCK, new Identifier("quilt-energy-api", "lamp_block"), LAMP_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("quilt-energy-api", "lamp_block"), new BlockItem(LAMP_BLOCK, new QuiltItemSettings().group(ItemGroup.MISC)));

		Registry.register(Registry.BLOCK, new Identifier("quilt-energy-api", "battery_block"), BATTERY_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("quilt-energy-api", "battery_block"), new BlockItem(BATTERY_BLOCK, new QuiltItemSettings().group(ItemGroup.MISC)));
	}

	public static void onInitialize() {

	}
}
