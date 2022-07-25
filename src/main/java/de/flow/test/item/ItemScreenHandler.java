package de.flow.test.item;

import de.flow.FlowApi;
import de.flow.test.item.itemoutput.ItemOutputScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemScreenHandler {

	public static final ScreenHandlerType<ItemOutputScreenHandler> ITEM_OUTPUT;

	static {
		ITEM_OUTPUT = register("item_output", ItemOutputScreenHandler::new);
	}

	private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
		return Registry.register(Registry.SCREEN_HANDLER, new Identifier(FlowApi.MODID, id), new ScreenHandlerType<>(factory));
	}

	public static void onInitialize() {

	}
}
