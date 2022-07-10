package de.flow.test.redstone;

import de.flow.FlowApi;
import de.flow.test.energy.EnergyBlockInit;
import de.flow.test.redstone.blocks.RedstoneAcceptorBlockEntity;
import de.flow.test.redstone.blocks.RedstoneEmitterBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RedstoneBlockEntityInit {
	public static BlockEntityType<RedstoneAcceptorBlockEntity> ACCEPTOR_ENTITY;
	public static BlockEntityType<RedstoneEmitterBlockEntity> EMITTER_ENTITY;

	public static void onInitialize() {
		ACCEPTOR_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(FlowApi.MODID, "acceptor_entity"), FabricBlockEntityTypeBuilder.create(RedstoneAcceptorBlockEntity::new, RedstoneBlockInit.ACCEPTOR_BLOCK).build(null));
		EMITTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(FlowApi.MODID, "emitter_entity"), FabricBlockEntityTypeBuilder.create(RedstoneEmitterBlockEntity::new, RedstoneBlockInit.EMITTER_BLOCK).build(null));
	}
}
