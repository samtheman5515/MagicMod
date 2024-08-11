package sam5515.magicmod.common.registry;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sam5515.magicmod.MagicMod;
import sam5515.magicmod.common.block.PedestalBlock;

public class MMBlocks {
    private static final DeferredRegister<Block> blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, MagicMod.MODID);
    public static void register(IEventBus bus){
        blocks.register(bus);
    }
    public static final RegistryObject<PedestalBlock> pedestal = blocks.register("pedestal", PedestalBlock::new);
}
