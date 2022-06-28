package de.zonlykroks.energy;

import de.yoyosource.test.ModInit;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnergyApi implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("EnergyAPI");

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Initializing Quilt Energy Api v: " + mod.metadata().version().raw());
		new ModInit();
	}
}
