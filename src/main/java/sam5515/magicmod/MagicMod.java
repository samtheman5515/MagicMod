package sam5515.magicmod;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sam5515.magicmod.client.ClientEventListeners;
import sam5515.magicmod.client.MMKeyMappings;
import sam5515.magicmod.client.renderer.PedestalRenderer;
import sam5515.magicmod.common.api.recipe.PedestalRecipeManager;
import sam5515.magicmod.common.event.WorldEventListeners;
import sam5515.magicmod.common.lib.MagicUserManager;
import sam5515.magicmod.common.network.MMNetwork;
import sam5515.magicmod.common.registry.*;
import sam5515.magicmod.data.MMDataGenerator;
import sam5515.magicmod.server.MMArgumentTypes;
import sam5515.magicmod.server.ManaCommand;
import sam5515.magicmod.server.SpellCommand;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MagicMod.MODID)
public class MagicMod {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "magicmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MagicMod.class);

    public MagicMod(){
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        MMItems.register(modBus);
        MMBlocks.register(modBus);
        MMBlockEntities.register(modBus);
        MMCreativeModeTabs.register(modBus);
        MMSpells.register(modBus);
        MMNetwork.register();
        MMArgumentTypes.register(modBus);
        MMDataGenerator.register(modBus);
        modBus.addListener(this::onRegisterKeymappings);
        modBus.addListener(this::onRegisterRenderers);
        forgeBus.register(ClientEventListeners.class);
        forgeBus.register(WorldEventListeners.class);
        forgeBus.addListener(this::onRegisterCommand);
        forgeBus.addListener(this::onAddReloadListener);
        MagicUserManager.register(forgeBus);
    }
    private void onRegisterCommand(RegisterCommandsEvent event){
        var dispatcher = event.getDispatcher();
        ManaCommand.register(dispatcher);
        SpellCommand.register(dispatcher);
    }
    private void onRegisterKeymappings(RegisterKeyMappingsEvent event){
        event.register(MMKeyMappings.OPEN_SPELL_INVENTORY);
    }
    private void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(MMBlockEntities.pedestal.get(), PedestalRenderer::new);
    }
    private void onAddReloadListener(AddReloadListenerEvent event){
        event.addListener(PedestalRecipeManager.instance);
    }

}