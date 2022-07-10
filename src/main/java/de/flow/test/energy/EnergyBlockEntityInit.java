package de.flow.test.energy;

import de.flow.FlowApi;
import de.flow.test.energy.blocks.BatteryEntity;
import de.flow.test.energy.blocks.LampEntity;
import de.flow.test.energy.blocks.SolarPanelEntity;
import de.flow.test.energy.blocks.EnergyTransmitterEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EnergyBlockEntityInit {
	public static BlockEntityType<SolarPanelEntity> SOLAR_PANEL_ENTITY;

	public static BlockEntityType<LampEntity> LAMP_ENTITY;

	public static BlockEntityType<BatteryEntity> BATTERY_ENTITY;

	public static BlockEntityType<EnergyTransmitterEntity> ENERGY_TRANSMITTER_ENTITY;

	public static void onInitialize() {
		SOLAR_PANEL_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(FlowApi.MODID, "solar_panel_entity"), FabricBlockEntityTypeBuilder.create(SolarPanelEntity::new, EnergyBlockInit.SOLAR_PANEL_BLOCK).build(null));

		LAMP_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(FlowApi.MODID, "lamp_entity"), FabricBlockEntityTypeBuilder.create(LampEntity::new, EnergyBlockInit.LAMP_BLOCK).build(null));

		BATTERY_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(FlowApi.MODID, "battery_entity"), FabricBlockEntityTypeBuilder.create(BatteryEntity::new, EnergyBlockInit.BATTERY_BLOCK).build(null));

		ENERGY_TRANSMITTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(FlowApi.MODID, "energy_transmitter_entity"), FabricBlockEntityTypeBuilder.create(EnergyTransmitterEntity::new, EnergyBlockInit.ENERGY_TRANSMITTER_BLOCK).build(null));
	}
}
