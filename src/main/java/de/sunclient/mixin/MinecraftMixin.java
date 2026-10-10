package de.sunclient.mixin;

import de.sunclient.Stats;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Zaehlt jeden echten Frame (fuer genaue FPS und CPS), unabhaengig vom HUD-Rendering. */
@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Inject(method = "runTick", at = @At("HEAD"), require = 0)
	private void sunclient$onFrame(boolean renderLevel, CallbackInfo ci) {
		Stats.onFrame();
	}
}
