package de.flow.test.item;

import de.flow.FlowApi;
import de.flow.test.item.iteminput.ItemInputEntity;
import de.flow.test.item.itemoutput.ItemOutputEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemBlockEntityInit {

	public static BlockEntityType<ItemInputEntity> ITEM_INPUT_ENTITY;
	public static BlockEntityType<ItemOutputEntity> ITEM_OUTPUT_ENTITY;

	public static void onInitialize() {
		ITEM_INPUT_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(FlowApi.MODID, "item_input_entity"), FabricBlockEntityTypeBuilder.create(ItemInputEntity::new, ItemBlockInit.ITEM_INPUT_BLOCK).build(null));
		ITEM_OUTPUT_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(FlowApi.MODID, "item_output_entity"), FabricBlockEntityTypeBuilder.create(ItemOutputEntity::new, ItemBlockInit.ITEM_OUTPUT_BLOCK).build(null));
	}
}
