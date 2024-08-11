package sam5515.magicmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sam5515.magicmod.MagicMod;
import sam5515.magicmod.client.gui.SpellInventoryScreen;
import sam5515.magicmod.common.api.MagicUser;
import sam5515.magicmod.common.api.spell.SpellInstance;
import sam5515.magicmod.common.lib.MagicUserManager;
import sam5515.magicmod.common.network.MMNetwork;
import sam5515.magicmod.common.network.UpdateSelectedSpellMessage;
import sam5515.magicmod.common.registry.MMItems;
import sam5515.magicmod.common.registry.SpellRegistry;

public class ClientEventListeners {
    private static final ResourceLocation manaBarGui = new ResourceLocation(MagicMod.MODID, "textures/gui/manabar.png");
    private static final ResourceLocation spellWidgets = new ResourceLocation(MagicMod.MODID, "textures/gui/spell_widgets.png");
    private static ResourceLocation getSpellIcon(ResourceLocation spellName){
        return new ResourceLocation(spellName.getNamespace(), "textures/gui/spellicons/" + spellName.getPath() + ".png");
    }
    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Pre event){
        if (event.getOverlay() == VanillaGuiOverlay.HOTBAR.type()){
            Player player = Minecraft.getInstance().player;
            MagicUser magicUser = MagicUserManager.getUser(player);
            GuiGraphics g = event.getGuiGraphics();
            int startX = 15, startY = event.getWindow().getGuiScaledHeight() - 26;
            g.blit(manaBarGui, startX, startY, 50, 0, 0, 64, 16, 64, 32);
            g.blit(manaBarGui, startX, startY, 50, 0, 16, (int)((magicUser.getMana()/magicUser.getMaxMana()) * 64), 16, 64, 32);
            g.drawCenteredString(Minecraft.getInstance().font, Integer.toString((int)magicUser.getMana()), startX + 32, startY + 4, 0xffffff);

            if (player.isShiftKeyDown()){
                if (player.getMainHandItem().is(MMItems.wand.get()) || player.getOffhandItem().is(MMItems.wand.get())){
                    event.setCanceled(true);
                    startX = g.guiWidth()/2 - 91;
                    startY = g.guiHeight() - 22;
                    g.blit(spellWidgets, startX, startY, 0, 0, 182, 22);
                    g.blit(spellWidgets, startX - 1 + magicUser.getSelectedEquipSpell() * 20, startY - 1, 0, 22, 24, 24);
                    SpellInstance[] equippedSpells = magicUser.allEquippedSpells();
                    for (int i = 0; i < equippedSpells.length; i++) {
                        if (equippedSpells[i] != null) {
                            ResourceLocation name = SpellRegistry.getRegistry().getKey(equippedSpells[i].spell());
                            g.blit(getSpellIcon(name), startX + 3 + i * 20, startY + 3, 50, 0, 0, 16, 16, 16, 16);
                        }
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event){
        Player player = Minecraft.getInstance().player;
        if (player.isShiftKeyDown() && (player.getMainHandItem().is(MMItems.wand.get())|| player.getOffhandItem().is(MMItems.wand.get()))){
            MagicUser magicUser = MagicUserManager.getUser(player);
            event.setCanceled(true);
            int slot = magicUser.getSelectedEquipSpell();
            if (event.getScrollDelta() > 0){
                slot--;
            } else {
                slot++;
            }
            if (slot < 0){
                slot = MagicUser.EQUIP_SLOTS - 1;
            } else if (slot >= MagicUser.EQUIP_SLOTS){
                slot = 0;
            }
            magicUser.setSelectedEquipSpell(slot);
            MMNetwork.sendToServer(new UpdateSelectedSpellMessage(slot));
        }
    }
    @SubscribeEvent
    public static void onKey(InputEvent.Key event){
        var key = InputConstants.getKey(event.getKey(), event.getScanCode());
        if (MMKeyMappings.OPEN_SPELL_INVENTORY.isActiveAndMatches(key)){
            if (Minecraft.getInstance().screen == null){
                Minecraft.getInstance().setScreen(new SpellInventoryScreen());
            }
        }
    }

}
