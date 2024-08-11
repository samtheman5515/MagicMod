package sam5515.magicmod.common.api.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import sam5515.magicmod.MagicMod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedestalRecipeManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(PedestalRecipe.class, new PedestalRecipe.Serializer())
            .create();
    private final Map<ResourceLocation, PedestalRecipe> recipes;

    public PedestalRecipeManager() {
        super(GSON, "pedestal_recipes");
        recipes = new HashMap<>();
    }
    private void add(ResourceLocation key, PedestalRecipe recipe){
        recipes.put(key, recipe);
    }
    public PedestalRecipe getRecipe(ResourceLocation key){
        return recipes.get(key);
    }
    public List<PedestalRecipeMatch> match(ItemStack centerItem, List<ItemStack> outerItems){
        List<PedestalRecipeMatch> matches = new ArrayList<>();
        PedestalCraftItems input = new PedestalCraftItems(centerItem, outerItems);
        recipes.values().forEach(recipe -> {
            PedestalCraftItems result = recipe.test(input);
            if (result != null){
                matches.add(new PedestalRecipeMatch(recipe, result));
            }
        });
        return matches;
    }
    public void clear(){
        recipes.clear();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries, ResourceManager manager, ProfilerFiller profilerFiller) {
        clear();
        entries.forEach((location, json)-> {
            try{
                PedestalRecipe recipe = GSON.fromJson(json, PedestalRecipe.class);
                recipe.setId(location);
                add(location, recipe);

            } catch(JsonParseException e){
                MagicMod.LOGGER.warn("Could not deserialize pedestal recipe " + location, e);
            }
        });
    }
    public static final PedestalRecipeManager instance = new PedestalRecipeManager();
}
