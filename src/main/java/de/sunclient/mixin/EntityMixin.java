package de.sunclient.mixin;

import de.sunclient.Freelook;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Freelook: Mausbewegung geht an die Kamera statt an den Spieler. */
@Mixin(Entity.class)
public class EntityMixin {
	@Inject(method = "turn", at = @At("HEAD"), cancellable = true, require = 0)
	private void sunclient$turn(double yRot, double xRot, CallbackInfo ci) {
		if (Freelook.active && (Object) this == Minecraft.getInstance().player) {
			Freelook.turn(yRot, xRot);
			ci.cancel();
		}
	}
}
