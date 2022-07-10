package de.flow.test.redstone;

import de.flow.FlowApi;
import de.flow.test.redstone.blocks.RedstoneCableBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class RedstoneBlockInit {

    public static final Block CABLE_BLOCK;
    public static final BlockItem CABLE_ITEM;

    static {
        CABLE_BLOCK = registerBlock("redstone_cable_block", new RedstoneCableBlock());
        CABLE_ITEM = registerBlockItem("redstone_cable_block", CABLE_BLOCK);
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
