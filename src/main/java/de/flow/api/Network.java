package de.flow.api;

import de.flow.impl.TransmitterData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Interface for public api of the flow network system. One implementation is {@link de.flow.impl.NetworkImpl}.
 *
 * @param <C> the container object for transferring.
 */
public interface Network<C> extends Typeable<C> {

	/**
	 * Internal API
	 */
	UUID getId();

	/**
	 * Internal API
	 */
	boolean needsTick();

	/**
	 * Internal API
	 */
	void calculateAmounts();

	/**
	 * Internal API
	 */
	boolean hasTransmitter();

	/**
	 * Internal API
	 */
	void calculateWithoutTransmitter();

	/**
	 * Internal API
	 */
	Set<NetworkBlock.TransmitterIdentifier> calculateTransmitterLimits();

	/**
	 * Internal API
	 */
	void calculateTransmitterNeededOrProvided(Map<NetworkBlock.TransmitterIdentifier, TransmitterData<C>> data);

	/**
	 * Internal API
	 */
	void calculateTransmitterNeededOrProvidedStorage(Map<NetworkBlock.TransmitterIdentifier, TransmitterData<C>> data);

	/**
	 * Internal API
	 */
	boolean add(World world, BlockPos blockPos, Networkable<C> networkable);

	/**
	 * Internal API
	 */
	boolean remove(World world, BlockPos blockPos, Networkable<C> networkable);

	default void add(NetworkBlock networkBlock) {
		iterate(networkBlock, this::add);
	}
	default void remove(NetworkBlock networkBlock) {
		iterate(networkBlock, this::remove);
	}

	/**
	 * Internal API
	 */
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

	/**
	 * Internal API
	 */
	Map<World, Set<BlockPos>> cablePositions();

	boolean add(World world, BlockPos pos, NetworkCable<C> networkCable);
	boolean remove(World world, BlockPos pos, NetworkCable<C> networkCable);

	void merge(Network<C> network);
	void split(World world, BlockPos splitPos, List<BlockPos> blockPosList);
}
