package de.zonlykroks.energy;

import de.yoyosource.test.BlockInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class EnergyApiClient implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
		BlockInit.onInitialize();
	}
}
