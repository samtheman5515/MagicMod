package sam5515.magicmod.common.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import sam5515.magicmod.MagicMod;

public class MMNetwork {
    private static final String protocolVersion = "1";
    private static final SimpleChannel instance = NetworkRegistry.newSimpleChannel(new ResourceLocation(MagicMod.MODID, "main"), ()-> protocolVersion, protocolVersion::equals, protocolVersion::equals);
    private static <M> void addHandler(int id, MessageHandler<M> handler){
        instance.registerMessage(id, handler.getMessageClass(), handler::encode, handler::decode, (message, contextSupplier) -> {
            var context = contextSupplier.get();
            context.enqueueWork(()->handler.handle(context, message));
            context.setPacketHandled(true);
        });
    }
    public static void register(){
        int id = 1;
        addHandler(id++, UpdateManaMessage.HANDLER);
        addHandler(id++, UpdateKnownSpellsMessage.HANDLER);
        addHandler(id++, UpdateSelectedSpellMessage.HANDLER);
        addHandler(id++, UpdateEquippedSpellsMessage.HANDLER);
        addHandler(id++, SyncMagicUserMessage.HANDLER);
    }
    public static void sendToClient(ServerPlayer player, Object message){
        instance.send(PacketDistributor.PLAYER.with(()->player), message);
    }
    public static void sendToServer(Object message){
        instance.sendToServer(message);
    }
}
