package de.flow.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface Network<C> extends Typeable<C> {

	UUID getId();

	void tick();
	boolean add(World world, BlockPos blockPos, Networkable<C> networkable);
	boolean remove(World world, BlockPos blockPos, Networkable<C> networkable);

	default void add(NetworkBlock networkBlock) {
		iterate(networkBlock, this::add);
	}
	default void remove(NetworkBlock networkBlock) {
		iterate(networkBlock, this::remove);
	}

	default void iterate(NetworkBlock networkBlock, TriPredicate<World, BlockPos, Networkable<C>> consumer) {
		Field[] fields = networkBlock.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(RegisterToNetwork.class)) {
				try {
					field.setAccessible(true);
					Networkable<C> networkable = (Networkable<C>) field.get(networkBlock);
					consumer.test(networkBlock.getWorld(), networkBlock.getPos(), networkable);
				} catch (Exception e) {
					// Ignore
				}
			}
		}
	}

	@FunctionalInterface
	interface TriPredicate<A, B, C> {
		boolean test(A a, B b, C c);
	}

	Map<World, Set<BlockPos>> cablePositions();

	boolean add(World world, BlockPos pos, NetworkCable<C> networkCable);
	boolean remove(World world, BlockPos pos, NetworkCable<C> networkCable);
}
