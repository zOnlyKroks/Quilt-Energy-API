package de.flow.test.item.itemoutput;

import de.flow.test.item.ItemScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class ItemOutputScreenHandler extends ScreenHandler {

	private final Inventory inventory;

	public ItemOutputScreenHandler(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, new SimpleInventory(1));
	}

	public ItemOutputScreenHandler(int i, PlayerInventory playerInventory, Inventory inventory) {
		super(ItemScreenHandler.ITEM_OUTPUT, i);
		this.inventory = inventory;
		checkSize(inventory, 1);
		inventory.onOpen(playerInventory.player);

		this.addSlot(new Slot(inventory, 0, 44 + 2 * 18, 20));

		int k;
		for(k = 0; k < 3; ++k) {
			for(int l = 0; l < 9; ++l) {
				this.addSlot(new Slot(playerInventory, l + k * 9 + 9, 8 + l * 18, k * 18 + 51));
			}
		}

		for(k = 0; k < 9; ++k) {
			this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 109));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		return ItemStack.EMPTY;
	}

	public void close(PlayerEntity player) {
		super.close(player);
		this.inventory.onClose(player);
	}
}
