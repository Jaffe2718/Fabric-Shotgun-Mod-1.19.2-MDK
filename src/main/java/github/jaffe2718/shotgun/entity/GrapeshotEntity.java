package github.jaffe2718.shotgun.entity;

import github.jaffe2718.shotgun.Shotgun;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class GrapeshotEntity extends ThrownItemEntity {
    public GrapeshotEntity(EntityType<? extends GrapeshotEntity> entityType, World world) {
        super(entityType, world);
    }

    public GrapeshotEntity(World world, LivingEntity owner) {
        super(Shotgun.GRAPESHOT, owner, world);
    }

    public GrapeshotEntity(World world, double x, double y, double z) {
        super(Shotgun.GRAPESHOT, x, y, z, world);
    }

    @Override
    protected void onEntityHit(EntityHitResult result) {  // 击中实体
        super.onEntityHit(result);
        Entity target = result.getEntity();
        int protection = 1;
        for (ItemStack armorStuck: target.getArmorItems()) {
            if (armorStuck.getItem() instanceof ArmorItem armor) {
                protection += armor.getProtection() + EnchantmentHelper.getLevel(Enchantments.PROJECTILE_PROTECTION, armorStuck);
            }
        }
        if (this.getOwner() != null) {
            float piercing = 0.0F;
            if (this.getOwner() instanceof PlayerEntity player) {
                piercing += EnchantmentHelper.getLevel(Enchantments.PIERCING, player.getMainHandStack());
            }
            Shotgun.LOGGER.info(""+((27.5F + piercing) / (float) Math.pow(Math.max(result.getPos().distanceTo(this.getOwner().getPos()), 2.5F) * protection, 0.333333333F)));
            target.damage(DamageSource.thrownProjectile(this, this.getOwner()),
                    (27.5F + piercing) / (float) Math.pow(Math.max(result.getPos().distanceTo(this.getOwner().getPos()), 2.5F) * protection, 0.333333333F));
        } else {
            target.damage(DamageSource.thrownProjectile(this, this.getOwner()),
                    10.0F / (float) Math.sqrt(Math.max(this.distanceTraveled, 2.5F) * protection));
        }
    }

    @Override
    public void tick() {                                  // 当霰弹实体存在时
        super.tick();
        if (this.getOwner()!=null && this.distanceTo(this.getOwner()) > 35.0F) {    // 射程最多35m
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {                     // 使用默认物品的模型
        return Items.POLISHED_BLACKSTONE_BUTTON;
    }

    @Override
    protected void onCollision(HitResult hitResult) {     // 击中事件产生时
        super.onCollision(hitResult);
        Vec3d hitPos = hitResult.getPos();
        if (this.getWorld() instanceof ServerWorld serverWorld) {                              // 击中成粒子
            Random rd = new Random();
            serverWorld.spawnParticles(ParticleTypes.CRIT,
                    hitPos.x, hitPos.y, hitPos.z,
                    2,
                    rd.nextDouble()-0.5D, rd.nextDouble()-0.5D, rd.nextDouble()-0.5D,
                    0.25D
            );
            this.world.sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.discard();
        }
    }
}
