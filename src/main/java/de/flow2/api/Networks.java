package de.flow2.api;

import de.flow2.api.networks.Network;
import lombok.experimental.UtilityClass;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class Networks {

	public <T extends Serializable> void add(Network<T> network) {
		// TODO: Implement
	}

	public <T extends Serializable> void remove(Network<T> network) {
		// TODO: Implement
	}

	public <T extends Serializable> @Nullable Network<T> get(UUID uuid) {
		return null; // TODO: Implement
	}

	public <T extends Serializable> @Nullable Network<T> get(Type<T> type, World world, BlockPos blockPos) {
		return (Network<T>) get(world, blockPos).get(type);
	}

	public Map<Type<?>, Network<?>> get(World world, BlockPos blockPos) {
		return null; // TODO: Implement
	}

	public <T extends Serializable> List<Network<T>> get(Type<T> type) {
		return null; // TODO: Implement
	}
}
