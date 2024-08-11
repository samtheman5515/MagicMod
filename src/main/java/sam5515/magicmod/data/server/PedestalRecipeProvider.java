package sam5515.magicmod.data.server;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import sam5515.magicmod.common.api.recipe.PedestalRecipe;
import sam5515.magicmod.common.api.spell.Spell;
import sam5515.magicmod.common.api.spell.SpellInstance;
import sam5515.magicmod.common.item.ScrollItem;
import sam5515.magicmod.common.registry.MMItems;
import sam5515.magicmod.common.registry.MMSpells;
import sam5515.magicmod.common.util.SpellUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PedestalRecipeProvider implements DataProvider {
    private final DataGenerator gen;
    private final String modID;

    public PedestalRecipeProvider(DataGenerator gen, String modID) {
        this.gen = gen;
        this.modID = modID;
    }


    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        PackOutput.PathProvider pathProvider = gen.getPackOutput().createPathProvider(PackOutput.Target.DATA_PACK,
                "pedestal_recipes");
        List<CompletableFuture<?>> futures = new ArrayList<>();
        Set<ResourceLocation> keys = new HashSet<>();
        buildRecipes(recipe -> {
            ResourceLocation key = recipe.getId();
            if (!keys.add(key)){
                throw new IllegalStateException("Duplicate Recipes: " + key);
            }
            futures.add(DataProvider.saveStable(pOutput, recipe.toJson(), pathProvider.json(key)));
        });
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Pedestal Recipes";
    }
    protected Ingredient scrollStack (Spell spell, int level){
        ItemStack stack = new ItemStack(MMItems.scroll.get());
        ScrollItem.writeSpell(stack, new SpellInstance(spell, level));
        return PartialNBTIngredient.of(MMItems.scroll.get(), stack.getTag());
    }
    protected PedestalRecipe.Builder scroll(Spell spell, int level){
        ItemStack stack = new ItemStack(MMItems.scroll.get());
        ScrollItem.writeSpell(stack, new SpellInstance(spell, level));
        return new PedestalRecipe.Builder()
                .id(new ResourceLocation(modID, "scroll_" + SpellUtil.getKey(spell).getPath() + "_" + level))
                .manaCost(50)
                .center(Items.PAPER)
                .result(stack);
    }
    public void buildRecipes(Consumer<PedestalRecipe> output){
        scroll(MMSpells.BLINK.get(), 0)
                .outer(Items.ENDER_PEARL)
                .outer(Items.CHORUS_FRUIT)
                .save(output);
        scroll(MMSpells.BLINK.get(), 1)
                .outer(scrollStack(MMSpells.BLINK.get(), 0))
                .outer(Items.SCULK)
                .outer(Items.DRAGON_BREATH)
                .save(output);
    }
}
