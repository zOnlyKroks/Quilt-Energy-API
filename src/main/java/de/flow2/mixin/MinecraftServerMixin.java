package de.flow2.mixin;

import de.flow2.impl.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.nio.file.Path;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

	@Shadow
	public abstract Path getSavePath(WorldSavePath worldSavePath);

	@Inject(method = "saveAllWorlds", at = @At("HEAD"))
	private void quilt_flow_api$saveNetworkOnWorldSave(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir) {
		NetworkManager.INSTANCE.save();
	}

	@Inject(method = "shutdown", at = @At("HEAD"))
	private void quilt_flow_api$unloadingNetworksOnWorldShutdown(CallbackInfo ci) {
		NetworkManager.INSTANCE.unloadNetworks();
	}

	@Inject(method = "createWorlds", at = @At("TAIL"))
	private void quilt_flow_api$loadingNetworksOnWorldCreation(CallbackInfo ci) {
		NetworkManager.INSTANCE.loadNetworks(new File(this.getSavePath(WorldSavePath.ROOT).toFile(), "networks"), ((MinecraftServer)(Object)this));
	}
}
