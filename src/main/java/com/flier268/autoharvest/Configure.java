package com.flier268.autoharvest;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;

public class Configure {
    private File configFile;

   public FlowerISseed flowerISseed=new FlowerISseed();
    public class FlowerISseed{
        public boolean value = false;
        private String name = "flowerISseed";
    }

    public Effect_radius effect_radius=new Effect_radius();
    public class Effect_radius{
        public int value = 3;
        private String name = "effect_radius";
        public static final int Max = 3;
        public static final int Min = 0;
    }

    public Configure() {
        this.configFile = FabricLoader
                .getInstance()
                .getConfigDir()
                .resolve("AutoHarvest.json")
                .toFile();
        flowerISseed.value = false;
    }

    public Configure load() {
        try {
            if (!Files.exists(this.configFile.toPath()))
                return this;
            String jsonStr = new String(Files.readAllBytes(this.configFile.toPath()));
            if (!jsonStr.equals("")) {
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = jsonParser.parse(jsonStr).getAsJsonObject();

                this.flowerISseed.value = jsonObject.getAsJsonPrimitive(flowerISseed.name).getAsBoolean();
                this.effect_radius.value = jsonObject.getAsJsonPrimitive(effect_radius.name).getAsInt();
                if (effect_radius.value <= Effect_radius.Min || effect_radius.value > Effect_radius.Max)
                    effect_radius.value = Effect_radius.Max;
                return this;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Configure save() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(flowerISseed.name, this.flowerISseed.value);
        jsonObject.addProperty(effect_radius.name, this.effect_radius.value);

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
