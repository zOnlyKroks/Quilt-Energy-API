package de.flow.api;

import de.flow.impl.NetworkManager;
import lombok.experimental.UtilityClass;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

@UtilityClass
public class Networks {

	public <T extends Network<C>, C> void add(T network) {
		NetworkManager.add(network);
	}

	public <T extends Network<C>, C> void remove(T network) {
		NetworkManager.remove(network);
	}

	public List<Type<?>> types() {
		return NetworkManager.types();
	}

	public <T extends Network<C>, C> List<T> get(Type<C> type) {
		return NetworkManager.get(type);
	}

	public <T extends Network<C>, C> T get(World world, BlockPos blockPos) {
		return NetworkManager.get(world, blockPos);
	}

	public <T extends Network<C>, C> T get(Type<C> type, World world, BlockPos blockPos) {
		return NetworkManager.get(type, world, blockPos);
	}

	public <T extends Network<C>, C> T get(UUID uuid) {
		return NetworkManager.get(uuid);
	}
}
