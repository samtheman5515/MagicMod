package sam5515.magicmod.common.spell;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import sam5515.magicmod.common.api.spell.Spell;
import sam5515.magicmod.common.block.entity.PedestalBlockEntity;
import sam5515.magicmod.common.util.SpellUtil;

public class CraftSpell implements Spell {
    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getCost(int level) {
        return 50;
    }

    @Override
    public boolean activate(int level, LivingEntity caster) {
        Vec3 from = caster.getEyePosition();
        Vec3 to = from.add(caster.getLookAngle().multiply(5, 5, 5));
        BlockHitResult hit = caster.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));
        return hit.getType() == HitResult.Type.BLOCK && caster.level().getBlockEntity(hit.getBlockPos()) instanceof PedestalBlockEntity pedestal && pedestal.attemptCraft();
    }
}
