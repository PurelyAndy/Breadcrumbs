package tech.encrusted.breadcrumbs.config;

import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import net.minecraft.text.Text;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FloatSliderEntry extends IntegerSliderEntry {
    public FloatSliderEntry(Text fieldName, float minimum, float maximum, float value, Text resetButtonKey, Supplier<Float> defaultValue, Consumer<Float> saveConsumer, Supplier<Optional<Text[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, (int)(minimum), (int)(maximum), (int)(value), resetButtonKey, () -> defaultValue != null ? (int)(defaultValue.get() * 100) : null, (integer -> {
            saveConsumer.accept((float)integer / 100f);
        }), tooltipSupplier, requiresRestart);

        setTextGetter(integer -> Text.literal(String.format("Value: %.2f", ((float)integer / 100f))));
    }

    public Float getFloatValue() {
        return (float)this.value.get() / 100.0F;
    }
}
