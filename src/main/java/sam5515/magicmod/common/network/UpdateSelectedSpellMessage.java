package sam5515.magicmod.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import sam5515.magicmod.common.api.MagicUser;
import sam5515.magicmod.common.lib.MagicUserManager;

public record UpdateSelectedSpellMessage(int selectedSpell) {
    public static final MessageHandler<UpdateSelectedSpellMessage> HANDLER = new MessageHandler<>() {
        @Override
        public Class<UpdateSelectedSpellMessage> getMessageClass() {
            return UpdateSelectedSpellMessage.class;
        }

        @Override
        public void encode(UpdateSelectedSpellMessage msg, FriendlyByteBuf buffer) {
            buffer.writeByte(msg.selectedSpell);
        }

        @Override
        public UpdateSelectedSpellMessage decode(FriendlyByteBuf buffer) {
            return new UpdateSelectedSpellMessage(buffer.readByte());
        }

        @Override
        public void handle(NetworkEvent.Context context, UpdateSelectedSpellMessage msg) {
            MagicUser magicUser = MagicUserManager.getUser(context.getSender());
            magicUser.setSelectedEquipSpell(msg.selectedSpell);

        }
    };
}
