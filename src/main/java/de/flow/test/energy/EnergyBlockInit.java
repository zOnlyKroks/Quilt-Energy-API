package de.flow.test.energy;

import de.flow.FlowApi;
import de.flow.test.energy.blocks.*;
import de.flow.test.energy.blocks.EnergyCableBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class EnergyBlockInit {
	public static final Block SOLAR_PANEL_BLOCK;
	public static final BlockItem SOLAR_PANEL_ITEM;

	public static final Block LAMP_BLOCK;
	public static final BlockItem LAMP_ITEM;

	public static final Block BATTERY_BLOCK;
	public static final BlockItem BATTERY_ITEM;

	public static final Block CABLE_BLOCK;
	public static final BlockItem CABLE_ITEM;

	public static final Block ENERGY_TRANSMITTER_BLOCK;
	public static final BlockItem ENERGY_TRANSMITTER_ITEM;

	static {
		SOLAR_PANEL_BLOCK = registerBlock("solar_panel_block", new SolarPanelBlock());
		SOLAR_PANEL_ITEM = registerBlockItem("solar_panel_block", SOLAR_PANEL_BLOCK);

		LAMP_BLOCK = registerBlock("lamp_block", new LampBlock());
		LAMP_ITEM = registerBlockItem("lamp_block", LAMP_BLOCK);

		BATTERY_BLOCK = registerBlock("battery_block", new BatteryBlock());
		BATTERY_ITEM = registerBlockItem("battery_block", BATTERY_BLOCK);

		CABLE_BLOCK = registerBlock("energy_cable_block", new EnergyCableBlock());
		CABLE_ITEM = registerBlockItem("energy_cable_block", CABLE_BLOCK);

		ENERGY_TRANSMITTER_BLOCK = registerBlock("energy_transmitter_block", new EnergyTransmitterBlock());
		ENERGY_TRANSMITTER_ITEM = registerBlockItem("energy_transmitter_block", ENERGY_TRANSMITTER_BLOCK);
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
