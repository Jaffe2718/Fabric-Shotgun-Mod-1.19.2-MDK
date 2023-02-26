package github.jaffe2718.shotgun.client;

import github.jaffe2718.shotgun.Shotgun;
import github.jaffe2718.shotgun.client.init.ModelPredicateInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

@Environment(EnvType.CLIENT)
public class ShotgunClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelPredicateInit.register();
        EntityRendererRegistry.register(Shotgun.GRAPESHOT, FlyingItemEntityRenderer::new);
    }
}
