package com.azr.client.modules.combat;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class WTap extends Module {
    public WTap() { super("WTap", "Briefly stop sprinting on hit for extra knockback", 0, ModuleCategory.COMBAT); }

    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(WTap.class);
        return m != null && m.isEnabled();
    }
}
