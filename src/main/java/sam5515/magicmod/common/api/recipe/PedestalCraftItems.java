package sam5515.magicmod.common.api.recipe;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record PedestalCraftItems(ItemStack center, List<ItemStack> outer) {
}
