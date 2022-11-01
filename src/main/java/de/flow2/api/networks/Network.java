package de.flow2.api.networks;

import de.flow2.api.Type;
import de.flow2.api.cables.CableBlock;
import de.flow2.api.machines.MachineEntity;
import de.flow2.api.machines.Typed;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// TODO: Add JavaDoc
public interface Network<T extends Serializable> {

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
	boolean add(World world, BlockPos blockPos, Typed<T> machineIOPort);

	/**
	 * Internal API
	 */
	boolean remove(World world, BlockPos blockPos, Typed<T> machineIOPort);

	default void add(MachineEntity machineEntity) {
		machineEntity.addIOToNetwork(this);
	}
	default void remove(MachineEntity machineEntity) {
		machineEntity.removeIOFromNetwork(this);
	}

	/**
	 * Internal API
	 */
	Map<World, Set<BlockPos>> cablePositions();

	boolean add(World world, BlockPos pos, CableBlock cableBlock);
	boolean remove(World world, BlockPos pos, CableBlock cableBlock);

	void merge(Network<T> network);
	void split(World world, BlockPos splitPos, List<BlockPos> blockPosList);
}
