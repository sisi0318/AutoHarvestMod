package com.flier268.autoharvest;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class AutoHarvest implements ClientModInitializer {
    public static final String MOD_NAME = "autoharvest";
    public static AutoHarvest instance;
    public HarvestMode mode = HarvestMode.FISHING;
    public int overlayRemainingTick = 0;
    public TickListener listener = null;
    public KeyPressListener KeyListener = null;

    TaskManager taskManager = new TaskManager();

    public boolean Switch = false;
    public Configure configure = new Configure();

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
        FARMER,     //Harvest then re-plant
        SEED,       // Harvest seeds & flowers
        BONEMEALING,
        FEED,       // Feed animals
        FISHING,    // Fishing
        SPAWNPROOF;
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
            listener.Reset();
        this.mode = mode;
        return mode;
    }

    public HarvestMode toNextMode() {
        //setDisabled();
        if (listener == null) {
            listener = new TickListener(configure, MinecraftClient.getInstance().player);
        } else
            listener.Reset();
        mode = mode.next();
        return mode;
    }

    public static void msg(String key, Object... obj) {
        if (MinecraftClient.getInstance() == null)
            return;
        if (MinecraftClient.getInstance().player == null)
            return;
        MinecraftClient.getInstance().player.sendMessage(new LiteralText(new TranslatableText("notify.prefix").getString() + new TranslatableText(key, obj).getString()), true);
    }
}
