package sam5515.magicmod.common.spell;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.Vec3;
import sam5515.magicmod.common.api.spell.Spell;

public class FireBallSpell implements Spell {
    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getCost(int level) {
        return level * 10 + 10;
    }

    @Override
    public boolean activate(int level, LivingEntity caster) {
        Vec3 look = caster.getLookAngle();
        SmallFireball fireball = new SmallFireball(caster.level(), caster, look.x, look.y, look.z);
        fireball.setPos(caster.getEyePosition());
        caster.level().addFreshEntity(fireball);
        return true;
    }
}
