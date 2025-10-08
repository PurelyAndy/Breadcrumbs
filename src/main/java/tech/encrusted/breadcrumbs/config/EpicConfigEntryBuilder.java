package tech.encrusted.breadcrumbs.config;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.impl.builders.*;
import me.shedaniel.clothconfig2.impl.ConfigEntryBuilderImpl;
import net.minecraft.text.Text;
import tech.encrusted.breadcrumbs.Txt;

import java.util.List;

// I wish whoever made this API would've allowed me to extend the class instead of having to wrap it
public class EpicConfigEntryBuilder implements ConfigEntryBuilder {
    private final Text resetButtonTranslationKey = Txt.translatable("text.cloth-config.reset_value");
    private ConfigEntryBuilderImpl builder;

    public EpicConfigEntryBuilder() {
        this.builder = ConfigEntryBuilderImpl.create();
    }

    public FloatSliderBuilder startFloatSlider(Text text, float value, float min, float max) {
        return new FloatSliderBuilder(resetButtonTranslationKey, text, value, min, max);
    }
    // <editor-fold desc="Delegated methods" defaultstate="collapsed">
    public Text getResetButtonKey() { return builder.getResetButtonKey(); }
    public ConfigEntryBuilder setResetButtonKey(Text text) { return builder.setResetButtonKey(text); }
    public IntListBuilder startIntList(Text text, List<Integer> values) { return builder.startIntList(text, values); }
    public LongListBuilder startLongList(Text text, List<Long> values) { return builder.startLongList(text, values); }
    public FloatListBuilder startFloatList(Text text, List<Float> values) { return builder.startFloatList(text, values); }
    public DoubleListBuilder startDoubleList(Text text, List<Double> values) { return builder.startDoubleList(text, values); }
    public StringListBuilder startStrList(Text text, List<String> values) { return builder.startStrList(text, values); }
    public SubCategoryBuilder startSubCategory(Text text) { return builder.startSubCategory(text); }
    public SubCategoryBuilder startSubCategory(Text text, List<AbstractConfigListEntry> entries) { return builder.startSubCategory(text, entries); }
    public BooleanToggleBuilder startBooleanToggle(Text text, boolean value) { return builder.startBooleanToggle(text, value); }
    public StringFieldBuilder startStrField(Text text, String value) { return builder.startStrField(text, value); }
    public ColorFieldBuilder startColorField(Text text, int value) { return builder.startColorField(text, value); }
    public TextFieldBuilder startTextField(Text text, String value) { return builder.startTextField(text, value); }
    public TextDescriptionBuilder startTextDescription(Text text) { return builder.startTextDescription(text); }
    public <T extends Enum<?>> EnumSelectorBuilder<T> startEnumSelector(Text text, Class<T> enumClass, T value) { return builder.startEnumSelector(text, enumClass, value); }
    public <T> SelectorBuilder<T> startSelector(Text text, T[] items, T value) { return builder.startSelector(text, items, value); }
    public IntFieldBuilder startIntField(Text text, int value) { return builder.startIntField(text, value); }
    public LongFieldBuilder startLongField(Text text, long value) { return builder.startLongField(text, value); }
    public FloatFieldBuilder startFloatField(Text text, float value) { return builder.startFloatField(text, value); }
    public DoubleFieldBuilder startDoubleField(Text text, double value) { return builder.startDoubleField(text, value); }
    public IntSliderBuilder startIntSlider(Text text, int value, int min, int max) { return builder.startIntSlider(text, value, min, max); }
    public LongSliderBuilder startLongSlider(Text text, long value, long min, long max) { return builder.startLongSlider(text, value, min, max); }
    public KeyCodeBuilder startModifierKeyCodeField(Text text, ModifierKeyCode modifierKeyCode) { return builder.startModifierKeyCodeField(text, modifierKeyCode); }
    public <T> DropdownMenuBuilder<T> startDropdownMenu(Text text, DropdownBoxEntry.SelectionTopCellElement<T> selectionTopCellElement, DropdownBoxEntry.SelectionCellCreator<T> selectionCellCreator) { return builder.startDropdownMenu(text, selectionTopCellElement, selectionCellCreator); }
    // </editor-fold>
}