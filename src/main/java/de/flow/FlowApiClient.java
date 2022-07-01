package de.flow;

import de.flow.test.BlockInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

@Environment(EnvType.CLIENT)
public class FlowApiClient implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
		BlockInit.onInitialize();
	}
}
