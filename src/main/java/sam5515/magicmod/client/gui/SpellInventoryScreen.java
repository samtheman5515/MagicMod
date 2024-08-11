package sam5515.magicmod.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import sam5515.magicmod.MagicMod;
import sam5515.magicmod.common.api.MagicUser;
import sam5515.magicmod.common.api.spell.Spell;
import sam5515.magicmod.common.api.spell.SpellInstance;
import sam5515.magicmod.common.lib.MagicUserManager;
import sam5515.magicmod.common.network.MMNetwork;
import sam5515.magicmod.common.network.UpdateEquippedSpellsMessage;
import sam5515.magicmod.common.registry.SpellRegistry;
import sam5515.magicmod.common.util.SpellUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SpellInventoryScreen extends Screen {
    private static final ResourceLocation TEXTURE = new ResourceLocation(MagicMod.MODID, "textures/gui/spell_inventory.png");
    public static final int HOTBARSLOTS = 9, SEARCHSLOTS = 54, TOTALSLOTS = HOTBARSLOTS + SEARCHSLOTS;
    private static int getSlotX(int slot){
        return 8 + (slot % 9) * 18;
    }
    private static int getSlotY(int slot){
        if(slot < HOTBARSLOTS){
            return 142;
        }
        return 12 + (slot/9) * 18;
    }
    private int leftPos, topPos;
    private final SpellSlot[] icons;
    private int scroll;
    private SpellInstance heldSpell;
    public SpellInventoryScreen() {
        super(Component.literal("Spells"));
        leftPos = 0;
        topPos = 0;
        icons = new SpellSlot[TOTALSLOTS];
        scroll = 0;
        heldSpell = null;
    }

    @Override
    protected void init() {
        MagicUser magicUser = MagicUserManager.getUser(minecraft.player);
        leftPos = (width - 194)/2;
        topPos = (height - 166)/2;
        for (int i = 0; i < icons.length; i++) {
            SpellInstance spellInst = null;
            if (i < HOTBARSLOTS && magicUser != null){
                spellInst = magicUser.getEquippedSpell(i);
            }
            icons[i] = new SpellSlot(leftPos + getSlotX(i), topPos + getSlotY(i), spellInst, i >= HOTBARSLOTS);
            addRenderableWidget(icons[i]);
        }
        updateReadOnlyIcons();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        g.blit(TEXTURE, leftPos, topPos, 0, 0, 194, 166, 256, 256);
        super.render(g, mouseX, mouseY, partialTick);
        for (SpellSlot icon : icons) {
            if (icon.isHovered()){
                int x = icon.getX();
                int y = icon.getY();
                g.fillGradient(RenderType.guiOverlay(), x, y, x + 16, y + 16, 0x80ffffff, 0x80ffffff, 0);
            }
        }
        if (heldSpell != null){
            g.pose().pushPose();
            g.pose().translate(0, 0, 1);
            renderSpellIcon(g, heldSpell, mouseX - 8, mouseY - 8);
            g.pose().popPose();
        }
    }

    @Override
    public void onClose() {
        MagicUser magicUser = MagicUserManager.getUser(minecraft.player);
        for (int i = 0; i < HOTBARSLOTS; i++) {
            SpellInstance spellInst = icons[i].spellInst;
            if (spellInst == null){
                magicUser.unequipSpell(i);
            } else {
                magicUser.equipSpell(i, spellInst.spell(), spellInst.level());
            }
        }
        MMNetwork.sendToServer(new UpdateEquippedSpellsMessage(magicUser.allEquippedSpells(), false));

        super.onClose();
    }

    public static void renderSpellIcon(GuiGraphics g, SpellInstance spell, int x, int y){
        ResourceLocation key = SpellRegistry.getRegistry().getKey(spell.spell());
        ResourceLocation iconTexture = new ResourceLocation(key.getNamespace(), "textures/gui/spellicons/" + key.getPath() + ".png");
        FormattedCharSequence seq = FormattedCharSequence.forward(String.valueOf(spell.level() + 1), Style.EMPTY);
        int width = Minecraft.getInstance().font.width(seq);
        int height = Minecraft.getInstance().font.lineHeight;
        g.blit(iconTexture, x, y, 0, 0, 0, 16, 16, 16, 16);
        g.drawString(Minecraft.getInstance().font, seq, x + 17 - width, y + 18 - height, 0xffffff);
    }

    private void updateReadOnlyIcons(){
        List<SpellInstance> spells = SpellRegistry.getRegistry().getValues().stream()
                .mapMulti((BiConsumer<Spell, Consumer<SpellInstance>>) (spell, consumer)->{
                    for (int level = 0; level < spell.getMaxLevel(); level ++){
                        consumer.accept(new SpellInstance(spell, level));
                    }
                })
                .skip(scroll * 9)
                .limit(SEARCHSLOTS)
                .toList();
        for (int i = 0; i < spells.size(); i++){
            icons[i+ HOTBARSLOTS].spellInst = spells.get(i);
            icons[i + HOTBARSLOTS].update();
        }
    }
    public class SpellSlot extends AbstractWidget{
        private final boolean isReadOnly;
        private SpellInstance spellInst;
        private ResourceLocation iconTexture;
        private Component levelComp;
        public SpellSlot(int x, int y, SpellInstance spellInst, boolean isReadOnly){
            super(x, y, 16, 16, Component.empty());
            this.isReadOnly = isReadOnly;
            this.spellInst = spellInst;
            iconTexture = null;
            levelComp = null;
            update();
        }
        public void update(){
            if(spellInst == null){
                setMessage(Component.empty());
                iconTexture = null;
                levelComp = null;
            } else {
                ResourceLocation name = SpellRegistry.getRegistry().getKey(spellInst.spell());
                setMessage(SpellUtil.translateSpellWithLevel(name, spellInst.level()));
                if(name == null){
                    iconTexture = null;
                } else {
                    iconTexture = new ResourceLocation(name.getNamespace(), "textures/gui/spellicons/" + name.getPath() + ".png");
                }
                levelComp = Component.literal(String.valueOf(spellInst.level() + 1));
            }
        }

        @Override
        protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
            if (iconTexture != null){
                g.blit(iconTexture, getX(), getY(), 0, 0, 0, getWidth(), getHeight(), 16, 16);
                FormattedCharSequence sequence = FormattedCharSequence.forward(levelComp.getString(), Style.EMPTY);
                int width = Minecraft.getInstance().font.width(sequence);
                g.drawString(Minecraft.getInstance().font, sequence, getX() + 17 - width, getY() + 18 - minecraft.font.lineHeight, 0xffffff);
            }
            if (isHovered){
                if (spellInst != null){
                    List<FormattedCharSequence> tooltip = new ArrayList<>();
                    tooltip.add(getMessage().getVisualOrderText());
                    tooltip.add(Component.literal("Cost: " + spellInst.spell().getCost(spellInst.level())).getVisualOrderText());
                    setTooltipForNextRenderPass(tooltip);
                } else {
                    setTooltipForNextRenderPass(Component.empty());

                }
            }

        }

        @Override
        public void onClick(double pMouseX, double pMouseY) {
            if (isReadOnly){
                if (heldSpell == null){
                    heldSpell = spellInst;
                } else {
                    heldSpell = null;
                }
            } else {
                SpellInstance temp = heldSpell;
                heldSpell = spellInst;
                spellInst = temp;
                update();
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }
    }
}
