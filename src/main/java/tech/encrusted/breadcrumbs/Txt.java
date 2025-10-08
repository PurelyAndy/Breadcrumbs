package tech.encrusted.breadcrumbs;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
//? if <=1.18.2
/*import net.minecraft.text.TranslatableText;*/

public class Txt {
    public static MutableText translatable(String key) {
        //? if <=1.18.2 {
        /*return new TranslatableText(key);
        *///?} else {
         return Text.translatable(key); 
        //?}
    }
}
