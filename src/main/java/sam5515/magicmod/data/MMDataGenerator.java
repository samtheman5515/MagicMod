package sam5515.magicmod.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import sam5515.magicmod.MagicMod;
import sam5515.magicmod.data.server.PedestalRecipeProvider;

public class MMDataGenerator {
    public static void register(IEventBus bus){
        bus.addListener(MMDataGenerator::onGatherData);
    }
    private static void onGatherData(GatherDataEvent event){
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        boolean includeClient = event.includeClient();
        boolean includeServer = event.includeServer();
        gen.addProvider(includeServer, new PedestalRecipeProvider(gen, MagicMod.MODID));
    }
}
