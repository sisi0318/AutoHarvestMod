package com.flier268.autoharvest;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
public class AutoHarvest implements ModInitializer {
    public static String MOD_NAME = "autoharvest";

    @Override
    public void onInitialize() {
        if (AutoHarvest.instance == null)
            AutoHarvest.instance = new AutoHarvest();
        if (AutoHarvest.instance.KeyListener == null) {
            AutoHarvest.instance.KeyListener = new KeyPressListener();
        }
    }

    public enum HarvestMode {
        HARVEST,  // Harvest only
        PLANT,  // Plant only
        Farmer,  //Harvest then re-plant
        SEED,   // Harvest seeds & flowers
        FEED,   // Feed animals
        FISHING,// Fishing
        OFF;    // Turn off mod
        private static HarvestMode[] vals = values();

        public AutoHarvest.HarvestMode next() {
            return vals[(this.ordinal() + 1) % vals.length];
        }
    }

    //@Mod.Instance
    public static AutoHarvest instance;
    public HarvestMode mode = HarvestMode.OFF;
    public TickListener listener = null;
    public KeyPressListener KeyListener = null;

    TaskManager taskManager = new TaskManager();

    private void setEnabled() {
        if (listener == null) {
            listener = new TickListener(mode, 3, MinecraftClient.getInstance().player);
        }
    }

    private void setDisabled() {
        if (listener != null) {
            listener = null;
        }
    }

    public HarvestMode toNextMode() {
        //setDisabled();
        mode = mode.next();
        if (mode != HarvestMode.OFF) {
            setEnabled();
        }
        return mode;
    }

    public void toNextMode(HarvestMode nextMode) {
        setDisabled();
        mode = nextMode;
        if (mode != HarvestMode.OFF) {
            setEnabled();
        }
    }

    public static void msg(String key, Object... obj) {
        if (MinecraftClient.getInstance() == null)
            return;
        if (MinecraftClient.getInstance().player == null)
            return;
        MinecraftClient.getInstance().player.addChatMessage(new LiteralText(I18n.translate("notify.prefix") + I18n.translate(key, obj)), false);
    }
}
