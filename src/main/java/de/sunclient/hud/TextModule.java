package de.sunclient.hud;

import de.sunclient.SunConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** HUD-Modul mit einer Textzeile. */
public abstract class TextModule extends HudModule {
	private static final int PAD = 2;

	protected TextModule(String id, String name, int defX, int defY) {
		super(id, name, defX, defY);
	}

	/** Anzuzeigender Text oder null, wenn gerade nichts angezeigt werden soll. */
	protected abstract String text(Minecraft mc);

	private String safeText(Minecraft mc) {
		String t = text(mc);
		return t == null ? "" : t;
	}

	@Override
	public boolean hasContent(Minecraft mc) {
		return text(mc) != null;
	}

	@Override
	public int width(Minecraft mc) {
		return mc.font.width(safeText(mc)) + 2 * PAD;
	}

	@Override
	public int height(Minecraft mc) {
		return 9 + 2 * PAD;
	}

	@Override
	public void draw(GuiGraphics g, Minecraft mc, int x, int y) {
		if (SunConfig.INSTANCE.hudBackground) {
			g.fill(x, y, x + width(mc), y + height(mc), 0x66000000);
		}
		g.drawString(mc.font, safeText(mc), x + PAD, y + PAD, 0xFFFFFFFF, true);
	}
}
