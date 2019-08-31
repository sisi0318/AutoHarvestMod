package com.flier268.autoharvest;

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeyPressListener {

    private FabricKeyBinding key_ModeChange;

    public KeyPressListener() {
        KeyBindingRegistry.INSTANCE.addCategory(AutoHarvest.MOD_NAME);
        key_ModeChange = FabricKeyBinding.Builder.create(
                new Identifier(AutoHarvest.MOD_NAME, "modechange"),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                AutoHarvest.MOD_NAME
        ).build();
        KeyBindingRegistry.INSTANCE.register(key_ModeChange);
        ClientTickCallback.EVENT.register(e ->
        {
            if (key_ModeChange.wasPressed())
                onProcessKey();
        });
    }

    public void onProcessKey() {
        String modeName = AutoHarvest.instance.toNextMode().toString().toLowerCase();
        AutoHarvest.msg("notify.switch_to." + modeName);
    }
}
