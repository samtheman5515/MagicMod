package sam5515.magicmod.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import sam5515.magicmod.common.api.MagicUser;
import sam5515.magicmod.common.api.spell.Spell;
import sam5515.magicmod.common.api.spell.SpellInstance;
import sam5515.magicmod.common.lib.MagicUserManager;
import sam5515.magicmod.common.registry.SpellRegistry;

public record UpdateEquippedSpellsMessage(SpellInstance[] equippedSpells, boolean updateClient) {
    public static final MessageHandler<UpdateEquippedSpellsMessage> HANDLER = new MessageHandler<>() {
        @Override
        public Class<UpdateEquippedSpellsMessage> getMessageClass() {
            return UpdateEquippedSpellsMessage.class;
        }

        @Override
        public void encode(UpdateEquippedSpellsMessage msg, FriendlyByteBuf buffer) {
            buffer.writeByte(msg.equippedSpells.length);
            for (SpellInstance equippedSpell : msg.equippedSpells) {
                if (equippedSpell == null){
                    buffer.writeByte(-1);
                } else {
                    buffer.writeByte(equippedSpell.level());
                    ResourceLocation name = SpellRegistry.getRegistry().getKey(equippedSpell.spell());
                    buffer.writeResourceLocation(name);
                }
            }
            buffer.writeBoolean(msg.updateClient);
        }

        @Override
        public UpdateEquippedSpellsMessage decode(FriendlyByteBuf buffer) {
            int count = buffer.readByte();
            SpellInstance[] equippedSpells = new SpellInstance[count];
            for (int i = 0; i < count; i++) {
                int level = buffer.readByte();
                if (level >= 0){
                    ResourceLocation name = buffer.readResourceLocation();
                    Spell spell = SpellRegistry.getRegistry().getValue(name);
                    equippedSpells[i] = new SpellInstance(spell, level);
                }
            }
            boolean updateClient = buffer.readBoolean();
            return new UpdateEquippedSpellsMessage(equippedSpells, updateClient);
        }

        @Override
        public void handle(NetworkEvent.Context context, UpdateEquippedSpellsMessage msg) {
            Player player;
            if (msg.updateClient()){
                player = Minecraft.getInstance().player;
            } else {
                player = context.getSender();
            }
            MagicUser magicUser = MagicUserManager.getUser(player);
            magicUser.updateEquippedSpells(msg.equippedSpells);
        }
    };
}
