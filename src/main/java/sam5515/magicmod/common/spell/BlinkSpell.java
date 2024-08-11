package sam5515.magicmod.common.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import sam5515.magicmod.common.api.spell.Spell;

public class BlinkSpell implements Spell {
    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getCost(int level) {
        return 80;
    }

    @Override
    public boolean activate(int level, LivingEntity caster) {
        if (caster.level().isClientSide){
            return false;
        }
        int range = (level + 1) * 70;
        Vec3 from = caster.getEyePosition();
        Vec3 to = from.add(caster.getLookAngle().multiply(range, range, range));
        BlockHitResult hit = caster.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));
        if(hit.getType() != HitResult.Type.BLOCK){
            return false;
        }
        BlockPos pos = hit.getBlockPos().relative(hit.getDirection());
        caster.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        return true;
    }
}
