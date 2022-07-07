package de.flow.test;

import de.flow.FlowApi;
import de.flow.test.blocks.BatteryEntity;
import de.flow.test.blocks.LampEntity;
import de.flow.test.blocks.SolarPanelEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockEntityInit {
	public static BlockEntityType<SolarPanelEntity> SOLAR_PANEL_ENTITY;

	public static BlockEntityType<LampEntity> LAMP_ENTITY;

	public static BlockEntityType<BatteryEntity> BATTERY_ENTITY;

	public static void onInitialize() {
		SOLAR_PANEL_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(FlowApi.MODID, "solar_panel_entity"), FabricBlockEntityTypeBuilder.create(SolarPanelEntity::new, BlockInit.SOLAR_PANEL_BLOCK).build(null));

		LAMP_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(FlowApi.MODID, "lamp_entity"), FabricBlockEntityTypeBuilder.create(LampEntity::new, BlockInit.LAMP_BLOCK).build(null));

		BATTERY_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(FlowApi.MODID, "battery_entity"), FabricBlockEntityTypeBuilder.create(BatteryEntity::new, BlockInit.BATTERY_BLOCK).build(null));
	}
}
