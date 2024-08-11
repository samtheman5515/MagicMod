package sam5515.magicmod.server;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sam5515.magicmod.MagicMod;

public class MMArgumentTypes {
    private static final DeferredRegister<ArgumentTypeInfo<?,?>> ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, MagicMod.MODID);
    public static void register(IEventBus bus){
        ARGUMENT_TYPES.register(bus);
    }
    public static final RegistryObject<ArgumentTypeInfo<?,?>>
        SPELL = ARGUMENT_TYPES.register("spell", ()-> ArgumentTypeInfos.registerByClass(SpellArgumentType.class, SingletonArgumentInfo.contextFree(SpellArgumentType::new)));
}
