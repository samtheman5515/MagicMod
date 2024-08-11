package sam5515.magicmod.common.api.recipe;

import com.google.gson.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class PedestalRecipe {
    private ResourceLocation id;
    public final int manaCost;
    public final Ingredient center;
    public final List<Ingredient> outer;
    private final ItemStack result;

    public PedestalRecipe(ResourceLocation id, int manaCost, Ingredient center, List<Ingredient> outer, ItemStack result) {
        this.id = id;
        this.manaCost = manaCost;
        this.center = center;
        this.outer = outer;
        this.result = result;
    }

    public ResourceLocation getId() {
        return id;
    }

    public ItemStack getResult() {
        return result.copy();
    }

    public void setId(ResourceLocation id) {
        if (this.id == null) {
            this.id = id;
        }
    }
    public JsonElement toJson(){
        JsonObject json = new JsonObject();
        json.addProperty("mana", manaCost);
        json.add("center", center.toJson());
        JsonArray outerJson = new JsonArray();
        outer.forEach(ingredient -> outerJson.add(ingredient.toJson()));
        json.add("outer", outerJson);
        JsonObject resultJson = new JsonObject();
        resultJson.addProperty("item", ForgeRegistries.ITEMS.getKey(result.getItem()).toString());
        if (result.getCount() > 1){
            resultJson.addProperty("count", result.getCount());
        }
        if (result.hasTag()){
            resultJson.addProperty("nbt", result.getTag().toString());
        }
        json.add("result", resultJson);
        return json;
    }
    public void toNetwork(FriendlyByteBuf buf){
        if (id == null){
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeResourceLocation(id);
        }
        buf.writeInt(manaCost);
        center.toNetwork(buf);
        buf.writeByte(outer.size());
        outer.forEach(ingredient -> ingredient.toNetwork(buf));
        buf.writeItem(result);
    }
    public PedestalCraftItems test(PedestalCraftItems input){
        if (outer.size() != input.outer().size()){
            return null;
        }
        if (!center.test(input.center())){
            return null;
        }
        IntList usedItems = new IntArrayList(outer.size());
        for (Ingredient ingredient : outer) {
            for (int i = 0; i < input.outer().size(); i++) {
                ItemStack stack = input.outer().get(i);
                if (!usedItems.contains(i) && ingredient.test(stack)){
                    usedItems.add(i);
                    break;
                }
            }
        }
        if (usedItems.size() != outer.size()){
            return null;
        }
        List<ItemStack> outerResult = new ArrayList<>();
        for (int i = 0; i < input.outer().size(); i++) {
            outerResult.add(ItemStack.EMPTY);
        }
        return new PedestalCraftItems(result.copy(), outerResult);

    }
    public static PedestalRecipe fromJson(JsonElement elem){
        JsonObject json = elem.getAsJsonObject();
        int manaCost = json.get("mana").getAsInt();
        Ingredient center = Ingredient.fromJson(json.get("center").getAsJsonObject());
        List<Ingredient> outer = new ArrayList<>();
        JsonArray outerJson = json.get("outer").getAsJsonArray();
        outerJson.forEach(ingredientJson -> outer.add(Ingredient.fromJson(ingredientJson)));
        ItemStack result = CraftingHelper.getItemStack(json.get("result").getAsJsonObject(), true);
        return new PedestalRecipe(null, manaCost, center, outer, result);
    }
    public static PedestalRecipe fromNetwork(FriendlyByteBuf buf){
        ResourceLocation id = null;
        if(buf.readBoolean()){
            id = buf.readResourceLocation();
        }
        int manaCost = buf.readShort();
        Ingredient center = Ingredient.fromNetwork(buf);
        int count = buf.readByte();
        List<Ingredient> outer = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            outer.add(Ingredient.fromNetwork(buf));
        }
        ItemStack result = buf.readItem();
        return new PedestalRecipe(id, manaCost, center, outer, result);
    }
    public static class Serializer implements JsonSerializer<PedestalRecipe>, JsonDeserializer<PedestalRecipe>{

        @Override
        public PedestalRecipe deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return fromJson(jsonElement);
        }

        @Override
        public JsonElement serialize(PedestalRecipe recipe, Type type, JsonSerializationContext jsonSerializationContext) {
            return recipe.toJson();
        }
    }
    public static class Builder {
        private ResourceLocation id;
        private int manaCost;
        private Ingredient center;
        private final List<Ingredient> outer;
        private ItemStack result;
        public Builder(){
            id = null;
            manaCost = 0;
            center = null;
            outer = new ArrayList<>();
            result = ItemStack.EMPTY;
        }
        public Builder id(ResourceLocation id){
            this.id = id;
            return this;
        }

        public Builder manaCost(int manaCost) {
            this.manaCost = manaCost;
            return this;
        }

        public Builder center(Ingredient center) {
            this.center = center;
            return this;
        }
        public Builder center(ItemLike... possibleItems){
            return center(Ingredient.of(possibleItems));
        }
        public Builder center(TagKey<Item> tag){
            return center(Ingredient.of(tag));
        }
        public Builder outer(Ingredient... ingredients){
            Collections.addAll(outer, ingredients);
            return this;
        }
        public Builder outer(ItemLike... possibleItems){
            return outer(Ingredient.of(possibleItems));
        }
        public Builder outer(TagKey<Item> tag){
            return outer(Ingredient.of(tag));
        }

        public Builder result(ItemStack result) {
            this.result = result;
            return this;
        }
        public Builder result(ItemLike item, int count){
            return result(new ItemStack(item, count));
        }
        public Builder result(ItemLike item){
            return result(item, 1);
        }
        public PedestalRecipe build(boolean setDefaultID){
            if (center == null){
                throw new IllegalStateException("Center ingredient must be defined");
            }
            if (result.isEmpty()){
                throw new IllegalStateException("Result must be defined");
            }
            if (outer.isEmpty()){
                throw new IllegalStateException("Outer ingredients must not be empty");
            }
            if (setDefaultID && id == null){
                id = ForgeRegistries.ITEMS.getKey(result.getItem());
            }
            return new PedestalRecipe(id, manaCost, center, outer, result);
        }
        public PedestalRecipe build(){
            return build(true);
        }
        public void save(Consumer<PedestalRecipe> output, boolean setDefaultID){
            output.accept(build(setDefaultID));
        }
        public void save(Consumer<PedestalRecipe> output){
            save(output, true);
        }

        }
}
