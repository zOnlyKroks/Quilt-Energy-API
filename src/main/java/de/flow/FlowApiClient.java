package de.flow;

import de.flow.test.energy.EnergyBlockInit;
import de.flow.test.item.ItemBlockEntityInit;
import de.flow.test.item.ItemBlockInit;
import de.flow.test.item.ItemScreenHandled;
import de.flow.test.redstone.RedstoneBlockInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

@Environment(EnvType.CLIENT)
public class FlowApiClient implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
		EnergyBlockInit.onInitialize();

		RedstoneBlockInit.onInitialize();

		ItemBlockInit.onInitialize();
		ItemScreenHandled.onInitialize();
	}
}
