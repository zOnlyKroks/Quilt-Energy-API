package de.flow.impl;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import de.flow.FlowApi;
import de.flow.api.Network;
import de.flow.api.Type;
import lombok.experimental.UtilityClass;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.quiltmc.qsl.lifecycle.api.event.ServerTickEvents;
import org.quiltmc.qsl.lifecycle.api.event.ServerWorldLoadEvents;
import org.quiltmc.qsl.networking.api.ServerLoginConnectionEvents;

import java.io.File;
import java.util.*;

@UtilityClass
public class NetworkManager {

	private Map<Type<?>, List<Network<?>>> networks = new HashMap<>();

	private File networksDir;
	private PersistentStateManager persistentStateManager;

	public <T extends PersistentState & Network<C>, C> void add(T network) {
		networks.computeIfAbsent(network.type(), ignore -> new ArrayList<>()).add(network);
		persistentStateManager.set(network.getId().toString(), network);
	}

	public <T extends PersistentState & Network<C>, C> void remove(T network) {
		networks.computeIfAbsent(network.type(), ignore -> new ArrayList<>()).remove(network);
		persistentStateManager.set(network.getId().toString(), null);
		new File(networksDir, network.getId().toString()).delete();
	}

	public void loadNetworks(File networksDir, MinecraftServer minecraftServer) {
		if (persistentStateManager != null) return;
		NetworkManager.networksDir = networksDir;
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
		for (File networkFile : files) {
			if (!networkFile.getName().endsWith(".dat")) continue;
			String name = networkFile.getName().substring(0, networkFile.getName().length() - 4);
			Network<?> network = persistentStateManager.get(nbtCompound -> new NetworkImpl<>(UUID.fromString(name), nbtCompound, minecraftServer), name);
			networks.computeIfAbsent(network.type(), ignore -> new ArrayList<>()).add(network);
		}
	}

	public void unloadNetworks() {
		if (persistentStateManager == null) return;
		FlowApi.LOGGER.info("Unloading networks...");
		persistentStateManager.save();
		persistentStateManager = null;
		networks.clear();
	}
}
