package de.flow;

import de.flow.test.BlockEntityInit;
import de.flow.test.BlockInit;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.item.group.api.QuiltItemGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowApi implements ModInitializer {
	public static final String MODID = "quilt-flow-api";
	public static final Logger LOGGER = LoggerFactory.getLogger("Quilt Flow API");

	public static ItemGroup ITEM_GROUP = QuiltItemGroup.createWithIcon(new Identifier(MODID, "item_group"),
			() -> new ItemStack(BlockInit.SOLAR_PANEL_BLOCK));

	@Override
	public void onInitialize(ModContainer mod) {
		BlockEntityInit.onInitialize();
		BlockInit.onInitialize();

		LOGGER.info("Initializing Quilt Energy Api v: " + mod.metadata().version().raw());
	}
}
