package de.sunclient.hud;

import de.sunclient.SunConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** Ein verschiebbares HUD-Element. */
public abstract class HudModule {
	public final String id;
	public final String name;
	private final int defX;
	private final int defY;

	protected HudModule(String id, String name, int defX, int defY) {
		this.id = id;
		this.name = name;
		this.defX = defX;
		this.defY = defY;
	}

	public SunConfig.ModuleState state() {
		return SunConfig.INSTANCE.state(id, defX, defY);
	}

	public boolean isEnabled() {
		return state().enabled;
	}

	public void setEnabled(boolean enabled) {
		state().enabled = enabled;
	}

	public void resetPosition() {
		SunConfig.ModuleState s = state();
		s.x = defX;
		s.y = defY;
	}

	/** false = gerade nichts anzuzeigen (z.B. Sprint-Status). Im Editor wird immer eine Vorschau gezeigt. */
	public boolean hasContent(Minecraft mc) {
		return true;
	}

	public abstract int width(Minecraft mc);

	public abstract int height(Minecraft mc);

	public abstract void draw(GuiGraphics g, Minecraft mc, int x, int y);
}
