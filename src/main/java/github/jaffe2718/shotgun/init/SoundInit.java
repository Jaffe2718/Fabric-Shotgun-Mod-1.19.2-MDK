package github.jaffe2718.shotgun.init;

import github.jaffe2718.shotgun.Shotgun;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundInit {
    public static final SoundEvent CAN_LOAD, LOADED, LOADING, SHOOT;

    static {
        CAN_LOAD = new SoundEvent(new Identifier(Shotgun.ModID, "shotgun_can_load"));
        LOADED = new SoundEvent(new Identifier(Shotgun.ModID, "shotgun_loaded"));
        LOADING = new SoundEvent(new Identifier(Shotgun.ModID, "shotgun_loading"));
        SHOOT = new SoundEvent(new Identifier(Shotgun.ModID, "shotgun_shoot"));
    }
}
