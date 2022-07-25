package de.flow.test.redstone;

import de.flow.FlowApi;
import de.flow.test.redstone.redstoneacceptor.RedstoneAcceptorEntity;
import de.flow.test.redstone.redstoneemitter.RedstoneEmitterEntity;
import de.flow.test.redstone.redstonetransmitter.RedstoneTransmitterEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RedstoneBlockEntityInit {
	public static BlockEntityType<RedstoneAcceptorEntity> ACCEPTOR_ENTITY;
	public static BlockEntityType<RedstoneEmitterEntity> EMITTER_ENTITY;
	public static BlockEntityType<RedstoneTransmitterEntity> REDSTONE_TRANSMITTER_ENTITY;

	public static void onInitialize() {
		ACCEPTOR_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(FlowApi.MODID, "acceptor_entity"), FabricBlockEntityTypeBuilder.create(RedstoneAcceptorEntity::new, RedstoneBlockInit.ACCEPTOR_BLOCK).build(null));
		EMITTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(FlowApi.MODID, "emitter_entity"), FabricBlockEntityTypeBuilder.create(RedstoneEmitterEntity::new, RedstoneBlockInit.EMITTER_BLOCK).build(null));
		REDSTONE_TRANSMITTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(FlowApi.MODID, "redstone_transmitter_entity"), FabricBlockEntityTypeBuilder.create(RedstoneTransmitterEntity::new, RedstoneBlockInit.TRANSMITTER_BLOCK).build(null));
	}
}
