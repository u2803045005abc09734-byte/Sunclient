package de.sunclient.hud;

import de.sunclient.Stats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** WASD, Maustasten (mit CPS) und Leertaste. */
public class KeystrokesModule extends HudModule {
	private static final int S = 20;
	private static final int GAP = 2;

	public KeystrokesModule() {
		super("keystrokes", "Keystrokes", 4, 90);
	}

	@Override
	public int width(Minecraft mc) {
		return 3 * S + 2 * GAP;
	}

	@Override
	public int height(Minecraft mc) {
		return 3 * S + 3 * GAP + 12;
	}

	private static void box(GuiGraphics g, Minecraft mc, int x, int y, int w, int h, String label, boolean down) {
		g.fill(x, y, x + w, y + h, down ? 0xAAFFFFFF : 0x66000000);
		int tw = mc.font.width(label);
		g.drawString(mc.font, label, x + (w - tw) / 2, y + (h - 8) / 2, down ? 0xFF000000 : 0xFFFFFFFF, false);
	}

	@Override
	public void draw(GuiGraphics g, Minecraft mc, int x, int y) {
		box(g, mc, x + S + GAP, y, S, S, "W", mc.options.keyUp.isDown());
		box(g, mc, x, y + S + GAP, S, S, "A", mc.options.keyLeft.isDown());
		box(g, mc, x + S + GAP, y + S + GAP, S, S, "S", mc.options.keyDown.isDown());
		box(g, mc, x + 2 * (S + GAP), y + S + GAP, S, S, "D", mc.options.keyRight.isDown());

		int mouseY = y + 2 * (S + GAP);
		int mw = (width(mc) - GAP) / 2;
		box(g, mc, x, mouseY, mw, S, "LMB " + Stats.cpsLeft(), Stats.leftDown());
		box(g, mc, x + mw + GAP, mouseY, mw, S, "RMB " + Stats.cpsRight(), Stats.rightDown());

		box(g, mc, x, mouseY + S + GAP, width(mc), 12, "-----", mc.options.keyJump.isDown());
	}
}
