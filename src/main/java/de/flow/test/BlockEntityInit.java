package de.flow.test;

import com.google.common.util.concurrent.AtomicDouble;
import de.flow.api.Network;
import de.flow.api.Utils;
import de.flow.impl.NetworkImpl;
import de.flow.test.blocks.BatteryEntity;
import de.flow.test.blocks.LampEntity;
import de.flow.test.blocks.SolarPanelEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.lifecycle.api.event.ServerTickEvents;

public class BlockEntityInit {
	public static Network<Double, AtomicDouble> network = new NetworkImpl<>(Utils.ENERGY_TYPE);
	public static BlockEntityType<SolarPanelEntity> SOLAR_PANEL_ENTITY;

	public static BlockEntityType<LampEntity> LAMP_ENTITY;

	public static BlockEntityType<BatteryEntity> BATTERY_ENTITY;

	public static void onInitialize() {
		SOLAR_PANEL_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "quilt-flow-api:solar_panel_entity", FabricBlockEntityTypeBuilder.create(SolarPanelEntity::new, BlockInit.SOLAR_PANEL_BLOCK).build(null));

		LAMP_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "quilt-flow-api:lamp_entity", FabricBlockEntityTypeBuilder.create(LampEntity::new, BlockInit.LAMP_BLOCK).build(null));

		BATTERY_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "quilt-flow-api:battery_entity", FabricBlockEntityTypeBuilder.create(BatteryEntity::new, BlockInit.BATTERY_BLOCK).build(null));

		ServerTickEvents.START.register(server -> network.tick());
	}
}
