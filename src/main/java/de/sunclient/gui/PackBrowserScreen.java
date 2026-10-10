package de.sunclient.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Resource Packs auf Modrinth suchen und direkt in den resourcepacks-Ordner laden. */
public class PackBrowserScreen extends SunScreen {
	private static final String MC_VERSION = "1.21.11";
	private static final int MAX_RESULTS = 6;
	private static final HttpClient HTTP = HttpClient.newBuilder()
		.followRedirects(HttpClient.Redirect.NORMAL)
		.connectTimeout(Duration.ofSeconds(10))
		.build();

	private record Hit(String id, String title, String author, long downloads) {}

	private static String lastQuery = "";

	private EditBox searchBox;
	private final List<Button> resultButtons = new ArrayList<>();
	private volatile List<Hit> hits = List.of();
	private volatile int version = 0;
	private int shownVersion = -1;
	private volatile String status = "Suchbegriff eingeben und auf Suchen klicken.";

	public PackBrowserScreen(Screen parent) {
		super("Resource Packs (Modrinth)", parent);
	}

	@Override
	protected void init() {
		resultButtons.clear();

		searchBox = new EditBox(this.font, this.width / 2 - 150, 36, 200, 20, Component.literal("Suche"));
		searchBox.setValue(lastQuery);
		addRenderableWidget(searchBox);

		addButton("Suchen", this.width / 2 + 55, 36, 95, () -> search(searchBox.getValue()));

		for (int i = 0; i < MAX_RESULTS; i++) {
			final int index = i;
			Button b = addButton("", this.width / 2 - 150, 66 + i * 22, 300, () -> {
				List<Hit> current = hits;
				if (index < current.size()) download(current.get(index));
			});
			b.visible = false;
			resultButtons.add(b);
		}

		addButton("Zurueck", this.width / 2 - 100, this.height - 28, 200, this::onClose);

		shownVersion = -1;
		if (hits.isEmpty()) search(lastQuery);
	}

	private static String formatNumber(long n) {
		if (n >= 1_000_000) return (n / 100_000) / 10.0 + "M";
		if (n >= 1_000) return (n / 100) / 10.0 + "k";
		return String.valueOf(n);
	}

	private void search(String query) {
		lastQuery = query == null ? "" : query.trim();
		final String q = lastQuery;
		status = "Suche ...";
		CompletableFuture.runAsync(() -> {
			try {
				String facets = "[[\"project_type:resourcepack\"],[\"versions:" + MC_VERSION + "\"]]";
				String url = "https://api.modrinth.com/v2/search?query="
					+ URLEncoder.encode(q, StandardCharsets.UTF_8)
					+ "&facets=" + URLEncoder.encode(facets, StandardCharsets.UTF_8)
					+ "&limit=" + MAX_RESULTS
					+ (q.isEmpty() ? "&index=downloads" : "");
				String body = get(url);
				JsonObject root = JsonParser.parseString(body).getAsJsonObject();
				JsonArray arr = root.getAsJsonArray("hits");
				List<Hit> result = new ArrayList<>();
				for (JsonElement el : arr) {
					JsonObject o = el.getAsJsonObject();
					result.add(new Hit(
						o.get("project_id").getAsString(),
						o.get("title").getAsString(),
						o.has("author") ? o.get("author").getAsString() : "?",
						o.has("downloads") ? o.get("downloads").getAsLong() : 0L));
				}
				hits = result;
				version++;
				status = result.isEmpty()
					? "Nichts gefunden."
					: "Auf ein Pack klicken, um es herunterzuladen.";
			} catch (Exception e) {
				status = "Fehler bei der Suche: " + e.getMessage();
			}
		});
	}

	private void download(Hit hit) {
		status = "Lade " + hit.title() + " ...";
		CompletableFuture.runAsync(() -> {
			try {
				String gv = URLEncoder.encode("[\"" + MC_VERSION + "\"]", StandardCharsets.UTF_8);
				String body = get("https://api.modrinth.com/v2/project/" + hit.id() + "/version?game_versions=" + gv);
				JsonArray versions = JsonParser.parseString(body).getAsJsonArray();
				if (versions.isEmpty()) {
					status = "Keine Version fuer " + MC_VERSION + " gefunden.";
					return;
				}
				JsonArray files = versions.get(0).getAsJsonObject().getAsJsonArray("files");
				JsonObject chosen = null;
				for (JsonElement el : files) {
					JsonObject f = el.getAsJsonObject();
					if (chosen == null) chosen = f;
					if (f.has("primary") && f.get("primary").getAsBoolean()) {
						chosen = f;
						break;
					}
				}
				if (chosen == null) {
					status = "Keine Datei gefunden.";
					return;
				}
				String fileUrl = chosen.get("url").getAsString();
				String fileName = chosen.get("filename").getAsString().replaceAll("[\\\\/:*?\"<>|]", "_");

				Path dir = FabricLoader.getInstance().getGameDir().resolve("resourcepacks");
				Files.createDirectories(dir);
				Path target = dir.resolve(fileName);
				Path tmp = dir.resolve(fileName + ".part");

				HttpRequest req = HttpRequest.newBuilder(URI.create(fileUrl))
					.header("User-Agent", "SunClient/1.0 (Fabric mod)")
					.timeout(Duration.ofMinutes(5))
					.GET().build();
				HttpResponse<Path> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofFile(tmp));
				if (resp.statusCode() != 200) {
					Files.deleteIfExists(tmp);
					status = "Download fehlgeschlagen (HTTP " + resp.statusCode() + ").";
					return;
				}
				Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);

				status = "Fertig: " + fileName + " - jetzt unter Optionen > Ressourcenpakete aktivieren.";
				Minecraft mc = Minecraft.getInstance();
				mc.execute(() -> mc.getResourcePackRepository().reload());
			} catch (Exception e) {
				status = "Fehler beim Download: " + e.getMessage();
			}
		});
	}

	private static String get(String url) throws Exception {
		HttpRequest req = HttpRequest.newBuilder(URI.create(url))
			.header("User-Agent", "SunClient/1.0 (Fabric mod)")
			.timeout(Duration.ofSeconds(15))
			.GET().build();
		HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
		if (resp.statusCode() != 200) throw new IllegalStateException("HTTP " + resp.statusCode());
		return resp.body();
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
		// Ergebnisliste in die Buttons uebernehmen, sobald neue Ergebnisse da sind
		if (shownVersion != version) {
			List<Hit> current = hits;
			for (int i = 0; i < resultButtons.size(); i++) {
				Button b = resultButtons.get(i);
				if (i < current.size()) {
					Hit h = current.get(i);
					String text = h.title() + "  -  " + h.author() + "  (" + formatNumber(h.downloads()) + " Downloads)";
					if (text.length() > 52) text = text.substring(0, 49) + "...";
					b.setMessage(Component.literal(text));
					b.visible = true;
				} else {
					b.visible = false;
				}
			}
			shownVersion = version;
		}

		super.render(g, mouseX, mouseY, delta);
		g.drawCenteredString(this.font, Component.literal(status), this.width / 2, this.height - 46, 0xFFFFFFFF);
	}
}
