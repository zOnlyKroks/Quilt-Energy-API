package de.flow.test.item;

import de.flow.test.item.itemoutput.ItemOutputScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ItemScreenHandled {

	public static void onInitialize() {
		HandledScreens.register(ItemScreenHandler.ITEM_OUTPUT, ItemOutputScreen::new);
	}
}
