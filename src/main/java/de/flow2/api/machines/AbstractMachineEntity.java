package de.flow2.api.machines;

import de.flow2.api.Type;
import de.flow2.api.networks.Network;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Field;
import java.util.*;

// TODO: Add JavaDoc
public abstract class AbstractMachineEntity extends BlockEntity implements MachineEntity {

	protected final Map<Type<?>, List<Typed<?>>> io;

	protected AbstractMachineEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);

		Map<Type<?>, List<Typed<?>>> io = new HashMap<>();
		for (Field field : this.getClass().getDeclaredFields()) {
			if (!field.isAnnotationPresent(IO.class)) continue;
			try {
				field.setAccessible(true);
				Typed<?> typed = (Typed<?>) field.get(this);
				io.computeIfAbsent(typed.type(), type -> new ArrayList<>()).add(typed);
			} catch (Exception e) {
				// ignore
			}
		}

		Map<Type<?>, List<Typed<?>>> temp = new HashMap<>();
		io.forEach((type, typeds) -> {
			temp.put(type, Collections.unmodifiableList(typeds));
		});
		this.io = Collections.unmodifiableMap(temp);
	}

	@Override
	public boolean hasType(Type<?> type) {
		return io.containsKey(type);
	}

	@Override
	public <T> void addIOToNetwork(Network<T> network) {
		io.getOrDefault(network.type(), Collections.emptyList()).forEach(typed -> {
			network.add(getWorld(), getPos(), (Typed<T>) typed);
		});
	}

	@Override
	public <T> void removeIOFromNetwork(Network<T> network) {
		io.getOrDefault(network.type(), Collections.emptyList()).forEach(typed -> {
			network.remove(getWorld(), getPos(), (Typed<T>) typed);
		});
	}
}
