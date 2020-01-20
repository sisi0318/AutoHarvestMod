package com.flier268.autoharvest.Plugin;

import com.flier268.autoharvest.AutoHarvest;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

public class ModMenu implements ModMenuApi {
    @Override
    public String getModId() {
        return AutoHarvest.MOD_NAME;
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return ClothConfig::openConfigScreen;
    }
}
