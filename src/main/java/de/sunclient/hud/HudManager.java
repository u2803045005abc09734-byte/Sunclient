package de.sunclient.hud;

import de.sunclient.Stats;
import de.sunclient.SunConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class HudManager {
	private HudManager() {}

	/** true nur waehrend der HUD-Editor zeichnet: Module zeigen dann Beispielinhalte. */
	public static boolean editing = false;

	public static final List<HudModule> MODULES = new ArrayList<>();

	static {
		MODULES.add(new TextModule("fps", "FPS", 4, 4) {
			@Override
			protected String text(Minecraft mc) {
				return "FPS: " + Stats.fps();
			}
		});
		MODULES.add(new TextModule("cps", "CPS", 4, 17) {
			@Override
			protected String text(Minecraft mc) {
				return "CPS: " + Stats.cpsLeft() + " | " + Stats.cpsRight();
			}
		});
		MODULES.add(new TextModule("ping", "Ping", 4, 30) {
			@Override
			protected String text(Minecraft mc) {
				int ping = Stats.ping(mc);
				return ping < 0 ? "Ping: -" : "Ping: " + ping + " ms";
			}
		});
		MODULES.add(new TextModule("coords", "Koordinaten", 4, 43) {
			@Override
			protected String text(Minecraft mc) {
				if (mc.player == null) return "XYZ: 0.0 / 64.0 / 0.0";
				return String.format(Locale.ROOT, "XYZ: %.1f / %.1f / %.1f",
					mc.player.getX(), mc.player.getY(), mc.player.getZ());
			}
		});
		MODULES.add(new TextModule("direction", "Richtung", 4, 56) {
			@Override
			protected String text(Minecraft mc) {
				if (mc.player == null) return "Richtung: North";
				String dir = mc.player.getDirection().getName();
				return "Richtung: " + dir.substring(0, 1).toUpperCase(Locale.ROOT) + dir.substring(1);
			}
		});
		MODULES.add(new TextModule("sprint", "Sprint-Status", 4, 69) {
			@Override
			protected String text(Minecraft mc) {
				if (editing || SunConfig.INSTANCE.toggleSprint) return "[Sprint: Toggled]";
				if (mc.options.keySprint.isDown()) return "[Sprint: Gehalten]";
				return null;
			}
		});
		MODULES.add(new KeystrokesModule());
		MODULES.add(new ArmorModule());
	}

	/** Position eines Moduls, auf den sichtbaren Bildschirm begrenzt. */
	public static int[] position(HudModule m, Minecraft mc) {
		SunConfig.ModuleState s = m.state();
		int sw = mc.getWindow().getGuiScaledWidth();
		int sh = mc.getWindow().getGuiScaledHeight();
		int x = Math.max(0, Math.min(s.x, sw - m.width(mc)));
		int y = Math.max(0, Math.min(s.y, sh - m.height(mc)));
		return new int[] {x, y};
	}

	public static void renderAll(GuiGraphics g, Minecraft mc) {
		for (HudModule m : MODULES) {
			if (!m.isEnabled() || !m.hasContent(mc)) continue;
			int[] p = position(m, mc);
			m.draw(g, mc, p[0], p[1]);
		}
	}

	/** Rahmen (4 duenne Rechtecke). */
	public static void outline(GuiGraphics g, int x, int y, int w, int h, int color) {
		g.fill(x - 1, y - 1, x + w + 1, y, color);
		g.fill(x - 1, y + h, x + w + 1, y + h + 1, color);
		g.fill(x - 1, y, x, y + h, color);
		g.fill(x + w, y, x + w + 1, y + h, color);
	}
}
