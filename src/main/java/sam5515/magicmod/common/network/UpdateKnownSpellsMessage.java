package sam5515.magicmod.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import sam5515.magicmod.common.api.MagicUser;
import sam5515.magicmod.common.api.spell.Spell;
import sam5515.magicmod.common.api.spell.SpellInstance;
import sam5515.magicmod.common.registry.SpellRegistry;

import java.util.ArrayList;
import java.util.List;

public record UpdateKnownSpellsMessage(List<SpellInstance> knownSpells) {
    public UpdateKnownSpellsMessage(MagicUser magicUser){
        this(magicUser.allKnownSpells().map(entry -> new SpellInstance(entry.getKey(), entry.getValue())).toList());
    }
    public static final MessageHandler<UpdateKnownSpellsMessage> HANDLER = new MessageHandler<UpdateKnownSpellsMessage>() {
        @Override
        public Class<UpdateKnownSpellsMessage> getMessageClass() {
            return UpdateKnownSpellsMessage.class;
        }

        @Override
        public void encode(UpdateKnownSpellsMessage msg, FriendlyByteBuf buffer) {
            buffer.writeShort(msg.knownSpells.size());
            msg.knownSpells.forEach(spellInstance -> {
                buffer.writeResourceLocation(SpellRegistry.getRegistry().getKey(spellInstance.spell()));
                buffer.writeByte(spellInstance.level());
            });
        }

        @Override
        public UpdateKnownSpellsMessage decode(FriendlyByteBuf buffer) {
            List<SpellInstance> knownSpells = new ArrayList<>();
            int count = buffer.readShort();
            for (int i = 0; i < count; i++) {
                ResourceLocation name = buffer.readResourceLocation();
                Spell spell = SpellRegistry.getRegistry().getValue(name);
                int level = buffer.readByte();
                knownSpells.add(new SpellInstance(spell, level));
            }
            return new UpdateKnownSpellsMessage(knownSpells);
        }

        @Override
        public void handle(NetworkEvent.Context context, UpdateKnownSpellsMessage msg) {
            //Minecraft.getInstance().player.getCapability(MMCapabilities)
        }
    };
}
