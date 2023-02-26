package github.jaffe2718.shotgun.init;

import github.jaffe2718.shotgun.Shotgun;
import github.jaffe2718.shotgun.item.ShotgunItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class ItemRegistry {
    public static final Item GRAPESHOT = new Item(new Item.Settings().group(ItemGroup.COMBAT));
    public static final Item SHOTGUN = new ShotgunItem(new Item.Settings().maxDamage(100).group(ItemGroup.COMBAT));

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(Shotgun.ModID, "grapeshot"), GRAPESHOT);
        Registry.register(Registry.ITEM, new Identifier(Shotgun.ModID, "shotgun"), SHOTGUN);
    }
}
