package sam5515.magicmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import sam5515.magicmod.common.block.entity.PedestalBlockEntity;

public class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity> {
    private final ItemRenderer itemRenderer;
    public PedestalRenderer(BlockEntityRendererProvider.Context context){
        itemRenderer = context.getItemRenderer();
    }
    @Override
    public void render(PedestalBlockEntity pedestal, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (pedestal.hasItem()){
            ItemStack stack = pedestal.getItem();
            pose.pushPose();
            pose.translate(0.5f, 1.2f, 0.5f);
            pose.scale(0.4f, 0.4f, 0.4f);
            float rotation = pedestal.getItemRotation(partialTick);
            pose.mulPose(Axis.YP.rotation(rotation));
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, pose, buffer, pedestal.getLevel(), (int) pedestal.getBlockPos().asLong());
            pose.popPose();
        }
    }
}
