package de.flow2.impl;

import de.flow2.api.Type;
import lombok.NonNull;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeManager {

	public static final TypeManager INSTANCE = new TypeManager();

	private Map<Identifier, Type<?>> typeMap = new HashMap<>();
	private Map<Type<?>, Identifier> nameMap = new HashMap<>();

	public List<Type<?>> types() {
		return new ArrayList<>(typeMap.values());
	}

	public <T> void register(@NonNull Type<T> type, @NonNull Identifier name) {
		if (typeMap.containsKey(name)) {
			if (typeMap.get(name) != type) {
				throw new IllegalArgumentException("Type with name '" + name + "' already registered");
			} else {
				return;
			}
		}
		if (nameMap.containsKey(type)) {
			if (!nameMap.get(type).equals(name)) {
				throw new IllegalArgumentException("Type '" + type + "' already registered with name '" + nameMap.get(type) + "'");
			} else {
				return;
			}
		}
		typeMap.put(name, type);
		nameMap.put(type, name);
	}

	public <T> boolean isRegistered(@NonNull Type<T> type) {
		return nameMap.containsKey(type);
	}

	public boolean isRegistered(@NonNull Identifier name) {
		return typeMap.containsKey(name);
	}

	public <T> @Nullable Type<T> type(@NonNull Identifier name) {
		return (Type<T>) typeMap.get(name);
	}

	public <T> @Nullable Identifier type(@NonNull Type<T> type) {
		return nameMap.get(type);
	}
}
