package de.flow.test.item.blocks;

import com.google.common.util.concurrent.AtomicDouble;
import de.flow.api.*;
import de.flow.api.ItemStackContainer;
import de.flow.test.item.ItemBlockEntityInit;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ItemInputEntity extends BlockEntity implements NetworkBlock {

	@Getter
	private double storedAmount = 0;
	private boolean noInput = false;
	private boolean redstoneNoInput = false;

	private List<Inventory> inventoryList = new ArrayList<>();

	public static void tick(World world, BlockPos pos, BlockState state, ItemInputEntity itemInputEntity) {
		itemInputEntity.noInput = itemInputEntity.redstoneNoInput;
		itemInputEntity.redstoneNoInput = false;
		itemInputEntity.inventoryList.clear();
		for (Direction direction : Direction.values()) {
			if (world.getBlockEntity(pos.offset(direction)) instanceof Inventory inventory) {
				itemInputEntity.inventoryList.add(inventory);
			}
		}
	}

	@RegisterToNetwork
	public Input<Map<ItemStackContainer, BigInteger>> input = new LockableInput<>(new DefaultInput<>(this::content, consume -> {
		if (consume.isEmpty()) return;
		storedAmount -= 20;
		extract(new HashMap<>(consume));
	}, Unit.unit(Utils.ITEM_TYPE)), () -> noInput || storedAmount < 20);

	@RegisterToNetwork
	public Output<AtomicDouble> output = new DefaultOutput<>(() -> new AtomicDouble(100 - storedAmount), aDouble -> storedAmount += aDouble.get(), Unit.energyUnit(1));

	@RegisterToNetwork
	private Output<AtomicInteger> redstoneOutput = new DefaultOutput<>(() -> new AtomicInteger(1), value -> redstoneNoInput = true, Unit.numberUnit(Utils.REDSTONE_TYPE, 1));

	public ItemInputEntity(BlockPos blockPos, BlockState blockState) {
		super(ItemBlockEntityInit.ITEM_INPUT_ENTITY, blockPos, blockState);
	}

	private static IntStream getAvailableSlots(Inventory inventory) {
		return inventory instanceof SidedInventory ? IntStream.of(((SidedInventory) inventory).getAvailableSlots(Direction.DOWN)) : IntStream.range(0, inventory.size());
	}

	private Map<ItemStackContainer, BigInteger> content() {
		Map<ItemStackContainer, BigInteger> content = new HashMap<>();
		inventoryList.forEach(inventory -> {
			getAvailableSlots(inventory).forEach(value -> {
				ItemStack itemStack = inventory.getStack(value);
				if (!itemStack.isEmpty()) {
					ItemStackContainer itemStackContainer = new ItemStackContainer(itemStack);
					BigInteger bigInteger = content.getOrDefault(itemStackContainer, BigInteger.ZERO);
					bigInteger = bigInteger.add(BigInteger.valueOf(itemStack.getCount()));
					content.put(itemStackContainer, bigInteger);
				}
			});
		});
		return content;
	}

	private void extract(Map<ItemStackContainer, BigInteger> toExtract) {
		for (Inventory inventory : inventoryList) {
			PrimitiveIterator.OfInt iterator = getAvailableSlots(inventory).iterator();
			while (iterator.hasNext()) {
				int slot = iterator.nextInt();
				ItemStack itemStack = inventory.getStack(slot);
				if (itemStack.isEmpty()) continue;
				ItemStackContainer current = new ItemStackContainer(itemStack);
				if (!toExtract.containsKey(current)) continue;
				BigInteger amount = toExtract.get(current);
				BigInteger itemAmount = BigInteger.valueOf(itemStack.getCount());
				BigInteger extractAmount = amount.min(itemAmount);
				itemStack.decrement(extractAmount.intValue());
				amount = amount.subtract(extractAmount);
				if (amount.equals(BigInteger.ZERO)) {
					toExtract.remove(current);
				} else {
					toExtract.put(current, amount);
				}
				if (itemStack.getCount() == 0) {
					inventory.setStack(slot, ItemStack.EMPTY);
				}
				if (toExtract.isEmpty()) break;
			}
			if (toExtract.isEmpty()) break;
		}
	}
}
