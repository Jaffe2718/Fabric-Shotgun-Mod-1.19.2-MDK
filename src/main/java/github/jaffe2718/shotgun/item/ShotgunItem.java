package github.jaffe2718.shotgun.item;


import github.jaffe2718.shotgun.entity.GrapeshotEntity;
import github.jaffe2718.shotgun.init.ItemRegistry;
import github.jaffe2718.shotgun.init.SoundInit;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.function.Predicate;


public class ShotgunItem extends CrossbowItem {

    public ShotgunItem(Settings settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return (stuck) -> stuck.isOf(ItemRegistry.GRAPESHOT);
    }

    @Override
    public int getRange() {
        return 8;
    }

    public static boolean isCharged(ItemStack stack) {
        if (stack.isOf(ItemRegistry.SHOTGUN)) {
            NbtCompound nbtCompound = stack.getNbt();
            return nbtCompound != null && nbtCompound.getBoolean("Charged");
        } else {
            return false;
        }
    }

    public static void setCharged(ItemStack stack, boolean charged) {
        if (stack.isOf(ItemRegistry.SHOTGUN)) {
            NbtCompound nbtCompound = stack.getOrCreateNbt();
            nbtCompound.putBoolean("Charged", charged);
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        int i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack);
        return 30 - 4 * i;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (hand==Hand.MAIN_HAND) {           // 只能右手使用
            ItemStack stack = user.getStackInHand(hand);
            if (isCharged(stack) && !user.getItemCooldownManager().isCoolingDown(this)) {  // 已经装填，就发射
                user.setCurrentHand(hand);
                shoot(world, user, stack);                                                      // 单次射击
                if (EnchantmentHelper.getLevel(Enchantments.MULTISHOT, stack) > 0) {            // 多重射击
                    user.getItemCooldownManager().set(this, 16);
                } else {                                                                        // 单次射击完成，不多重射击
                    setCharged(stack, false);
                }
                if (!world.isClient &&
                        user instanceof ServerPlayerEntity serverPlayer &&
                        !serverPlayer.isCreative()) {                                          // 消耗耐久
                    stack.damage(1, Random.create(), (ServerPlayerEntity) user);
                }
                return TypedActionResult.success(stack);
            } else if ((user.isCreative() || user.getInventory().contains(new ItemStack(ItemRegistry.GRAPESHOT))) &&
                    !user.getItemCooldownManager().isCoolingDown(this)) {                 // 没有装填，就装填
                user.setCurrentHand(hand);
                return TypedActionResult.consume(stack);
            } else {                          // 背包没有子弹，不能用
                return TypedActionResult.fail(stack);
            }
        } else {                              // 左手不能用
            return TypedActionResult.fail(user.getStackInHand(hand));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity owner, int slot, boolean selected) {
        super.inventoryTick(stack, world, owner, slot, selected);
        if (owner instanceof PlayerEntity player && EnchantmentHelper.getLevel(Enchantments.MULTISHOT, stack) > 0) {
            //  多重射击的第二下
            boolean doubleShot = player.getItemCooldownManager().getCooldownProgress(this, 0.0F) < 0.1F &&
                    player.getItemCooldownManager().isCoolingDown(this);
            if (isCharged(stack) && doubleShot && player.getMainHandStack().getItem().equals(this)) {
                player.getItemCooldownManager().update();
                shoot(world, player, stack);
                setCharged(stack, false);
            }
        }
        if (stack.getDamage()>=this.getMaxDamage() && owner instanceof PlayerEntity player) {  // 耐久用完，消失
            player.getInventory().remove((stack1 -> stack1.isOf(this) && stack1.getDamage()>=this.getMaxDamage()), 1, player.getInventory());
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.CROSSBOW;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
//        if (user instanceof PlayerEntity player && player.isUsingItem() &&
//                stack.getItem().equals(this) && player.getItemCooldownManager().isCoolingDown(this)) { // 冷却时期不能用
//            user.stopUsingItem();
//        }
        if (remainingUseTicks==getMaxUseTime(stack)) {
            world.playSound(null,
                    user.getX(), user.getEyeY(), user.getZ(),
                    SoundInit.LOADING, SoundCategory.PLAYERS, 1.0F, 1.0F);
        } else if (!isCharged(stack) && remainingUseTicks==1) {  // 装填成功
            world.playSound(null,
                    user.getX(), user.getEyeY(), user.getZ(),
                    SoundInit.CAN_LOAD, SoundCategory.PLAYERS, 1.0F, 1.0F);
            setCharged(stack, true);
            PlayerEntity player = (PlayerEntity) user;
            if (!player.isCreative()) {
                player.getInventory().remove((stack1 -> stack1.isOf(ItemRegistry.GRAPESHOT)), 1, player.getInventory());
            }
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (remainingUseTicks <= 0 && !world.isClient) { // 装填完成，播放音效
            world.playSound(null,
                    user.getX(), user.getEyeY(), user.getZ(),
                    SoundInit.LOADED, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }

    private void shoot(World world, LivingEntity user, ItemStack stack) {  // 开火的过程
        if (!world.isClient) {        // Server端发射子弹
            world.playSound(null,                       // 开火音效
                    user.getX(), user.getEyeY(), user.getZ(),
                    SoundInit.SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
            ArrayList<GrapeshotEntity> bullets = new ArrayList<>();
            for (int i = 0; i < 9 + EnchantmentHelper.getLevel(Enchantments.PIERCING, stack); i++) {        // 一次发射9+穿透等级个霰弹
                bullets.add(new GrapeshotEntity(world, user));
                bullets.get(i).setPosition(user.getEyePos());
                bullets.get(i).setVelocity(user, user.getPitch(),
                        user.getYaw(),
                        0.0F, 6.0F, 10.0F);
                world.spawnEntity(bullets.get(i));     // 生成实体
            }
        } else {                           // Client端生成粒子效果
            Vec3d muzzle = user.getEyePos().add(user.getRotationVec(1.0F));
            world.addParticle(ParticleTypes.LARGE_SMOKE,
                    muzzle.x, muzzle.y, muzzle.z, 0.0D, 0.0D, 0.0D);
        }
        user.setPitch(user.getPitch() - 8.0F);                                             // 垂直后坐力
        user.setHeadYaw(user.getYaw() + (new java.util.Random()).nextFloat() * 4 - 2.0F);  // 水平后坐力
        // unfinished
    }

}
