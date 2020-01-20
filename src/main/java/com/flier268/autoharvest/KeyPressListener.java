package com.flier268.autoharvest;

import com.flier268.autoharvest.Plugin.ClothConfig;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeyPressListener {

    private FabricKeyBinding key_Switch, key_ModeChange, key_Config;

    public KeyPressListener() {
        KeyBindingRegistry.INSTANCE.addCategory(AutoHarvest.MOD_NAME);
        key_ModeChange = FabricKeyBinding.Builder.create(
                new Identifier(AutoHarvest.MOD_NAME, "modechange"),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                AutoHarvest.MOD_NAME
        ).build();
        key_Switch = FabricKeyBinding.Builder.create(
                new Identifier(AutoHarvest.MOD_NAME, "switch"),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                AutoHarvest.MOD_NAME
        ).build();
        key_Config = FabricKeyBinding.Builder.create(
                new Identifier(AutoHarvest.MOD_NAME, "config"),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                AutoHarvest.MOD_NAME
        ).build();
        KeyBindingRegistry.INSTANCE.register(key_ModeChange);
        KeyBindingRegistry.INSTANCE.register(key_Switch);
        KeyBindingRegistry.INSTANCE.register(key_Config);
        ClientTickCallback.EVENT.register(e ->
        {
            if (key_ModeChange.wasPressed())
                onProcessKey(key_ModeChange);
            else if (key_Switch.wasPressed())
                onProcessKey(key_Switch);
            else if (key_Config.wasPressed())
                onProcessKey(key_Config);
        });
    }

    public void onProcessKey(FabricKeyBinding key) {
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
