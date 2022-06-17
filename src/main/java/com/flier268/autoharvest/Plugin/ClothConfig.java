package com.flier268.autoharvest.Plugin;

import com.flier268.autoharvest.AutoHarvest;
import com.flier268.autoharvest.Configuration;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

public class ClothConfig {
    public static Screen openConfigScreen(Screen parentScreen) {
        ConfigBuilder builder = ConfigBuilder.create()

                .setTitle(Text.of(new TranslatableTextContent(AutoHarvest.MOD_NAME + " config screen").toString()))
                .setSavingRunnable(ClothConfig::saveConfig);

        ConfigCategory scrolling = builder.getOrCreateCategory(Text.of(new TranslatableTextContent(AutoHarvest.MOD_NAME).toString()));
        ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

        Configuration c = AutoHarvest.instance.configure.load();

        scrolling.addEntry(entryBuilder.startBooleanToggle(Text.of(new TranslatableTextContent("config.flower_is_seed").toString()), c.flowerISseed.value).setDefaultValue(false).setSaveConsumer(b -> c.flowerISseed.value = b).build());

        scrolling.addEntry(entryBuilder.startIntSlider(Text.of(new TranslatableTextContent("config.effect_radius").toString()), c.effect_radius.value, Configuration.Effect_radius.Min, Configuration.Effect_radius.Max).setDefaultValue((new Configuration.Effect_radius()).value).setSaveConsumer(b -> c.effect_radius.value = b).build());
        scrolling.addEntry(entryBuilder.startIntSlider(Text.of(new TranslatableTextContent("config.tick_skip").toString()), c.tickSkip.value, Configuration.TickSkip.Min, Configuration.TickSkip.Max).setTooltip(Text.of(new TranslatableTextContent("config.tick_skip_tooltip").toString())).setDefaultValue(new Configuration.TickSkip().value).setSaveConsumer(b -> c.tickSkip.value = b).build());
        scrolling.addEntry(entryBuilder.startBooleanToggle(Text.of(new TranslatableTextContent("config.keep_fishing_rod_alive").toString()), c.keepFishingRodAlive.value).setDefaultValue(new Configuration.KeepFishingRodAlive().value).setSaveConsumer(b -> c.keepFishingRodAlive.value = b).build());
        return builder.setParentScreen(parentScreen).build();
    }

    private static void saveConfig() {
        AutoHarvest.instance.configure.save();
    }
}
