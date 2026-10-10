package de.sunclient.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Hauptmenue (Standard: Rechte Shift-Taste). */
public class SunMenuScreen extends SunScreen {

	public SunMenuScreen(Screen parent) {
		super("Sun Client", parent);
	}

	@Override
	protected void init() {
		int cx = this.width / 2 - 110;
		int y = this.height / 2 - 62;
		int step = 26;

		addButton("HUD-Positionen bearbeiten", cx, y, 220, () -> mc().setScreen(new HudEditorScreen(this)));
		addButton("HUD-Module an/aus", cx, y + step, 220, () -> mc().setScreen(new ModulesScreen(this)));
		addButton("Funktionen (Zoom, Sprint, Freelook ...)", cx, y + 2 * step, 220,
			() -> mc().setScreen(new FeaturesScreen(this)));
		addButton("Resource Packs von Modrinth", cx, y + 3 * step, 220,
			() -> mc().setScreen(new PackBrowserScreen(this)));
		addButton("Fertig", cx, y + 4 * step + 8, 220, this::onClose);
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
		super.render(g, mouseX, mouseY, delta);
		g.drawCenteredString(this.font,
			Component.literal("Zoom: C (halten)  |  Toggle Sprint: R  |  Freelook: Alt (halten)"),
			this.width / 2, this.height / 2 + 70, 0xFFAAAAAA);
	}
}
