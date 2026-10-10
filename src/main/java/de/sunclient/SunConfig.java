package de.sunclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** JSON-Config: config/sunclient.json */
public class SunConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("sunclient.json");

	public static SunConfig INSTANCE = new SunConfig();

	public static class ModuleState {
		public boolean enabled = true;
		public int x;
		public int y;
	}

	/** Zustand (an/aus + Position) pro HUD-Modul. */
	public Map<String, ModuleState> modules = new LinkedHashMap<>();

	// Funktionen
	public boolean toggleSprint = false;
	public boolean zoomEnabled = true;
	public int zoomFov = 30;
	public boolean freelookEnabled = true;
	public boolean hudBackground = true;
	/** false = Ping aus der Tab-Liste (Server), true = eigene TCP-Messung zum Server. */
	public boolean pingMeasure = false;

	public ModuleState state(String id, int defX, int defY) {
		if (modules == null) modules = new LinkedHashMap<>();
		ModuleState s = modules.get(id);
		if (s == null) {
			s = new ModuleState();
			s.x = defX;
			s.y = defY;
			modules.put(id, s);
		}
		return s;
	}

	public static void load() {
		try {
			if (Files.exists(FILE)) {
				SunConfig cfg = GSON.fromJson(Files.readString(FILE), SunConfig.class);
				if (cfg != null) INSTANCE = cfg;
			}
		} catch (Exception e) {
			System.err.println("[SunClient] Config konnte nicht geladen werden: " + e);
		}
		save();
	}

	public static void save() {
		try {
			Files.createDirectories(FILE.getParent());
			Files.writeString(FILE, GSON.toJson(INSTANCE));
		} catch (IOException e) {
			System.err.println("[SunClient] Config konnte nicht gespeichert werden: " + e);
		}
	}
}
