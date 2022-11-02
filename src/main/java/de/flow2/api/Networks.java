package de.flow2.api;

import de.flow2.api.networks.Network;
import de.flow2.impl.NetworkManager;
import lombok.experimental.UtilityClass;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class Networks {

	public <T> void add(Network<T> network) {
		NetworkManager.INSTANCE.add(network);
	}

	public <T> void remove(Network<T> network) {
		NetworkManager.INSTANCE.remove(network);
	}

	public <T> @Nullable Network<T> get(UUID uuid) {
		return NetworkManager.INSTANCE.get(uuid);
	}

	public <T> @Nullable Network<T> get(Type<T> type, World world, BlockPos blockPos) {
		return (Network<T>) get(world, blockPos).get(type);
	}

	public Map<Type<?>, Network<?>> get(World world, BlockPos blockPos) {
		return NetworkManager.INSTANCE.get(world, blockPos);
	}

	public <T> List<Network<T>> get(Type<T> type) {
		return NetworkManager.INSTANCE.get(type);
	}

	public void loadCallback(Runnable runnable) {
		NetworkManager.INSTANCE.loadCallback(runnable);
	}

	public void unloadCallback(Runnable runnable) {
		NetworkManager.INSTANCE.unloadCallback(runnable);
	}
}
