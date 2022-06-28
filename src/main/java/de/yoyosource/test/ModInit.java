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

public class ModInit {

	public static Network network = new Network();

	public static BlockEntityType<SolarPanelEntity> SOLAR_PANEL_ENTITY;
	public static Block SOLAR_PANEL_BLOCK = new SolarPanelBlock();

	public static BlockEntityType<LampEntity> LAMP_ENTITY;
	public static Block LAMP_BLOCK = new LampBlock();

	public static BlockEntityType<BatteryEntity> BATTERY_ENTITY;
	public static Block BATTERY_BLOCK = new BatteryBlock();

	static {
		Registry.register(Registry.BLOCK, new Identifier("quilt-energy-api", "solar_panel_block"), SOLAR_PANEL_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("quilt-energy-api", "solar_panel_block"), new BlockItem(SOLAR_PANEL_BLOCK, new QuiltItemSettings().group(ItemGroup.MISC)));

		SOLAR_PANEL_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "quilt-energy-api:solar_panel_entity", FabricBlockEntityTypeBuilder.create(SolarPanelEntity::new, SOLAR_PANEL_BLOCK).build(null));

		Registry.register(Registry.BLOCK, new Identifier("quilt-energy-api", "lamp_block"), LAMP_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("quilt-energy-api", "lamp_block"), new BlockItem(LAMP_BLOCK, new QuiltItemSettings().group(ItemGroup.MISC)));

		LAMP_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "quilt-energy-api:lamp_entity", FabricBlockEntityTypeBuilder.create(LampEntity::new, LAMP_BLOCK).build(null));

		Registry.register(Registry.BLOCK, new Identifier("quilt-energy-api", "battery_block"), BATTERY_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("quilt-energy-api", "battery_block"), new BlockItem(BATTERY_BLOCK, new QuiltItemSettings().group(ItemGroup.MISC)));

		BATTERY_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "quilt-energy-api:battery_lamp", FabricBlockEntityTypeBuilder.create(BatteryEntity::new, BATTERY_BLOCK).build(null));

		ServerTickEvents.START.register(server -> {
			network.tick();
		});
	}
}
