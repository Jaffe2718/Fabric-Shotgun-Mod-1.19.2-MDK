package github.jaffe2718.shotgun.mixin;


import github.jaffe2718.shotgun.item.ShotgunItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Accessor
    abstract MinecraftClient getClient();

    @Shadow
    abstract void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress);

    @Shadow
    abstract void applySwingOffset(MatrixStack matrices, Arm arm, float equipProgress);

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
    private void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand,
                                       float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
                                       VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (item.getItem() instanceof ShotgunItem) {
            boolean bl = hand == Hand.MAIN_HAND;
            Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
            matrices.push();
            boolean bl2 = ShotgunItem.isCharged(item);                                                   // 是否装填
            boolean bl3 = arm == Arm.RIGHT;                                                              // 是否右手持物品
            int i = bl3 ? 1 : -1;
            if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == Hand.MAIN_HAND) { // 装填中
                this.applyEquipOffset(matrices, arm, equipProgress);
                matrices.translate((float)i * -0.4785682f, -0.094387f, 0.05731530860066414);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-11.935f));
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float)i * 65.3f));
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)i * -9.785f));
                float f = (float)item.getMaxUseTime() - ((float)getClient().player.getItemUseTimeLeft() - tickDelta + 1.0f);
                float g = f / (float)item.getMaxUseTime();
                if (g > 1.0f) {
                    g = 1.0f;
                }
                if (g > 0.1f) {
                    float h = MathHelper.sin((f - 0.1f) * 1.3f);
                    float j = g - 0.1f;
                    float k = h * j;
                    matrices.translate(k * 0.0f, k * 0.004f, k * 0.0f);
                }
                matrices.translate(g * 0.0f, g * 0.0f, g * 0.04f);
                matrices.scale(1.0f, 1.0f, 1.0f + g * 0.2f);
                matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion((float)i * 45.0f));
                ((HeldItemRenderer)(Object)this).renderItem(player, item, bl3 ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, !bl3, matrices, vertexConsumers, light);
                ci.cancel();
            } else if (!player.isUsingItem() && bl2 && player.getActiveHand() == Hand.MAIN_HAND) {    // 已经装填，瞄准时
                float f = -0.4f * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
                float g = 0.2f * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float)Math.PI * 2));
                float h = -0.2f * MathHelper.sin(swingProgress * (float)Math.PI);
                matrices.translate((float)i * f, g, h);
                applyEquipOffset(matrices, arm, equipProgress);
                applySwingOffset(matrices, arm, swingProgress);
                if (bl2 && swingProgress < 0.001f && bl) {
                    matrices.translate((float)i * -0.641864f, 0.0, 0.0);
                    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float)i * 10.0f));
                }
                ((HeldItemRenderer)(Object)this).renderItem(player, item, bl3 ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, !bl3, matrices, vertexConsumers, light);
                ci.cancel();
            }
        }
    }
}
