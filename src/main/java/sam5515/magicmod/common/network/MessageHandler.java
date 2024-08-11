package sam5515.magicmod.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public interface MessageHandler<M> {
    Class<M> getMessageClass();
    void encode(M msg, FriendlyByteBuf buffer);
    M decode(FriendlyByteBuf buffer);
    void handle(NetworkEvent.Context context, M msg);
}
