package tech.encrusted.breadcrumbs.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.text.Text;
import tech.encrusted.breadcrumbs.Txt;

import static tech.encrusted.breadcrumbs.Breadcrumbs.settings;

public class ModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create();
            builder.setParentScreen(parent).setTitle(Txt.translatable("config.breadcrumbs.title"));

            EpicConfigEntryBuilder entryBuilder = new EpicConfigEntryBuilder();
            //is it just me or is this absolutely terrible?
            builder.getOrCreateCategory(Txt.translatable("config.breadcrumbs.category.general"))
            .addEntry(entryBuilder.startBooleanToggle(Txt.translatable("config.breadcrumbs.render_through_walls"), settings.renderThroughWalls).setDefaultValue(true).setSaveConsumer(v -> settings.renderThroughWalls = v).build())
            .addEntry(entryBuilder.startFloatSlider(Txt.translatable("config.breadcrumbs.segment_length"), settings.segmentLength, 0f, 25f).setDefaultValue(1f).setSaveConsumer(v -> settings.segmentLength = v / 100f).build())
            .addEntry(entryBuilder.startBooleanToggle(Txt.translatable("config.breadcrumbs.smooth_interpolation"), settings.smoothInterpolation).setDefaultValue(true).setSaveConsumer(v -> settings.smoothInterpolation = v).build())
            .addEntry(entryBuilder.startIntSlider(Txt.translatable("config.breadcrumbs.interpolation_steps"), settings.interpolationSteps, 1, 100).setDefaultValue(7).setSaveConsumer(v -> settings.interpolationSteps = v).build())
            .addEntry(entryBuilder.startEnumSelector(Txt.translatable("config.breadcrumbs.trail_mode"), TrailMode.class, settings.trailMode).setDefaultValue(TrailMode.LINES).setSaveConsumer(v -> settings.trailMode = v).build())
            .addEntry(entryBuilder.startFloatSlider(Txt.translatable("config.breadcrumbs.trail_thickness"), settings.trailThickness, 0.1f, 10f).setDefaultValue(0.25f).setSaveConsumer(v -> settings.trailThickness = v / 100f).build())
            .addEntry(entryBuilder.startFloatSlider(Txt.translatable("config.breadcrumbs.trail_opacity"), settings.trailOpacity, 0f, 1f).setDefaultValue(1f).setSaveConsumer(v -> settings.trailOpacity = v / 100f).build())
            .addEntry(entryBuilder.startBooleanToggle(Txt.translatable("config.breadcrumbs.render_arrows"), settings.renderArrows).setDefaultValue(true).setSaveConsumer(v -> settings.renderArrows = v).build())
            .addEntry(entryBuilder.startBooleanToggle(Txt.translatable("config.breadcrumbs.backwards_arrows"), settings.backwardsArrows).setDefaultValue(false).setSaveConsumer(v -> settings.backwardsArrows = v).build())
            .addEntry(entryBuilder.startFloatSlider(Txt.translatable("config.breadcrumbs.arrow_size"), settings.arrowSize, 0.1f, 3f).setDefaultValue(0.2f).setSaveConsumer(v -> settings.arrowSize = v / 100f).build())
            .addEntry(entryBuilder.startIntSlider(Txt.translatable("config.breadcrumbs.arrow_frequency"), settings.arrowFrequency, 1, 50).setDefaultValue(5).setSaveConsumer(v -> settings.arrowFrequency = v).build())
            .addEntry(entryBuilder.startBooleanToggle(Txt.translatable("config.breadcrumbs.remove_loops"), settings.removeLoops).setDefaultValue(false).setSaveConsumer(v -> settings.removeLoops = v).build());

            builder.setSavingRunnable(() -> AutoConfig.getConfigHolder(Settings.class).save());
            return builder.build();
        };
    }
}
