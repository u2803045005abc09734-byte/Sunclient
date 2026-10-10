package de.sunclient;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;

/** Freelook: Kamera frei drehen, waehrend die Spielerblickrichtung gleich bleibt. */
public final class Freelook {
	private Freelook() {}

	public static boolean active = false;
	public static float yaw = 0f;
	public static float pitch = 0f;
	private static CameraType previous = null;

	public static void begin(Minecraft mc) {
		if (active || mc.player == null) return;
		active = true;
		yaw = mc.player.getYRot();
		pitch = mc.player.getXRot();
		previous = mc.options.getCameraType();
		mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
	}

	public static void end(Minecraft mc) {
		if (!active) return;
		active = false;
		if (previous != null) mc.options.setCameraType(previous);
		previous = null;
	}

	/** Mausbewegung (wie Entity.turn): dy = horizontal, dx = vertikal. */
	public static void turn(double dy, double dx) {
		yaw += (float) dy * 0.15f;
		pitch += (float) dx * 0.15f;
		if (pitch > 90f) pitch = 90f;
		if (pitch < -90f) pitch = -90f;
	}
}
