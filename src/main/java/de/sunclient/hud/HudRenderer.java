package de.sunclient.hud;

import de.sunclient.Stats;
import de.sunclient.gui.HudEditorScreen;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class HudRenderer {
	private HudRenderer() {}

	public static void register() {
		HudRenderCallback.EVENT.register(HudRenderer::render);
	}

	private static void render(GuiGraphics g, DeltaTracker delta) {
		Stats.onHudFrame();
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.options.hideGui) return;
		// Im HUD-Editor zeichnet der Editor die Module selbst
		if (mc.screen instanceof HudEditorScreen) return;
		HudManager.renderAll(g, mc);
	}
}
