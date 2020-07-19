package com.flier268.autoharvest;

import com.flier268.autoharvest.Plugin.ClothConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyPressListener {

    private KeyBinding key_Switch, key_ModeChange, key_Config;

    public KeyPressListener() {

        key_ModeChange = new KeyBinding("key.autoharvest.modechange",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                AutoHarvest.MOD_NAME
        );
        key_Switch = new KeyBinding("key.autoharvest.switch",
                GLFW.GLFW_KEY_J,
                AutoHarvest.MOD_NAME
        );
        key_Config = new KeyBinding("key.autoharvest.config",
                GLFW.GLFW_KEY_K,
                AutoHarvest.MOD_NAME
        );
        KeyBindingHelper.registerKeyBinding(key_ModeChange);
        KeyBindingHelper.registerKeyBinding(key_Switch);
        KeyBindingHelper.registerKeyBinding(key_Config);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (key_ModeChange.wasPressed())
                onProcessKey(key_ModeChange);
            else if (key_Switch.wasPressed())
                onProcessKey(key_Switch);
            else if (key_Config.wasPressed())
                onProcessKey(key_Config);
        });
    }

    public void onProcessKey(KeyBinding key) {
        if (key.equals(key_ModeChange)) {
            String modeName = AutoHarvest.instance.toNextMode().toString().toLowerCase();
            AutoHarvest.msg("notify.switch_to." + modeName);
        } else if (key.equals(key_Switch)) {
            AutoHarvest.instance.Switch = !AutoHarvest.instance.Switch;
            AutoHarvest.msg("notify.turn." + (AutoHarvest.instance.Switch ? "on" : "off"));
        } else if (key.equals(key_Config)) {
            MinecraftClient.getInstance().openScreen(ClothConfig.openConfigScreen(MinecraftClient.getInstance().currentScreen));
        }
    }
}
