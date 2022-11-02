package de.flow2.impl;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import de.flow.FlowApi;
import de.flow2.api.Type;
import de.flow2.api.networks.Network;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class NetworkManager {

	public static final NetworkManager INSTANCE = new NetworkManager();

	private Map<Type<?>, List<Network<?>>> networks = new HashMap<>();

	private Set<Runnable> loadCallbacks = new HashSet<>();
	private Set<Runnable> unloadCallbacks = new HashSet<>();

	private File networksDir;
	private PersistentStateManager persistentStateManager;

	private <T extends Serializable> File getFile(Network<T> network) {
		return new File(networksDir, network.getId().toString() + ".dat");
	}

	public <T extends Serializable> void add(Network<T> network) {
		if (!(network instanceof PersistentState)) return;
		networks.computeIfAbsent(network.type(), __ -> new ArrayList<>()).add(network);
		persistentStateManager.set(network.getId().toString(), (PersistentState) network);
		try {
			File file = getFile(network);
			if (!file.exists()) file.createNewFile();
		} catch (IOException e) {
			// Ignore
		}
	}

	public <T extends Serializable> void remove(Network<T> network) {
		if (!(network instanceof PersistentState)) return;
		networks.computeIfAbsent(network.type(), __ -> new ArrayList<>()).remove(network);
		persistentStateManager.set(network.getId().toString(), null);
		getFile(network).delete();
	}

	public <T extends Serializable> @Nullable Network<T> get(UUID uuid) {
		return (Network<T>) networks.values()
				.stream()
				.flatMap(Collection::stream)
				.filter(network -> network.getId().equals(uuid))
				.findFirst()
				.orElse(null);
	}

	public Map<Type<?>, Network<?>> get(World world, BlockPos blockPos) {
		return networks.values().stream()
				.flatMap(Collection::stream)
				.filter(network -> network.cablePositions().containsKey(world))
				.filter(network -> network.cablePositions().get(world).contains(blockPos))
				.collect(Collectors.toMap(Network::type, network -> network));
	}

	public <T extends Serializable> List<Network<T>> get(Type<T> type) {
		return networks.getOrDefault(type, Collections.emptyList())
				.stream()
				.map(network -> (Network<T>) network)
				.collect(Collectors.toList());
	}

	public void loadCallback(Runnable runnable) {
		loadCallbacks.add(runnable);
	}

	public void unloadCallback(Runnable runnable) {
		unloadCallbacks.add(runnable);
	}

	public void loadNetworks(File networksDir, MinecraftServer minecraftServer) {
		if (persistentStateManager != null) return;
		this.networksDir = networksDir;
		networksDir.mkdirs();
		FlowApi.LOGGER.info("Loading networks...");
		persistentStateManager = new PersistentStateManager(networksDir, new DataFixer() {
			@Override
			public <T> Dynamic<T> update(DSL.TypeReference type, Dynamic<T> input, int version, int newVersion) {
				return input;
			}

			@Override
			public Schema getSchema(int key) {
				return new Schema(key, null);
			}
		});
		File[] files = networksDir.listFiles();
		if (files == null) return;

		Map<String, World> worlds = new HashMap<>();
		minecraftServer.getWorlds().forEach(serverWorld -> {
			worlds.put(serverWorld.getRegistryKey().getValue().toString(), serverWorld);
		});

		loadCallbacks.forEach(Runnable::run);
		for (File networkFile : files) {
			if (!networkFile.getName().endsWith(".dat")) continue;
			String name = networkFile.getName().substring(0, networkFile.getName().length() - 4);
			Network<?> network = persistentStateManager.get(nbtCompound -> new NetworkImpl<>(UUID.fromString(name), nbtCompound, worlds), name);
			networks.computeIfAbsent(network.type(), ignore -> new ArrayList<>()).add(network);
		}
	}

	public void tick() {
		networks.values().forEach(networks -> networks.forEach(Network::tick));
	}

	public void save() {
		if (persistentStateManager == null) return;
		FlowApi.LOGGER.info("Saving networks...");
		persistentStateManager.save();
	}

	public void unloadNetworks() {
		if (persistentStateManager == null) return;
		save();
		FlowApi.LOGGER.info("Unloading networks...");
		persistentStateManager = null;
		networks.clear();
		unloadCallbacks.forEach(Runnable::run);
	}
}
