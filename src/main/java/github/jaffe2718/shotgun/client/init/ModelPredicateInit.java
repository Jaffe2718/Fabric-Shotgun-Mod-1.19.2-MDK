package github.jaffe2718.shotgun.client.init;

import github.jaffe2718.shotgun.Shotgun;
import github.jaffe2718.shotgun.init.ItemRegistry;
import github.jaffe2718.shotgun.item.ShotgunItem;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class ModelPredicateInit {
    public static void register() {

        ModelPredicateProviderRegistry.register(ItemRegistry.SHOTGUN, new Identifier(Shotgun.ModID,"load"),
                (itemStack, clientWorld, livingEntity, seed) -> {
                    if (livingEntity!=null && livingEntity.isUsingItem() && itemStack.isOf(ItemRegistry.SHOTGUN) && livingEntity.getActiveItem().equals(itemStack)) {
                        return (float)(itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / (float)itemStack.getMaxUseTime();
                    } else {
                        return 0.0F;
                    }
                }
                );

        ModelPredicateProviderRegistry.register(ItemRegistry.SHOTGUN, new Identifier(Shotgun.ModID, "loaded"),
                (itemStack, clientWorld, livingEntity, seed) -> {
                    if (ShotgunItem.isCharged(itemStack)) {
                        if (livingEntity==null) {
                            return 1.0F;
                        } else if (!livingEntity.isUsingItem() || !livingEntity.getActiveItem().isOf(ItemRegistry.SHOTGUN)) {
                            return 1.0F;
                        } else {
                            return 0.0F;
                        }
                    } else {
                        return 0.0F;
                    }
                });
    }
}
