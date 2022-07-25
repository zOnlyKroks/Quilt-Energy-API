package de.flow.test.item.blocks;

import com.google.common.util.concurrent.AtomicDouble;
import de.flow.api.*;
import de.flow.test.item.ItemBlockEntityInit;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemOutputEntity extends BlockEntity implements NetworkBlock {

	@Getter
	private double storedAmount = 0;
	private boolean noInput = false;
	private boolean redstoneNoInput = false;

	private List<Inventory> inventoryList = new ArrayList<>();

	public static void tick(World world, BlockPos pos, BlockState state, ItemOutputEntity itemOutputEntity) {
		itemOutputEntity.noInput = itemOutputEntity.redstoneNoInput;
		itemOutputEntity.redstoneNoInput = false;
		itemOutputEntity.inventoryList.clear();
		for (Direction direction : Direction.values()) {
			if (world.getBlockEntity(pos.offset(direction)) instanceof Inventory inventory) {
				itemOutputEntity.inventoryList.add(inventory);
			}
		}
	}

	@RegisterToNetwork
	public Output<Map<ItemStackContainer, BigInteger>> input = new LockableOutput<>(new DefaultOutput<>(() -> {
		Map<ItemStackContainer, BigInteger> toGet = new HashMap<>();
		toGet.put(new ItemStackContainer(new ItemStack(Items.GRASS_BLOCK)), BigInteger.valueOf(1));
		return toGet;
	}, consume -> {
		storedAmount -= 20;
		System.out.println("OUTPUT: " + consume);
	}, Unit.unit(Utils.ITEM_TYPE)), () -> noInput || storedAmount < 20);

	@RegisterToNetwork
	public Output<AtomicDouble> output = new DefaultOutput<>(() -> new AtomicDouble(100 - storedAmount), aDouble -> storedAmount += aDouble.get(), Unit.energyUnit(1));

	@RegisterToNetwork
	private Output<AtomicInteger> redstoneOutput = new DefaultOutput<>(() -> new AtomicInteger(1), value -> redstoneNoInput = true, Unit.numberUnit(Utils.REDSTONE_TYPE, 1));

	public ItemOutputEntity(BlockPos blockPos, BlockState blockState) {
		super(ItemBlockEntityInit.ITEM_OUTPUT_ENTITY, blockPos, blockState);
	}
}
