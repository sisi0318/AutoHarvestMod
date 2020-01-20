package com.flier268.autoharvest;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

public class Configure {
    private static Configure instance = null;
    private File configFile;

    private boolean flowerISseed;

    public Configure() {
        this.configFile = FabricLoader
                .getInstance()
                .getConfigDirectory()
                .toPath()
                .resolve("AutoHarvest.json")
                .toFile();
        flowerISseed = false;
    }

    public static Configure getConfig() {
        if (instance == null) {
            instance = new Configure();
        }
        return instance;
    }

    public void load() {
        try {
            String jsonStr = new String(Files.readAllBytes(this.configFile.toPath()));
            if (!jsonStr. equals("")) {
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonStr);
                this.flowerISseed = jsonObject.getAsJsonPrimitive("flowerISseed").getAsBoolean();
            }
        } catch (IOException e) {
        }
    }

    public void save() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("flowerISseed", this.flowerISseed);

        JsonParser parser = new JsonParser();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement el = parser.parse(jsonObject.toString());
        try (PrintWriter out = new PrintWriter(configFile)) {
            out.println(gson.toJson(el));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean getFlowerISseed() {
        return flowerISseed;
    }

    public void setFlowerISseed(boolean flowerISseed) {
        this.flowerISseed = flowerISseed;
    }
}
