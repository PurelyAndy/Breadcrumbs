package andy.breadcrumbs.config;

import me.shedaniel.clothconfig2.impl.builders.IntSliderBuilder;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FloatSliderBuilder extends IntSliderBuilder {
    private Supplier<Float> floatDefaultValue = null;
    public FloatSliderBuilder(Text resetButtonKey, Text fieldNameKey, float value, float min, float max) {
        super(resetButtonKey, fieldNameKey, (int) (value * 100), (int) (min * 100), (int) (max * 100));
    }

    public FloatSliderBuilder setDefaultValue(float defaultValue) {
        this.defaultValue = () -> (int) (defaultValue * 100);
        this.floatDefaultValue = () -> defaultValue;
        return this;
    }

    private Consumer<Float> getFloatSaveConsumer() {
        return flt -> this.getSaveConsumer().accept((int) (flt * 100));
    }

    private Function<Float,Optional<Text[]>> getFloatTooltipSupplier() {
        return flt -> this.getTooltipSupplier().apply((int) (flt * 100));
    }

    private Function<Float,Optional<Text>> getFloatErrorSupplier() {
        return flt -> this.errorSupplier.apply((int) (flt * 100));
    }

    public @NotNull FloatSliderEntry build() {
        FloatSliderEntry entry = new FloatSliderEntry(getFieldNameKey(), min, max, value, getResetButtonKey(), floatDefaultValue, getFloatSaveConsumer(), null, isRequireRestart());

        entry.setTooltipSupplier(() -> this.getFloatTooltipSupplier().apply(entry.getFloatValue()));
        if (this.textGetter != null)
            entry.setTextGetter(this.textGetter);
        if (this.errorSupplier != null)
            entry.setErrorSupplier(() -> this.getFloatErrorSupplier().apply(entry.getFloatValue()));

        return (FloatSliderEntry) this.finishBuilding(entry);
    }
}
