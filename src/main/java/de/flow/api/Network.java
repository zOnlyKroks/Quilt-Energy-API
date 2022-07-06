package de.flow.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public interface Network<C> extends Typeable<C> {

	UUID getId();

	void tick();
	boolean add(Networkable<C> networkable);
	boolean remove(Networkable<C> networkable);

	default void add(NetworkBlock networkBlock) {
		iterate(networkBlock, this::add);
	}
	default void remove(NetworkBlock networkBlock) {
		iterate(networkBlock, this::remove);
	}

	default void iterate(NetworkBlock networkBlock, Predicate<Networkable<C>> consumer) {
		Field[] fields = networkBlock.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(RegisterToNetwork.class)) {
				try {
					field.setAccessible(true);
					Networkable<C> networkable = (Networkable<C>) field.get(networkBlock);
					consumer.test(networkable);
				} catch (Exception e) {
					// Ignore
				}
			}
		}
	}

	Map<World, Set<BlockPos>> cablePositions();

	boolean add(World world, BlockPos pos, NetworkCable<C> networkCable);
	boolean remove(World world, BlockPos pos, NetworkCable<C> networkCable);
}
