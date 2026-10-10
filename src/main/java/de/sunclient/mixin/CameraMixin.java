package de.sunclient.mixin;

import de.sunclient.Freelook;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Freelook: Die Kamera benutzt die Freelook-Blickrichtung statt der Spieler-Blickrichtung. */
@Mixin(Camera.class)
public class CameraMixin {
	@Redirect(method = "setup",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getViewYRot(F)F"),
		require = 0)
	private float sunclient$yaw(Entity entity, float partialTick) {
		if (Freelook.active && entity == Minecraft.getInstance().player) return Freelook.yaw;
		return entity.getViewYRot(partialTick);
	}

	@Redirect(method = "setup",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getViewXRot(F)F"),
		require = 0)
	private float sunclient$pitch(Entity entity, float partialTick) {
		if (Freelook.active && entity == Minecraft.getInstance().player) return Freelook.pitch;
		return entity.getViewXRot(partialTick);
	}
}
