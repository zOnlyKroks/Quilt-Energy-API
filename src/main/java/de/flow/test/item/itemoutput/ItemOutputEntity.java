package de.flow.test.item.itemoutput;

import com.google.common.util.concurrent.AtomicDouble;
import de.flow.api.*;
import de.flow.test.item.ItemBlockEntityInit;
import lombok.Getter;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ItemOutputEntity extends BlockEntity implements NetworkBlock, Inventory, NamedScreenHandlerFactory, ItemTypeRequests, NoInsert {

	@Getter
	private double storedAmount = 0;
	private boolean noInput = false;
	private boolean redstoneNoInput = false;

	private ItemStack[] toRequest = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};

	private List<Inventory> inventoryList = new ArrayList<>();

	public static void tick(World world, BlockPos pos, BlockState state, ItemOutputEntity itemOutputEntity) {
		itemOutputEntity.noInput = itemOutputEntity.redstoneNoInput;
		itemOutputEntity.redstoneNoInput = false;
		itemOutputEntity.inventoryList.clear();
		for (Direction direction : Direction.values()) {
			if (world.getBlockEntity(pos.offset(direction)) instanceof Inventory inventory) {
				if (inventory instanceof NoInsert noInsert && noInsert.hasNoInsertType(Utils.ITEM_TYPE)) continue;
				itemOutputEntity.inventoryList.add(inventory);
			}
		}
		itemOutputEntity.storedAmount -= Math.min(itemOutputEntity.storedAmount, 5);
	}

	@Override
	public ItemStack[] requesting() {
		return toRequest;
	}

	@RegisterToNetwork
	public Output<Map<ItemStackContainer, BigInteger>> input = new LockableOutput<>(new DefaultOutput<>(() -> {
		if (hasSpace()) { // TODO: Fix calculation to only reqeust what is possible to insert
			Map<ItemStackContainer, BigInteger> toGet = new HashMap<>();
			for (ItemStack request : toRequest) {
				if (request.isEmpty()) continue;
				toGet.put(new ItemStackContainer(request), BigInteger.valueOf(request.getCount()));
			}
			return toGet;
		} else {
			return new HashMap<>();
		}
	}, consume -> {
		storedAmount -= 20;
		insert(new HashMap<>(consume));
	}, Unit.unit(Utils.ITEM_TYPE)), () -> noInput || storedAmount < 20);

	@RegisterToNetwork
	public Output<AtomicDouble> output = new DefaultOutput<>(() -> new AtomicDouble(100 - storedAmount), aDouble -> storedAmount += aDouble.get(), Unit.energyUnit(1));

	@RegisterToNetwork
	private Output<AtomicInteger> redstoneOutput = new DefaultOutput<>(() -> new AtomicInteger(1), value -> redstoneNoInput = true, Unit.numberUnit(Utils.REDSTONE_TYPE, 1));

	public ItemOutputEntity(BlockPos blockPos, BlockState blockState) {
		super(ItemBlockEntityInit.ITEM_OUTPUT_ENTITY, blockPos, blockState);
	}

	private static IntStream getAvailableSlots(Inventory inventory, Direction... directions) {
		if (inventory instanceof SidedInventory sidedInventory) {
			IntStream intStream = IntStream.empty();
			for (Direction direction : directions) {
				intStream = IntStream.concat(intStream, IntStream.of(sidedInventory.getAvailableSlots(direction)));
			}
			return intStream.distinct();
		} else {
			return IntStream.range(0, inventory.size());
		}
	}

	private static IntStream getAvailableSlots(Inventory inventory) {
		return getAvailableSlots(inventory, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
	}

	private boolean hasSpace() {
		for (Inventory inventory : inventoryList) {
			if (getAvailableSlots(inventory).anyMatch(slot -> {
				ItemStack itemStack = inventory.getStack(slot);
				return itemStack.getCount() < itemStack.getMaxCount();
			})) {
				return true;
			}
		}
		return false;
	}

	private void insert(Map<ItemStackContainer, BigInteger> toInsert) {
		insert(toInsert, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
		if (toInsert.isEmpty()) return;
		insert(toInsert, Direction.UP);
	}

	private void insert(Map<ItemStackContainer, BigInteger> toInsert, Direction... directions) {
		for (Inventory inventory : inventoryList) {
			PrimitiveIterator.OfInt iterator = getAvailableSlots(inventory, directions).iterator();
			while (iterator.hasNext()) {
				int slot = iterator.nextInt();
				ItemStack itemStack = inventory.getStack(slot);
				if (itemStack.isEmpty()) continue;

				ItemStackContainer current = new ItemStackContainer(itemStack);
				if (!canInsert(inventory, slot, current.getValue(), directions)) continue;
				BigInteger amount = toInsert.get(current);
				if (amount == null) continue;
				int insertAmount = amount.min(BigInteger.valueOf(itemStack.getMaxCount() - itemStack.getCount())).intValue();
				itemStack.increment(insertAmount);
				amount = amount.subtract(BigInteger.valueOf(insertAmount));
				if (amount.equals(BigInteger.ZERO)) {
					toInsert.remove(current);
				} else {
					toInsert.put(current, amount);
				}
				if (toInsert.isEmpty()) break;
			}
			if (toInsert.isEmpty()) break;
		}
		if (toInsert.isEmpty()) return;

		for (Inventory inventory : inventoryList) {
			PrimitiveIterator.OfInt iterator = getAvailableSlots(inventory, directions).iterator();
			while (iterator.hasNext()) {
				int slot = iterator.nextInt();
				ItemStack itemStack = inventory.getStack(slot);
				if (!itemStack.isEmpty()) continue;

				ItemStackContainer itemStackContainer = toInsert.keySet().iterator().next();
				if (!canInsert(inventory, slot, itemStackContainer.getValue(), directions)) continue;
				ItemStack value = itemStackContainer.getValue().copy();
				BigInteger amount = toInsert.get(itemStackContainer);
				amount = amount.min(BigInteger.valueOf(value.getMaxCount()));
				value.setCount(amount.intValue());
				inventory.setStack(slot, value);
				amount = toInsert.get(itemStackContainer).subtract(BigInteger.valueOf(value.getCount()));
				if (amount.equals(BigInteger.ZERO)) {
					toInsert.remove(itemStackContainer);
				} else {
					toInsert.put(itemStackContainer, amount);
				}
				if (toInsert.isEmpty()) break;
			}
			if (toInsert.isEmpty()) break;
		}
	}

	private boolean canInsert(Inventory inventory, int slot, ItemStack itemStack, Direction... directions) {
		if (inventory instanceof SidedInventory sidedInventory) {
			for (Direction direction : directions) {
				if (sidedInventory.canInsert(slot, itemStack, direction)) {
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		NbtList toRequest = nbt.getList("toRequest", NbtType.COMPOUND);
		for (int i = 0; i < toRequest.size(); i++) {
			NbtCompound nbtCompound = (NbtCompound) toRequest.get(i);
			this.toRequest[i] = ItemStack.fromNbt(nbtCompound);
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		NbtList nbtList = new NbtList();
		for (ItemStack itemStack : toRequest) {
			NbtCompound nbtCompound = new NbtCompound();
			itemStack.writeNbt(nbtCompound);
			nbtList.add(nbtCompound);
		}
		nbt.put("toRequest", nbtList);
	}

	@Override
	public int size() {
		return toRequest.length;
	}

	@Override
	public boolean isEmpty() {
		return Arrays.stream(toRequest).allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getStack(int slot) {
		if (slot >= 0 && slot < toRequest.length) {
			return toRequest[slot];
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return removeStack(slot);
	}

	@Override
	public ItemStack removeStack(int slot) {
		if (slot >= 0 && slot < toRequest.length) {
			ItemStack itemStack = toRequest[slot];
			toRequest[slot] = ItemStack.EMPTY;
			return itemStack;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		if (slot >= 0 && slot < toRequest.length) {
			toRequest[slot] = stack;
		}
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		if (this.world.getBlockEntity(this.pos) != this) {
			return false;
		} else {
			return !(player.squaredDistanceTo((double) this.pos.getX() + 0.5, (double) this.pos.getY() + 0.5, (double) this.pos.getZ() + 0.5) > 64.0);
		}
	}

	@Override
	public void clear() {
		Arrays.fill(toRequest, ItemStack.EMPTY);
	}

	@Override
	public Text getDisplayName() {
		return Text.translatable("container.quilt-flow-api.item_output");
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new ItemOutputScreenHandler(i, playerInventory, this);
	}

	@Override
	public Type<?>[] types() {
		return new Type<?>[]{Utils.ITEM_TYPE};
	}
}
