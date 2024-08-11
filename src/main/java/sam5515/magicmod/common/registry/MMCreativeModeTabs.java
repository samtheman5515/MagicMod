package sam5515.magicmod.common.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import sam5515.magicmod.MagicMod;
import sam5515.magicmod.common.api.spell.Spell;
import sam5515.magicmod.common.api.spell.SpellInstance;
import sam5515.magicmod.common.item.ScrollItem;
import sam5515.magicmod.common.util.SpellUtil;

import java.util.Comparator;

public class MMCreativeModeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), MagicMod.MODID);
    public static void register(IEventBus bus){
        TABS.register(bus);
    }
    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.literal("Magic Mod"))
            .icon(()-> new ItemStack(MMItems.wand.get()))
            .displayItems((params, output) -> {
                output.accept(MMItems.pedestal.get());
                output.accept(MMItems.wand.get());
                SpellRegistry.getRegistry().getValues().stream()
                        .sorted(Comparator.comparing(spell -> SpellUtil.translateSpell(spell).toString()))
                        .forEach(spell -> {
                            for (int level = 0; level < spell.getMaxLevel(); level++){
                                ItemStack stack = new ItemStack(MMItems.scroll.get());
                                ScrollItem.writeSpell(stack, new SpellInstance(spell, level));
                                output.accept(stack);
                            }
                        });
            })
            .build()
    );
}
