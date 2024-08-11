package sam5515.magicmod.common.util;

import com.sun.jna.WString;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import sam5515.magicmod.common.api.spell.Spell;
import sam5515.magicmod.common.registry.SpellRegistry;

public class SpellUtil {
    public static String toRomanNumerals(int n){
        return switch (n){
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> Integer.toString(n);
        };
    }
    public static Spell getSpell(ResourceLocation key){
        return SpellRegistry.getRegistry().getValue(key);
    }
    public static ResourceLocation getKey(Spell spell){
        return SpellRegistry.getRegistry().getKey(spell);
    }
    public static MutableComponent translateSpell(ResourceLocation key){
        return Component.translatable("spell.%s.%s".formatted(key.getNamespace(), key.getPath()));
    }
    public static MutableComponent translateSpell(Spell spell){
        return translateSpell(getKey(spell));
    }
    public static MutableComponent translateSpellWithLevel(ResourceLocation key, int level){
        return translateSpell(key).append(" " + toRomanNumerals(level + 1));
    }
    public static MutableComponent translateSpellWithLevel(Spell spell, int level){
        return translateSpellWithLevel(getKey(spell), level);
    }
}
