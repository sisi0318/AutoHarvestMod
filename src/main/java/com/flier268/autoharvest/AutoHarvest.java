package com.flier268.autoharvest;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

public class AutoHarvest implements ClientModInitializer {
    public static final String MOD_NAME = "autoharvest";
    public static AutoHarvest instance;
    public HarvestMode mode = HarvestMode.FISHING;
    public int overlayRemainingTick = 0;
    public TickListener listener = null;
    public KeyPressListener KeyListener = null;

    TaskManager taskManager = new TaskManager();

    public boolean Switch = false;
    public Configuration configure = new Configuration();

    @Override
    public void onInitializeClient() {
        if (AutoHarvest.instance == null)
            AutoHarvest.instance = new AutoHarvest();
        if (AutoHarvest.instance.KeyListener == null) {
            AutoHarvest.instance.KeyListener = new KeyPressListener();
        }
        AutoHarvest.instance.configure.load();
    }

    public enum HarvestMode {
        HARVEST,    // Harvest only
        PLANT,      // Plant only
        FARMER,     // Harvest then re-plant
        SEED,       // Harvest seeds & flowers
        BONEMEALING,
        FEED,       // Feed animals
        FISHING,    // Fishing
        SPAWNPROOF; // Spawnproofing
        private static final HarvestMode[] VALUES = values();

        public AutoHarvest.HarvestMode next() {
            return VALUES[(this.ordinal() + 1) % VALUES.length];
        }
    }


    public HarvestMode toSpecifiedMode(HarvestMode mode) {
        //setDisabled();
        if (listener == null) {
            listener = new TickListener(configure, MinecraftClient.getInstance().player);
        } else
            listener.reset();
        this.mode = mode;
        return mode;
    }

    public HarvestMode toNextMode() {
        //setDisabled();
        if (listener == null) {
            listener = new TickListener(configure, MinecraftClient.getInstance().player);
        } else
            listener.reset();
        mode = mode.next();
        return mode;
    }

    public static void msg(String key, Object... obj) {
        if (MinecraftClient.getInstance() == null)
            return;
        if (MinecraftClient.getInstance().player == null)
            return;
        MinecraftClient.getInstance().player.sendMessage(
                Text.of(
                        String.format("%s %s",
                                new TranslatableTextContent("notify.prefix"),
                                new TranslatableTextContent(key, obj)
                        )
                )
                , true);
    }
}
