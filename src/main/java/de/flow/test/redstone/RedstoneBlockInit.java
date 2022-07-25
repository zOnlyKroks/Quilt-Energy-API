package de.flow.test.redstone;

import de.flow.FlowApi;
import de.flow.test.redstone.redstoneacceptor.RedstoneAcceptorBlock;
import de.flow.test.redstone.redstonecable.RedstoneCableBlock;
import de.flow.test.redstone.redstoneemitter.RedstoneEmitterBlock;
import de.flow.test.redstone.redstonetransmitter.RedstoneTransmitterBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class RedstoneBlockInit {

    public static final Block CABLE_BLOCK;
	public static final Block ACCEPTOR_BLOCK;
	public static final Block EMITTER_BLOCK;
	public static final Block TRANSMITTER_BLOCK;
    public static final BlockItem CABLE_ITEM;
	public static final BlockItem ACCEPTOR_ITEM;
	public static final BlockItem EMITTER_ITEM;
	public static final BlockItem TRANSMITTER_ITEM;

    static {
        CABLE_BLOCK = registerBlock("redstone_cable_block", new RedstoneCableBlock());
        CABLE_ITEM = registerBlockItem("redstone_cable_block", CABLE_BLOCK);
		ACCEPTOR_BLOCK = registerBlock("redstone_acceptor_block", new RedstoneAcceptorBlock());
		ACCEPTOR_ITEM = registerBlockItem("redstone_acceptor_block", ACCEPTOR_BLOCK);
		EMITTER_BLOCK = registerBlock("redstone_emitter_block", new RedstoneEmitterBlock());
		EMITTER_ITEM = registerBlockItem("redstone_emitter_block", EMITTER_BLOCK);
		TRANSMITTER_BLOCK = registerBlock("redstone_transmitter_block", new RedstoneTransmitterBlock());
		TRANSMITTER_ITEM = registerBlockItem("redstone_transmitter_block", TRANSMITTER_BLOCK);
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
