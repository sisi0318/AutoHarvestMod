package com.flier268.autoharvest.Plugin;

import com.flier268.autoharvest.AutoHarvest;
import com.flier268.autoharvest.Configure;
import com.flier268.autoharvest.TickListener;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

public class ClothConfig {
    public static Screen openConfigScreen(Screen parentScreen) {
        ConfigBuilder builder = ConfigBuilder.create()

                .setTitle(new TranslatableText(AutoHarvest.MOD_NAME + " config screen"))
                .setSavingRunnable(ClothConfig::saveConfig);

        ConfigCategory scrolling = builder.getOrCreateCategory(new TranslatableText(AutoHarvest.MOD_NAME));
        ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

        Configure c = AutoHarvest.instance.configure.load();

        scrolling.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.flower_is_seed"), c.flowerISseed).setDefaultValue(false).setSaveConsumer(b -> c.flowerISseed = b).build());

        scrolling.addEntry(entryBuilder.startIntSlider(new TranslatableText("config.effect_radius"), c.effect_radius, Configure.effect_radiusMin, Configure.effect_radiusMax).setDefaultValue(Configure.effect_radiusMax).setSaveConsumer(b -> c.effect_radius = b).build());
        return builder.setParentScreen(parentScreen).build();
    }

    private static void saveConfig() {
        AutoHarvest.instance.configure.save();
    }
}
