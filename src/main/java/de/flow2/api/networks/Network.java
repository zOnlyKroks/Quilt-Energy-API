package de.flow2.api.networks;

import de.flow.api.NetworkCable;
import de.flow.api.Networkable;
import de.flow2.api.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.checkerframework.checker.units.qual.C;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// TODO: Add JavaDoc
public interface Network<T> {

	/**
	 * Internal API
	 */
	Type<T> type();

	/**
	 * Internal API
	 */
	UUID getId();

	/**
	 * Internal API
	 */
	void tick();

	/**
	 * Internal API
	 */
	boolean add(World world, BlockPos blockPos, Networkable<C> networkable);

	/**
	 * Internal API
	 */
	boolean remove(World world, BlockPos blockPos, Networkable<C> networkable);

	/*
	default void add(NetworkBlock networkBlock) {
		iterate(networkBlock, this::add);
	}
	default void remove(NetworkBlock networkBlock) {
		iterate(networkBlock, this::remove);
	}
	 */

	/**
	 * Internal API
	 */
	Map<World, Set<BlockPos>> cablePositions();

	boolean add(World world, BlockPos pos, NetworkCable<C> networkCable);
	boolean remove(World world, BlockPos pos, NetworkCable<C> networkCable);

	void merge(de.flow.api.Network<C> network);
	void split(World world, BlockPos splitPos, List<BlockPos> blockPosList);
}
