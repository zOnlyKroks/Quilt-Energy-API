package de.flow;

import de.flow.test.BlockEntityInit;
import de.flow.test.BlockInit;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowApi implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("EnergyAPI");

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Initializing Quilt Energy Api v: " + mod.metadata().version().raw());
		BlockEntityInit.onInitialize();
		BlockInit.onInitialize();
	}
}
