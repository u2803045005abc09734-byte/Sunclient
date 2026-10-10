package de.sunclient.gui;

import de.sunclient.SunConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** Basis fuer alle Sun-Client-Menues: Titel, Button-Helfer, Zurueck-Verhalten. */
public abstract class SunScreen extends Screen {
	protected final Screen parent;

	protected SunScreen(String title, Screen parent) {
		super(Component.literal(title));
		this.parent = parent;
	}

	protected static Minecraft mc() {
		return Minecraft.getInstance();
	}

	protected Button addButton(String label, int x, int y, int w, Runnable action) {
		return addRenderableWidget(Button.builder(Component.literal(label), b -> action.run())
			.bounds(x, y, w, 20).build());
	}

	protected void toggle(int x, int y, int w, String name, BooleanSupplier get, Consumer<Boolean> set) {
		Button[] ref = new Button[1];
		ref[0] = addButton(label(name, get.getAsBoolean()), x, y, w, () -> {
			boolean now = !get.getAsBoolean();
			set.accept(now);
			ref[0].setMessage(Component.literal(label(name, now)));
			SunConfig.save();
		});
	}

	private static String label(String name, boolean on) {
		return name + ": " + (on ? "AN" : "AUS");
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
		super.render(g, mouseX, mouseY, delta);
		g.drawCenteredString(this.font, this.title, this.width / 2, 14, 0xFFFFC107);
	}

	@Override
	public void onClose() {
		SunConfig.save();
		mc().setScreen(parent);
	}
}
