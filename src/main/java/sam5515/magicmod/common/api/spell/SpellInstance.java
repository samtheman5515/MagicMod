package sam5515.magicmod.common.api.spell;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import sam5515.magicmod.common.registry.SpellRegistry;

public record SpellInstance(Spell spell, int level) {
    public CompoundTag serializeNBT(){
        CompoundTag tag = new CompoundTag();
        tag.putString("spell", SpellRegistry.getRegistry().getKey(spell).toString());
        tag.putByte("level", (byte) level);
        return tag;
    }
    public static SpellInstance fromTag(CompoundTag tag){
        ResourceLocation name = new ResourceLocation(tag.getString("spell"));
        Spell spell =  SpellRegistry.getRegistry().getValue(name);
        if (spell == null){
            throw new IllegalArgumentException("Unknown Spell:" + name);
        }
        int level = tag.getByte("level");
        return new SpellInstance(spell, level);
    }
}
