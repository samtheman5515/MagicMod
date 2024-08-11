package sam5515.magicmod.common.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sam5515.magicmod.common.api.MagicUser;
import sam5515.magicmod.common.api.spell.Spell;
import sam5515.magicmod.common.api.spell.SpellInstance;
import sam5515.magicmod.common.lib.MagicUserManager;
import sam5515.magicmod.common.registry.SpellRegistry;
import sam5515.magicmod.common.util.SpellUtil;

import java.security.Key;

public class ScrollItem extends Item {

    public ScrollItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide){
            SpellInstance spell = readSpell(player.getItemInHand(hand));
            if (spell != null){
                MagicUser magicUser = MagicUserManager.getUser(player);
                magicUser.learnSpell(spell.spell(), spell.level());
                player.displayClientMessage(Component.translatable("message.magicmod.scroll.learned", SpellUtil.translateSpellWithLevel(spell.spell(), spell.level())), true);
                player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1, 1);
                player.setItemInHand(hand, ItemStack.EMPTY);
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public Component getName(ItemStack stack) {
        SpellInstance spell = readSpell(stack);
        if (spell != null){
            return Component.translatable("item.magicmod.scroll.with_spell", SpellUtil.translateSpellWithLevel(spell.spell(), spell.level()));
        }
        return super.getName(stack);
    }

    public static SpellInstance readSpell(ItemStack stack){
        CompoundTag tag = stack.getTag();
        if (tag != null){
            ResourceLocation key = new ResourceLocation(tag.getString("SpellKey"));
            int level = tag.getByte("SpellLevel");
            Spell spell = SpellUtil.getSpell(key);

            return new SpellInstance(spell, level);
        }
        return null;
    }
    public static void writeSpell(ItemStack stack, SpellInstance spell){
        CompoundTag tag = stack.getOrCreateTag();
        ResourceLocation key = SpellUtil.getKey(spell.spell());
        tag.putString("SpellKey", key.toString());
        tag.putByte("SpellLevel", (byte) spell.level());
    }
}
