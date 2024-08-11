package sam5515.magicmod.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import sam5515.magicmod.common.block.entity.PedestalBlockEntity;

public class PedestalBlock extends BaseEntityBlock {
    private static final VoxelShape shape = Shapes.or(
            Block.box(1, 0, 1, 15, 2, 15),
            Block.box(3, 2, 3, 13, 4, 13),
            Block.box(5, 4, 5, 11, 14, 11),
            Block.box(3, 14, 3, 13, 16, 13)
    );
    public PedestalBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PedestalBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return shape;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
       if (level.getBlockEntity(pos) instanceof PedestalBlockEntity pedestal){
           ItemStack heldItem = player.getItemInHand(hand);
           if (heldItem.isEmpty() || pedestal.hasItem()){
               ItemStack removedItem = pedestal.removeItem();
               if (!player.addItem(removedItem)){
                   ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, removedItem);
                   level.addFreshEntity(itemEntity);
               }
           } else {
               pedestal.setItem(heldItem.split(1));
           }
           level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
           return InteractionResult.sidedSuccess(level.isClientSide);

       }
       return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof PedestalBlockEntity pedestal && pedestal.hasItem()){
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), pedestal.getItem());
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
