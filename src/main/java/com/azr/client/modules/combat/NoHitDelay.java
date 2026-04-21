package com.azr.client.modules.combat;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class NoHitDelay extends Module {
    public NoHitDelay() { super("NoHitDelay", "Removes attack cooldown delay (mixin)", 0, ModuleCategory.COMBAT); }

    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(NoHitDelay.class);
        return m != null && m.isEnabled();
    }
}
