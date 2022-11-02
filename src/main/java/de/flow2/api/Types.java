package de.flow2.api;

import de.flow2.impl.TypeManager;
import lombok.experimental.UtilityClass;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

@UtilityClass
public class Types {

	public List<Type<?>> types() {
		return TypeManager.INSTANCE.types();
	}

	public <T extends Serializable> void register(Type<T> type, Identifier name) {
		TypeManager.INSTANCE.register(type, name);
	}

	public <T extends Serializable> boolean isRegistered(Type<T> type) {
		return TypeManager.INSTANCE.isRegistered(type);
	}

	public boolean isRegistered(Identifier name) {
		return TypeManager.INSTANCE.isRegistered(name);
	}

	public <T extends Serializable> @Nullable Type<T> type(Identifier name) {
		return TypeManager.INSTANCE.type(name);
	}

	public <T extends Serializable> @Nullable Identifier type(Type<T> type) {
		return TypeManager.INSTANCE.type(type);
	}
}
