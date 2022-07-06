package de.flow.api;

import de.flow.impl.NetworkManager;
import lombok.experimental.UtilityClass;
import net.minecraft.world.PersistentState;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class Networks {

	public <T extends PersistentState & Network<C>, C> void add(T network) {
		NetworkManager.add(network);
	}

	public <T extends PersistentState & Network<C>, C> void remove(T network) {
		NetworkManager.remove(network);
	}

	public List<Type<?>> types() {
		return NetworkManager.types();
	}

	public <T extends Network<C>, C> List<T> get(Type<C> type) {
		return NetworkManager.get(type);
	}

	public <T extends Network<C>, C> T get(UUID uuid) {
		return NetworkManager.get(uuid);
	}
}
