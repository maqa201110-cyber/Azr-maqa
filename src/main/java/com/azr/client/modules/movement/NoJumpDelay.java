package com.azr.client.modules.movement;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class NoJumpDelay extends Module {
    public NoJumpDelay() { super("NoJumpDelay", "Removes jump cooldown", 0, ModuleCategory.MOVEMENT); }

    @Override
    public void onTick() {
        if (inGame()) ((com.azr.client.mixin.PlayerEntityAccessor) player()).setJumpingCooldown(0);
    }

    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(NoJumpDelay.class);
        return m != null && m.isEnabled();
    }
}
