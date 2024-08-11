package sam5515.magicmod.common.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.common.util.INBTSerializable;
import sam5515.magicmod.common.api.spell.Spell;
import sam5515.magicmod.common.api.spell.SpellInstance;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class MagicUser implements INBTSerializable<CompoundTag> {
    public static final int EQUIP_SLOTS = 9;
    private double mana;
    private double maxMana;
    private double rechargeRate;
    private int rechargeCooldown;
    private final Map<Spell, Integer> knownSpells;
    private final SpellInstance[] equippedSpells;
    private int selectedEquipSpell;
    private boolean modified;
    public MagicUser(){
        mana = 0;
        maxMana = 100;
        rechargeRate = 1;
        rechargeCooldown = 0;
        knownSpells = new HashMap<>();
        equippedSpells = new SpellInstance[EQUIP_SLOTS];
        selectedEquipSpell = 0;
        modified = false;
    }
    private void markModified(){
        modified = true;
    }
    public boolean hasBeenModified(){
        if(modified){
            modified = false;
            return true;
        }
        return false;
    }
    public double getMana() {
        return mana;
    }

    public double getMaxMana() {
        return maxMana;
    }

    public double getRechargeRate() {
        return rechargeRate;
    }
    public Stream<Map.Entry<Spell, Integer>> allKnownSpells(){
        return knownSpells.entrySet().stream();
    }
    public int getKnownLevel(Spell spell){
        return knownSpells.getOrDefault(spell, -1);
    }
    public SpellInstance[] allEquippedSpells(){
        SpellInstance[] copy = new SpellInstance[EQUIP_SLOTS];
        System.arraycopy(equippedSpells, 0, copy, 0, EQUIP_SLOTS);
        return copy;
    }
    public SpellInstance getEquippedSpell(int slot){
        if (slot >= 0 && slot < EQUIP_SLOTS) {
            return equippedSpells[slot];
        }
        return null;
    }
    public int getSelectedEquipSpell(){
        return selectedEquipSpell;
    }
    public void setMana(double mana) {
        double prevMana = this.mana;
        this.mana = Mth.clamp(mana, 0, maxMana);
        modified = modified || this.mana != prevMana;
    }

    public void changeMana(double amount) {
        setMana(mana + amount);
    }

    public void setMaxMana(double maxMana) {
        this.maxMana = Mth.clamp(maxMana, 0, Double.MAX_VALUE);
        modified = true;
    }

    public void setRechargeRate(double rechargeRate) {
        this.rechargeRate = Mth.clamp(rechargeRate, 0, Double.MAX_VALUE);
        modified = true;
    }

    public void attemptRecharge() {
        if (rechargeRate > 0 && mana < maxMana) {
            if (++rechargeCooldown >= 20) {
                rechargeCooldown = 0;
                changeMana(rechargeRate);
            }
        } else {
            if (rechargeCooldown != 0) {
                rechargeCooldown = 0;
            }
        }
    }
    public void learnSpell(Spell spell, int level){
        int currentKnownLevel = getKnownLevel(spell);
        if (level > currentKnownLevel && level <= spell.getMaxLevel()){
            knownSpells.put(spell, level);
            markModified();
        }
    }
    public void equipSpell(int slot, Spell spell, int level){
        if (slot >= 0 && slot < EQUIP_SLOTS /*&& getKnownLevel(spell) >= level*/){
            equippedSpells[slot] = new SpellInstance(spell, level);
            markModified();
        }
    }
    public void unequipSpell(int slot){
        if(slot >= 0 && slot < EQUIP_SLOTS){
            equippedSpells[slot] = null;
            markModified();
        }
    }
    public void setSelectedEquipSpell(int slot){
        if (slot >= 0 && slot< EQUIP_SLOTS){
            selectedEquipSpell = slot;
        }
    }
    public void updateKnownSpells(List<SpellInstance> newKnownSpells){
        knownSpells.clear();
        newKnownSpells.forEach(spellInst -> {
            knownSpells.put(spellInst.spell(), spellInst.level());
        });
    }
    public void updateEquippedSpells(SpellInstance[] newEquippedSpells){
        System.arraycopy(newEquippedSpells, 0, equippedSpells, 0, EQUIP_SLOTS);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("Mana", mana);
        tag.putDouble("MaxMana", maxMana);
        tag.putDouble("RechargeRate", rechargeRate);
        tag.putByte("RechargeCooldown", (byte) rechargeCooldown);
        ListTag knownSpellsTag = new ListTag();
        knownSpells.forEach((spell, level) -> knownSpellsTag.add(new SpellInstance(spell, level).serializeNBT()));
        tag.put("KnownSpells", knownSpellsTag);
        ListTag equippedSpellsTag = new ListTag();
        for (int i = 0; i < equippedSpells.length; i++) {
            SpellInstance spellInstance = equippedSpells[i];
            if(spellInstance != null){
                CompoundTag equippedSpellTag = new CompoundTag();
                equippedSpellTag.putByte("Slot", (byte) i);
                equippedSpellTag.put("SpellInstance", spellInstance.serializeNBT());
                equippedSpellsTag.add(equippedSpellTag);
            }
        }
        tag.put("EquippedSpells", equippedSpellsTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        knownSpells.clear();
        Arrays.fill(equippedSpells, null);
        mana = tag.getDouble("Mana");
        maxMana = tag.getDouble("MaxMana");
        rechargeRate = tag.getDouble("RechargeRate");
        rechargeCooldown = tag.getByte("RechargeCooldown");
        tag.getList("KnownSpells", Tag.TAG_COMPOUND).forEach(knownSpellTag -> {
            SpellInstance spellInst = SpellInstance.fromTag((CompoundTag) knownSpellTag);
            knownSpells.put(spellInst.spell(), spellInst.level());
        });
        tag.getList("EquippedSpells", Tag.TAG_COMPOUND).forEach(equippedSpellTagRaw -> {
            CompoundTag equippedSpellTag = (CompoundTag) equippedSpellTagRaw;
            int slot = equippedSpellTag.getByte("Slot");
            SpellInstance spellInst = SpellInstance.fromTag(equippedSpellTag.getCompound("SpellInstance"));
            equippedSpells[slot] = spellInst;
        });
    }
}
