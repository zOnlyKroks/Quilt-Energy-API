package de.flow.test.item.itemoutput;

import de.flow.test.item.ItemScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ClickType;

import java.util.Optional;

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

	private StackReference getCursorStackReference() {
		return new StackReference() {
			public ItemStack get() {
				return ItemOutputScreenHandler.this.getCursorStack();
			}

			public boolean set(ItemStack stack) {
				ItemOutputScreenHandler.this.setCursorStack(stack);
				return true;
			}
		};
	}

	@Override
	public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
		PlayerInventory playerInventory = player.getInventory();
		if ((actionType == SlotActionType.PICKUP || actionType == SlotActionType.QUICK_MOVE) && (button == 0 || button == 1)) {
			ClickType clickType = button == 0 ? ClickType.LEFT : ClickType.RIGHT;
			if (slotIndex == -999) {
				if (!this.getCursorStack().isEmpty()) {
					if (clickType == ClickType.LEFT) {
						player.dropItem(this.getCursorStack(), true);
						this.setCursorStack(ItemStack.EMPTY);
					} else {
						player.dropItem(this.getCursorStack().split(1), true);
					}
				}
			} else if (actionType == SlotActionType.QUICK_MOVE) {
				if (slotIndex < 0) {
					return;
				}
				Slot slot = this.slots.get(slotIndex);
				if (slotIndex == 0) {
					inventory.setStack(0, ItemStack.EMPTY);
					slot.setStack(ItemStack.EMPTY);
					slot.markDirty();
				} else if (slotIndex > 1) {
					ItemStack itemStack = slot.getStack().copy();
					inventory.setStack(0, itemStack);
					slot.setStack(itemStack);
					slot.markDirty();
				}
			} else {
				if (slotIndex < 0) {
					return;
				}

				Slot slot = this.slots.get(slotIndex);
				if (slotIndex == 0) {
					ItemStack itemStack = getCursorStack().copy();
					inventory.setStack(0, itemStack);
					slot.setStack(itemStack);
					slot.markDirty();
					return;
				}

				ItemStack itemStack = slot.getStack();
				int n;
				ItemStack itemStack5 = this.getCursorStack();
				player.onPickupSlotClick(itemStack5, slot.getStack(), clickType);
				if (!itemStack5.onStackClicked(slot, clickType, player) && !itemStack.onClicked(itemStack5, slot, clickType, player, this.getCursorStackReference())) {
					if (itemStack.isEmpty()) {
						if (!itemStack5.isEmpty()) {
							n = clickType == ClickType.LEFT ? itemStack5.getCount() : 1;
							this.setCursorStack(slot.insertStack(itemStack5, n));
						}
					} else if (slot.canTakeItems(player)) {
						if (itemStack5.isEmpty()) {
							n = clickType == ClickType.LEFT ? itemStack.getCount() : (itemStack.getCount() + 1) / 2;
							Optional<ItemStack> optional = slot.tryTakeStackRange(n, Integer.MAX_VALUE, player);
							optional.ifPresent((stack) -> {
								this.setCursorStack(stack);
								slot.onTakeItem(player, stack);
							});
						} else if (slot.canInsert(itemStack5)) {
							if (ItemStack.canCombine(itemStack, itemStack5)) {
								n = clickType == ClickType.LEFT ? itemStack5.getCount() : 1;
								this.setCursorStack(slot.insertStack(itemStack5, n));
							} else if (itemStack5.getCount() <= slot.getMaxItemCount(itemStack5)) {
								this.setCursorStack(itemStack);
								slot.setStack(itemStack5);
							}
						} else if (ItemStack.canCombine(itemStack, itemStack5)) {
							Optional<ItemStack> optional2 = slot.tryTakeStackRange(itemStack.getCount(), itemStack5.getMaxCount() - itemStack5.getCount(), player);
							optional2.ifPresent((stack) -> {
								itemStack5.increment(stack.getCount());
								slot.onTakeItem(player, stack);
							});
						}
					}
				}

				slot.markDirty();
			}
		} else if (actionType == SlotActionType.THROW && this.getCursorStack().isEmpty() && slotIndex >= 0) {
			Slot slot = this.slots.get(slotIndex);
			if (slotIndex == 0) {
				inventory.setStack(0, ItemStack.EMPTY);
				slot.setStack(ItemStack.EMPTY);
				slot.markDirty();
				return;
			}
			int j = button == 0 ? 1 : slot.getStack().getCount();
			ItemStack itemStack = slot.takeStackRange(j, Integer.MAX_VALUE, player);
			player.dropItem(itemStack, true);
		}
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
