package sam5515.magicmod.common.registry;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import sam5515.magicmod.common.api.spell.Spell;
import sam5515.magicmod.common.spell.BlinkSpell;
import sam5515.magicmod.common.spell.CraftSpell;
import sam5515.magicmod.common.spell.FireBallSpell;
import sam5515.magicmod.common.spell.HarmSpell;

public class MMSpells {
    public static final RegistryObject<Spell>
        FIREBALL = SpellRegistry.SPELLS.register("fireball", FireBallSpell::new),
        BLINK = SpellRegistry.SPELLS.register("blink", BlinkSpell::new),
        HARM = SpellRegistry.SPELLS.register("harm", HarmSpell::new),
        CRAFT = SpellRegistry.SPELLS.register("craft", CraftSpell::new);
    public static void register (IEventBus bus){
        SpellRegistry.SPELLS.register(bus);
    }
}
