package de.flow2.api;

import de.flow.FlowApi;
import de.flow2.api.utils.ItemStackContainer;
import de.flow2.impl.TypeManager;
import lombok.experimental.UtilityClass;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@UtilityClass
public class Types {

	public static void init() {
		register(ENERGY_TYPE, new Identifier(FlowApi.MODID, "energy"));
		register(REDSTONE_TYPE, new Identifier(FlowApi.MODID, "redstone"));
		register(ITEM_TYPE, new Identifier(FlowApi.MODID, "item"));
	}

	public static final Type<Double> ENERGY_TYPE = new EnergyType();
	public static final Type<Integer> REDSTONE_TYPE = new RedstoneType();
	public static final Type<Map<ItemStackContainer, Long>> ITEM_TYPE = new ItemType();

	public List<Type<?>> types() {
		return TypeManager.INSTANCE.types();
	}

	public <T> void register(Type<T> type, Identifier name) {
		TypeManager.INSTANCE.register(type, name);
	}

	public <T> boolean isRegistered(Type<T> type) {
		return TypeManager.INSTANCE.isRegistered(type);
	}

	public boolean isRegistered(Identifier name) {
		return TypeManager.INSTANCE.isRegistered(name);
	}

	public <T> @Nullable Type<T> type(Identifier name) {
		return TypeManager.INSTANCE.type(name);
	}

	public <T> @Nullable Identifier type(Type<T> type) {
		return TypeManager.INSTANCE.type(type);
	}
}
