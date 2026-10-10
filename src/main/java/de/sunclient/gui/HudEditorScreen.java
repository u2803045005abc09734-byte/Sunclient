package de.sunclient.gui;

import de.sunclient.SunConfig;
import de.sunclient.hud.HudManager;
import de.sunclient.hud.HudModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/** HUD-Editor: Module mit der linken Maustaste anklicken und verschieben. */
public class HudEditorScreen extends SunScreen {
	private HudModule dragging = null;
	private int offX = 0;
	private int offY = 0;
	private boolean wasDown = false;

	public HudEditorScreen(Screen parent) {
		super("HUD-Positionen bearbeiten", parent);
	}

	private static boolean isMouseDown() {
		long window = GLFW.glfwGetCurrentContext();
		return window != 0L
			&& GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
	}

	@Override
	protected void init() {
		addButton("Zuruecksetzen", this.width / 2 - 105, this.height - 28, 100, () -> {
			for (HudModule m : HudManager.MODULES) m.resetPosition();
		});
		addButton("Fertig", this.width / 2 + 5, this.height - 28, 100, this::onClose);
		// Maustaste kann noch vom Klick auf den Menue-Button gedrueckt sein
		wasDown = isMouseDown();
		dragging = null;
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
		super.render(g, mouseX, mouseY, delta);
		Minecraft mc = mc();

		g.drawCenteredString(this.font,
			Component.literal("Ziehe die Elemente mit der linken Maustaste an die gewuenschte Stelle"),
			this.width / 2, 30, 0xFFAAAAAA);

		boolean down = isMouseDown();
		boolean overButtons = mouseY > this.height - 34;

		HudManager.editing = true;
		try {
			// Neues Modul greifen (oberstes zuerst)
			if (down && !wasDown && !overButtons) {
				for (int i = HudManager.MODULES.size() - 1; i >= 0; i--) {
					HudModule m = HudManager.MODULES.get(i);
					if (!m.isEnabled()) continue;
					int[] p = HudManager.position(m, mc);
					if (mouseX >= p[0] && mouseX < p[0] + m.width(mc)
						&& mouseY >= p[1] && mouseY < p[1] + m.height(mc)) {
						dragging = m;
						offX = mouseX - p[0];
						offY = mouseY - p[1];
						break;
					}
				}
			}

			if (!down && dragging != null) {
				dragging = null;
				SunConfig.save();
			}

			if (dragging != null) {
				int sw = mc.getWindow().getGuiScaledWidth();
				int sh = mc.getWindow().getGuiScaledHeight();
				SunConfig.ModuleState s = dragging.state();
				s.x = Math.max(0, Math.min(mouseX - offX, sw - dragging.width(mc)));
				s.y = Math.max(0, Math.min(mouseY - offY, sh - dragging.height(mc)));
			}

			for (HudModule m : HudManager.MODULES) {
				if (!m.isEnabled()) continue;
				int[] p = HudManager.position(m, mc);
				int w = m.width(mc);
				int h = m.height(mc);
				m.draw(g, mc, p[0], p[1]);
				HudManager.outline(g, p[0], p[1], w, h, m == dragging ? 0xFFFFC107 : 0x88FFFFFF);
				g.drawString(mc.font, m.name, p[0], Math.max(0, p[1] - 10), 0xFFAAAAAA, false);
			}
		} finally {
			HudManager.editing = false;
		}

		wasDown = down;
	}

	@Override
	public void onClose() {
		HudManager.editing = false;
		super.onClose();
	}
}
