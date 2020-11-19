package com.flier268.autoharvest;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;

public class Configure {
    private File configFile;

    public boolean flowerISseed = false;
    private String property_flowerISseed = "flowerISseed";
    public int effect_radius = 3;
    private String property_effect_radius = "effect_radius";
    public static final int effect_radiusMax = 3;
    public static final int effect_radiusMin = 0;
    public Configure() {
        this.configFile = FabricLoader
                .getInstance()
                .getConfigDir()
                .resolve("AutoHarvest.json")
                .toFile();
        flowerISseed = false;
    }

    public Configure load() {
        try {
            if (!Files.exists(this.configFile.toPath()))
                return this;
            String jsonStr = new String(Files.readAllBytes(this.configFile.toPath()));
            if (!jsonStr.equals("")) {
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = jsonParser.parse(jsonStr).getAsJsonObject();

                this.flowerISseed = jsonObject.getAsJsonPrimitive(property_flowerISseed).getAsBoolean();
                this.effect_radius = jsonObject.getAsJsonPrimitive(property_effect_radius).getAsInt();
                if (effect_radius <= effect_radiusMin || effect_radius > effect_radiusMax)
                    effect_radius = effect_radiusMax;
                return this;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Configure save() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(property_flowerISseed, this.flowerISseed);
        jsonObject.addProperty(property_effect_radius, this.effect_radius);

        JsonParser parser = new JsonParser();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement el = parser.parse(jsonObject.toString());
        try (PrintWriter out = new PrintWriter(configFile)) {
            out.println(gson.toJson(el));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return this;
    }
}
