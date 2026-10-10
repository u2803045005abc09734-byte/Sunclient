package de.sunclient;

import com.mojang.blaze3d.platform.InputConstants;
import de.sunclient.gui.SunMenuScreen;
import de.sunclient.hud.HudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class SunClient implements ClientModInitializer {
	public static KeyMapping menuKey;
	public static KeyMapping zoomKey;
	public static KeyMapping sprintToggleKey;
	public static KeyMapping freelookKey;

	/** FOV vor dem Zoomen, null = nicht am Zoomen. */
	private static Integer savedFov = null;

	@Override
	public void onInitializeClient() {
		SunConfig.load();

		menuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.sunclient.menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, KeyMapping.Category.MISC));
		zoomKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.sunclient.zoom", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, KeyMapping.Category.MISC));
		sprintToggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.sunclient.togglesprint", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, KeyMapping.Category.MISC));
		freelookKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.sunclient.freelook", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, KeyMapping.Category.MISC));

		HudRenderer.register();
		ClientTickEvents.END_CLIENT_TICK.register(SunClient::onTick);
	}

	private static void onTick(Minecraft mc) {
		SunConfig cfg = SunConfig.INSTANCE;

		while (menuKey.consumeClick()) {
			if (mc.screen == null) mc.setScreen(new SunMenuScreen(null));
		}
		while (sprintToggleKey.consumeClick()) {
			cfg.toggleSprint = !cfg.toggleSprint;
			SunConfig.save();
		}

		if (mc.player == null) {
			savedFov = null;
			Freelook.active = false;
			return;
		}

		if (cfg.pingMeasure) Stats.ensureTcpThread();

		// Toggle Sprint
		if (cfg.toggleSprint && mc.screen == null && mc.options.keyUp.isDown()) {
			mc.options.keySprint.setDown(true);
		}

		// Zoom
		boolean zooming = cfg.zoomEnabled && zoomKey.isDown() && mc.screen == null;
		if (zooming) {
			if (savedFov == null) savedFov = mc.options.fov().get();
			mc.options.fov().set(Math.max(30, Math.min(110, cfg.zoomFov)));
		} else if (savedFov != null) {
			mc.options.fov().set(savedFov);
			savedFov = null;
		}

		// Freelook (Taste halten)
		boolean look = cfg.freelookEnabled && freelookKey.isDown() && mc.screen == null;
		if (look) {
			Freelook.begin(mc);
		} else {
			Freelook.end(mc);
		}
	}
}
