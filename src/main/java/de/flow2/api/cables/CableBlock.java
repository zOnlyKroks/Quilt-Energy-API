package de.flow2.api.cables;

import com.google.common.collect.ImmutableList;
import de.flow2.api.Type;
import de.flow2.api.TypeCheck;
import lombok.NonNull;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

// TODO: Add JavaDoc
public interface CableBlock extends TypeCheck {

	/**
	 * <b>Contract:</b>
	 * <ul>
	 * <li>Calling {@link #types()} twice should return the same object comparable by {@code ==}</li>
	 * <li>Calling {@link #types()} should never return null</li>
	 * </ul>
	 *
	 * @return the types the cable should be able to transfer
	 */
	@NonNull ImmutableList<Type<?>> types();

	@Override
	default boolean hasType(Type<?> type) {
		return types().stream()
				.anyMatch(t -> t == type);
	}

	boolean recalculateDirection(World world, BlockPos pos, Direction direction, boolean place);
}
