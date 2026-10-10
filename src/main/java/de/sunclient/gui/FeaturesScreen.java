package de.sunclient.gui;

import de.sunclient.SunConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Funktionen: Zoom, Toggle Sprint, Freelook, Ping-Modus, HUD-Hintergrund. */
public class FeaturesScreen extends SunScreen {
	private static final int[] FOV_STEPS = {30, 40, 50, 60};

	public FeaturesScreen(Screen parent) {
		super("Funktionen", parent);
	}

	private static String pingLabel() {
		return SunConfig.INSTANCE.pingMeasure ? "Ping: Messung (TCP)" : "Ping: Server (Tab-Liste)";
	}

	private static String fovLabel() {
		return "Zoom-FOV: " + SunConfig.INSTANCE.zoomFov;
	}

	@Override
	protected void init() {
		SunConfig c = SunConfig.INSTANCE;
		int left = this.width / 2 - 155;
		int right = this.width / 2 + 5;
		int top = 44;
		int step = 24;

		toggle(left, top, 150, "Toggle Sprint", () -> c.toggleSprint, v -> c.toggleSprint = v);
		toggle(right, top, 150, "Zoom", () -> c.zoomEnabled, v -> c.zoomEnabled = v);
		toggle(left, top + step, 150, "Freelook", () -> c.freelookEnabled, v -> c.freelookEnabled = v);
		toggle(right, top + step, 150, "HUD-Hintergrund", () -> c.hudBackground, v -> c.hudBackground = v);

		Button[] ping = new Button[1];
		ping[0] = addButton(pingLabel(), left, top + 2 * step, 150, () -> {
			c.pingMeasure = !c.pingMeasure;
			ping[0].setMessage(Component.literal(pingLabel()));
			SunConfig.save();
		});

		Button[] fov = new Button[1];
		fov[0] = addButton(fovLabel(), right, top + 2 * step, 150, () -> {
			int next = FOV_STEPS[0];
			for (int i = 0; i < FOV_STEPS.length; i++) {
				if (FOV_STEPS[i] == c.zoomFov) {
					next = FOV_STEPS[(i + 1) % FOV_STEPS.length];
					break;
				}
			}
			c.zoomFov = next;
			fov[0].setMessage(Component.literal(fovLabel()));
			SunConfig.save();
		});

		addButton("Zurueck", this.width / 2 - 100, top + 3 * step + 12, 200, this::onClose);
	}
}
