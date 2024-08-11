package sam5515.magicmod.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import sam5515.magicmod.common.api.MagicUser;
import sam5515.magicmod.common.lib.MagicUserManager;

public record SyncMagicUserMessage(CompoundTag nbt) {
    public SyncMagicUserMessage(MagicUser magicUser){
        this(magicUser.serializeNBT());
    }
    public static final MessageHandler<SyncMagicUserMessage> HANDLER = new MessageHandler<>() {
        @Override
        public Class<SyncMagicUserMessage> getMessageClass() {
            return SyncMagicUserMessage.class;
        }

        @Override
        public void encode(SyncMagicUserMessage msg, FriendlyByteBuf buffer) {
            buffer.writeNbt(msg.nbt);
        }

        @Override
        public SyncMagicUserMessage decode(FriendlyByteBuf buffer) {
            return new SyncMagicUserMessage(buffer.readNbt());
        }

        @Override
        public void handle(NetworkEvent.Context context, SyncMagicUserMessage msg) {
            MagicUser magicUser = new MagicUser();
            magicUser.deserializeNBT(msg.nbt);
            MagicUserManager.sync(Minecraft.getInstance().player.getUUID(), magicUser);
        }
    };
}
