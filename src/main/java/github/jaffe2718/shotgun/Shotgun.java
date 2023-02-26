package github.jaffe2718.shotgun;

import github.jaffe2718.shotgun.entity.GrapeshotEntity;
import github.jaffe2718.shotgun.init.ItemRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Shotgun implements ModInitializer {
    public static final String ModID = "shotgun";
    public static Logger LOGGER = LoggerFactory.getLogger(ModID);

    public static final EntityType<GrapeshotEntity> GRAPESHOT = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(Shotgun.ModID, "grapeshot"),
        FabricEntityTypeBuilder.<GrapeshotEntity>create(SpawnGroup.MISC, GrapeshotEntity::new)
            .dimensions(EntityDimensions.fixed(0.15F, 0.15F)) // dimensions in Minecraft units of the projectile
            .disableSaving().trackRangeChunks(16)
            .trackRangeBlocks(16)
            .trackedUpdateRate(8) // necessary for all thrown projectiles (as it prevents it from breaking, lol)
            .build() // VERY IMPORTANT DONT DELETE FOR THE LOVE OF GOD PSLSSSSSS
    );

    @Override
    public void onInitialize() {
        ItemRegistry.register();
    }
}
