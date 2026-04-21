package com.azr.client.modules.player;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;

public class SpinBot extends Module {
    public final Setting<Double> speed = register(new Setting<>("Speed", 18.0, 1.0, 90.0));

    public SpinBot() { super("SpinBot", "Continuously rotate yaw", 0, ModuleCategory.PLAYER); }

    @Override
    public void onTick() {
        if (!inGame()) return;
        player().setYaw(player().getYaw() + speed.asFloat());
    }
}
