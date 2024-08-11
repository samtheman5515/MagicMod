package sam5515.magicmod.common.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import sam5515.magicmod.MagicMod;
import sam5515.magicmod.common.api.spell.Spell;

import java.util.function.Supplier;

public class SpellRegistry {
    public static final ResourceLocation NAME = new ResourceLocation(MagicMod.MODID, "spells");
    static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(NAME, MagicMod.MODID);
    private static final Supplier<IForgeRegistry<Spell>> REGISTRY = SPELLS.makeRegistry(RegistryBuilder::new);
    public static IForgeRegistry<Spell> getRegistry(){
        return REGISTRY.get();
    }
}
