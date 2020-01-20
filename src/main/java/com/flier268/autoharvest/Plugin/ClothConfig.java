package com.flier268.autoharvest.Plugin;

import com.flier268.autoharvest.AutoHarvest;
import com.flier268.autoharvest.Configure;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;

public class ClothConfig {
    public static Screen openConfigScreen(Screen parentScreen) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(AutoHarvest.MOD_NAME + " config screen")
                .setSavingRunnable(ClothConfig::saveConfig);

        ConfigCategory scrolling = builder.getOrCreateCategory(AutoHarvest.MOD_NAME);
        ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

        Configure.getConfig().load();
        Configure c = Configure.getConfig();

        scrolling.addEntry(entryBuilder.startBooleanToggle(I18n.translate("config.flower_is_seed"), c.getFlowerISseed()).setDefaultValue(false).setSaveConsumer(b -> c.setFlowerISseed(b)).build());

        return builder.setParentScreen(parentScreen).build();
    }

    private static void saveConfig() {
        Configure.getConfig().save();
    }
}
