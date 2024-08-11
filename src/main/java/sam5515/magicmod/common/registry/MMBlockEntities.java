package sam5515.magicmod.common.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sam5515.magicmod.MagicMod;
import sam5515.magicmod.common.block.entity.PedestalBlockEntity;

import java.util.Arrays;
import java.util.function.Supplier;

public class MMBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> blockEntities = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MagicMod.MODID);
    public static void register (IEventBus bus){
        blockEntities.register(bus);
    }
    private static <T extends BlockEntity>RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> supplier, Supplier<Block>... validBlocks){
        return blockEntities.register(name, ()-> BlockEntityType.Builder.of(supplier, Arrays.stream(validBlocks).map(Supplier::get).toArray(Block[]::new)).build(null));
    }
    public static final RegistryObject<BlockEntityType<PedestalBlockEntity>> pedestal = register("pedestal", PedestalBlockEntity::new, MMBlocks.pedestal::get);
}
