package de.flow.mixin;

import de.flow.impl.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

	@Inject(method = "shutdown", at = @At("HEAD"))
	public void shutdown(CallbackInfo ci) {
		NetworkManager.unloadNetworks();
	}

	@Inject(method = "createWorlds", at = @At("TAIL"))
	public void createWorlds(CallbackInfo ci) {
		NetworkManager.loadNetworks(new File(((MinecraftServer)(Object)this).getSavePath(WorldSavePath.ROOT).toFile(), "networks"), ((MinecraftServer)(Object)this));
	}
}
