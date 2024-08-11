package sam5515.magicmod.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import sam5515.magicmod.common.api.MagicUser;
import sam5515.magicmod.common.lib.MagicUserManager;

public record UpdateManaMessage(double mana, double maxMana, double rechargeRate) {
    public UpdateManaMessage(MagicUser storage){
        this(storage.getMana(), storage.getMaxMana(), storage.getRechargeRate());
    }
    public static final MessageHandler<UpdateManaMessage> HANDLER = new MessageHandler<>() {
        @Override
        public Class<UpdateManaMessage> getMessageClass() {
            return UpdateManaMessage.class;
        }

        @Override
        public void encode(UpdateManaMessage msg, FriendlyByteBuf buffer) {
            buffer.writeDouble(msg.mana);
            buffer.writeDouble(msg.maxMana);
            buffer.writeDouble(msg.rechargeRate);
        }

        @Override
        public UpdateManaMessage decode(FriendlyByteBuf buffer) {
            return new UpdateManaMessage(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }

        @Override
        public void handle(NetworkEvent.Context context, UpdateManaMessage msg) {
            MagicUser magicUser = MagicUserManager.getUser(Minecraft.getInstance().player);
            magicUser.setMana(msg.mana);
            magicUser.setMaxMana(msg.maxMana);
            magicUser.setRechargeRate(msg.rechargeRate);

        }
    };
}
