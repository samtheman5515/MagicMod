package sam5515.magicmod.common.item;

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

public class WandItem extends Item {

    public WandItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide){
            MagicUser magicUser = MagicUserManager.getUser(player);
            int selected = magicUser.getSelectedEquipSpell();
            SpellInstance selectedSpell = magicUser.getEquippedSpell(selected);
            if(selectedSpell != null){
                Spell spell = selectedSpell.spell();
                int spellLevel = selectedSpell.level();
                double cost = spell.getCost(spellLevel);
                if (magicUser.getMana() >= cost){
                    boolean result = spell.activate(spellLevel, player);
                    if (result){
                        magicUser.changeMana(-cost);
                    }
                }
            }


        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
