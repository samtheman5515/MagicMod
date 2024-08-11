package sam5515.magicmod.common.spell;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import sam5515.magicmod.common.api.spell.Spell;

public class HarmSpell implements Spell {
    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getCost(int level) {
        return 5 + level * 5;
    }

    @Override
    public boolean activate(int level, LivingEntity caster) {
        Vec3 from = caster.getEyePosition();
        Vec3 to = from.add(caster.getLookAngle().scale(5));
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(caster, from, to, new AABB(from, to), entity -> entity instanceof LivingEntity, 5);
        if (hit.getType() == HitResult.Type.ENTITY){
            hit.getEntity().hurt(hit.getEntity().damageSources().indirectMagic(caster, hit.getEntity()), 5 * (level + 1));
            return true;
        } else {
            return false;
        }
    }
}
