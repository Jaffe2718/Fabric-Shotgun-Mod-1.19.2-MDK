package github.jaffe2718.shotgun.mixin;

import github.jaffe2718.shotgun.item.ShotgunItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {  // 用于修改第三人称持枪动作
    @Inject(method = "setModelPose", at = @At("TAIL"))
    private void setModelPose(AbstractClientPlayerEntity player, CallbackInfo ci) {
        PlayerEntityModel playerEntityModel = (PlayerEntityModel)((PlayerEntityRenderer)(Object)this).getModel();
        if (!player.isUsingItem() &&
                (player.getStackInHand(player.getActiveHand()).getItem() instanceof ShotgunItem) &&
                ShotgunItem.isCharged((player.getStackInHand(player.getActiveHand())))) {
            if (player.getActiveHand() == Hand.MAIN_HAND) {
                playerEntityModel.rightArmPose = BipedEntityModel.ArmPose.SPYGLASS;
            }
            if (player.getActiveHand() == Hand.OFF_HAND) {
                playerEntityModel.leftArmPose = BipedEntityModel.ArmPose.SPYGLASS;
            }
        }
    }
}
