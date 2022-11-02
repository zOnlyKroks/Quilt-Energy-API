package de.flow2.api.machines;

import de.flow2.api.Type;
import de.flow2.api.TypeCheck;
import de.flow2.api.networks.Network;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.lang.reflect.Field;

// TODO: Add JavaDoc
public interface MachineEntity extends TypeCheck {

	default Direction[] ports() {
		return Direction.values();
	}

	BlockPos getPos();

	World getWorld();

	default boolean hasType(Type<?> type) {
		for (Field field : this.getClass().getDeclaredFields()) {
			if (!field.isAnnotationPresent(IO.class)) continue;
			try {
				field.setAccessible(true);
				Typed<?> typed = (Typed<?>) field.get(this);
				if (typed.type() == type) return true;
			} catch (Exception e) {
				// ignore
			}
		}
		return false;
	}

	default void iterate(TriConsumer<World, BlockPos, Typed<?>> consumer) {
		for (Field field : this.getClass().getDeclaredFields()) {
			if (!field.isAnnotationPresent(IO.class)) continue;
			try {
				field.setAccessible(true);
				Typed<?> typed = (Typed<?>) field.get(this);
				consumer.accept(getWorld(), getPos(), typed);
			} catch (Exception e) {
				// ignore
			}
		}
	}

	interface TriConsumer<T, U, V> {
		void accept(T t, U u, V v);
	}

	default <T> void addIOToNetwork(Network<T> network) {
		iterate((world, blockPos, typed) -> {
			if (typed.type() == network.type()) {
				network.add(world, blockPos, (Typed<T>) typed);
			}
		});
	}

	default <T> void removeIOFromNetwork(Network<T> network) {
		iterate((world, blockPos, typed) -> {
			if (typed.type() == network.type()) {
				network.remove(world, blockPos, (Typed<T>) typed);
			}
		});
	}
}
