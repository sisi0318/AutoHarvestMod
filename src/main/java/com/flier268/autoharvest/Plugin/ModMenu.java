package com.flier268.autoharvest.Plugin;

import com.flier268.autoharvest.AutoHarvest;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class ModMenu implements ModMenuApi {
    @Override
    public String getModId() {
        return AutoHarvest.MOD_NAME;
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ClothConfig::openConfigScreen;
    }
}
