package de.flow.impl;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import de.flow.FlowApi;
import de.flow.api.Network;
import de.flow.api.NetworkBlock;
import de.flow.api.Type;
import lombok.experimental.UtilityClass;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.io.File;
import java.io.IOException;
import java.util.*;

@UtilityClass
public class NetworkManager {

	private Map<Type<?>, List<Network<?>>> networks = new HashMap<>();
	private Set<Runnable> loadCallbacks = new HashSet<>();
	private Set<Runnable> unloadCallbacks = new HashSet<>();

	private File networksDir;
	private PersistentStateManager persistentStateManager;

	public <T extends Network<C>, C> void add(T network) {
		if (!(network instanceof PersistentState)) return;
		networks.computeIfAbsent(network.type(), ignore -> new ArrayList<>()).add(network);
		persistentStateManager.set(network.getId().toString(), (PersistentState) network);
		try {
			File file = new File(networksDir, network.getId().toString() + ".dat");
			if (!file.exists()) file.createNewFile();
		} catch (IOException e) {
			// Ignore
		}
	}

	public <T extends Network<C>, C> void remove(T network) {
		if (!(network instanceof PersistentState)) return;
		networks.computeIfAbsent(network.type(), ignore -> new ArrayList<>()).remove(network);
		persistentStateManager.set(network.getId().toString(), null);
		new File(networksDir, network.getId().toString() + ".dat").delete();
	}

	public List<Type<?>> types() {
		return new ArrayList<>(networks.keySet());
	}

	public <T extends Network<C>, C> List<T> get(Type<C> type) {
		return (List<T>) networks.getOrDefault(type, Collections.emptyList());
	}

	public <T extends Network<C>, C> T get(World world, BlockPos blockPos) {
		for (Type<?> type : networks.keySet()) {
			Network<?> network = get(type, world, blockPos);
			if (network != null) {
				return (T) network;
			}
		}
		return null;
	}

	public <T extends Network<C>, C> T get(Type<C> type, World world, BlockPos blockPos) {
		return (T) networks.getOrDefault(type, Collections.emptyList()).stream().filter(network -> {
			if (!network.cablePositions().containsKey(world)) return false;
			return network.cablePositions().get(world).contains(blockPos);
		}).findFirst().orElse(null);
	}

	public <T extends Network<C>, C> T get(UUID uuid) {
		return (T) networks.values().stream().flatMap(Collection::stream).filter(network -> network.getId().equals(uuid)).findFirst().orElse(null);
	}

	public void loadCallback(Runnable runnable) {
		loadCallbacks.add(runnable);
	}

	public void unloadCallback(Runnable runnable) {
		unloadCallbacks.add(runnable);
	}

	public void tick() {
		for (Map.Entry<Type<?>, List<Network<?>>> networkEntry : networks.entrySet()) {
			List<Network<?>> toTick = networkEntry.getValue()
					.stream()
					.filter(Network::needsTick)
					.peek(Network::calculateAmounts)
					.filter(network -> {
						if (!network.hasTransmitter()) {
							network.calculateWithoutTransmitter();
							return false;
						} else {
							return true;
						}
					})
					.toList();
			if (toTick.isEmpty()) continue;
			Map<NetworkBlock.TransmitterIdentifier, TransmitterData<?>> data = new HashMap<>();
			toTick.stream().map(Network::calculateTransmitterLimits).flatMap(Collection::stream).forEach(limit -> {
				data.put(limit, new TransmitterData<>(networkEntry.getKey()));
			});
			toTick.forEach(network -> network.calculateTransmitterNeededOrProvided((Map) data));
			data.forEach((transmitterIdentifier, transmitterData) -> transmitterData.balance());
			System.out.println(data);
			// TODO: Add ticking for with transmitter
			// toTick.forEach(Network::tick);
		}
	}

	public void loadNetworks(File networksDir, MinecraftServer minecraftServer) {
		if (persistentStateManager != null) return;
		NetworkManager.networksDir = networksDir;
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
