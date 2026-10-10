package de.sunclient.gui;

import de.sunclient.hud.HudManager;
import de.sunclient.hud.HudModule;
import net.minecraft.client.gui.screens.Screen;

/** HUD-Module einzeln an- und ausschalten. */
public class ModulesScreen extends SunScreen {

	public ModulesScreen(Screen parent) {
		super("HUD-Module", parent);
	}

	@Override
	protected void init() {
		int left = this.width / 2 - 155;
		int right = this.width / 2 + 5;
		int top = 40;
		int step = 24;

		int i = 0;
		for (HudModule m : HudManager.MODULES) {
			int x = (i % 2 == 0) ? left : right;
			int y = top + (i / 2) * step;
			toggle(x, y, 150, m.name, m::isEnabled, m::setEnabled);
			i++;
		}

		int rows = (HudManager.MODULES.size() + 1) / 2;
		int by = top + rows * step + 8;
		addButton("Alle Positionen zuruecksetzen", this.width / 2 - 100, by, 200, () -> {
			for (HudModule m : HudManager.MODULES) m.resetPosition();
		});
		addButton("Zurueck", this.width / 2 - 100, by + 26, 200, this::onClose);
	}
}
