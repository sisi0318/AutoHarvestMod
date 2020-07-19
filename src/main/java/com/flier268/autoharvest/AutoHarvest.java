package com.flier268.autoharvest;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class AutoHarvest implements ClientModInitializer {
    public static String MOD_NAME = "autoharvest";
    public boolean Switch = false;

    @Override
    public void onInitializeClient() {
        if (AutoHarvest.instance == null)
            AutoHarvest.instance = new AutoHarvest();
        if (AutoHarvest.instance.KeyListener == null) {
            AutoHarvest.instance.KeyListener = new KeyPressListener();
        }
        Configure.getConfig().load();
    }

    public enum HarvestMode {
        HARVEST,  // Harvest only
        PLANT,  // Plant only
        Farmer,  //Harvest then re-plant
        SEED,   // Harvest seeds & flowers
        FEED,   // Feed animals
        FISHING;// Fishing
        private static HarvestMode[] vals = values();

        public AutoHarvest.HarvestMode next() {
            return vals[(this.ordinal() + 1) % vals.length];
        }
    }

    //@Mod.Instance
    public static AutoHarvest instance;
    public HarvestMode mode = HarvestMode.FISHING;
    public TickListener listener = null;
    public KeyPressListener KeyListener = null;

    TaskManager taskManager = new TaskManager();

    private void setDisabled() {
        if (listener != null) {
            listener = null;
        }
    }
    public HarvestMode toSpecifiedMode(HarvestMode mode) {
        //setDisabled();
        if (listener == null) {
            listener = new TickListener(3, MinecraftClient.getInstance().player);
        } else
            listener.Reset();
        this.mode = mode;
        return mode;
    }
    public HarvestMode toNextMode() {
        //setDisabled();
        if (listener == null) {
            listener = new TickListener(3, MinecraftClient.getInstance().player);
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
        MinecraftClient.getInstance().player.sendMessage(new LiteralText(new TranslatableText("notify.prefix").getString() + new TranslatableText(key, obj).getString()), false);
    }
}
