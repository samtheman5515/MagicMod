package sam5515.magicmod.common.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sam5515.magicmod.MagicMod;
import sam5515.magicmod.common.item.ScrollItem;
import sam5515.magicmod.common.item.WandItem;

public class MMItems {
    private static final DeferredRegister<Item> items = DeferredRegister.create(ForgeRegistries.ITEMS, MagicMod.MODID);
    public static void register(IEventBus bus){
        items.register(bus);
    }
    private static RegistryObject<BlockItem> registerBlock(RegistryObject<? extends Block> block){
        return items.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
    }
    public static final RegistryObject<WandItem> wand = items.register("wand", WandItem::new);
    public static final RegistryObject<ScrollItem> scroll = items.register("scroll", ScrollItem::new);
    public static final RegistryObject<BlockItem> pedestal = registerBlock(MMBlocks.pedestal);
}
