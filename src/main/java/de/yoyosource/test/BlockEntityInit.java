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

public class BlockEntityInit {
	public static Network network = new Network();
	public static BlockEntityType<SolarPanelEntity> SOLAR_PANEL_ENTITY;

	public static BlockEntityType<LampEntity> LAMP_ENTITY;

	public static BlockEntityType<BatteryEntity> BATTERY_ENTITY;

	public static void onInitialize() {
		SOLAR_PANEL_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "quilt-energy-api:solar_panel_entity", FabricBlockEntityTypeBuilder.create(SolarPanelEntity::new, BlockInit.SOLAR_PANEL_BLOCK).build(null));

		LAMP_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "quilt-energy-api:lamp_entity", FabricBlockEntityTypeBuilder.create(LampEntity::new, BlockInit.LAMP_BLOCK).build(null));

		BATTERY_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "quilt-energy-api:battery_lamp", FabricBlockEntityTypeBuilder.create(BatteryEntity::new, BlockInit.BATTERY_BLOCK).build(null));

		ServerTickEvents.START.register(server -> network.tick());
	}
}
