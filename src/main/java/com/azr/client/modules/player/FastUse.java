package com.azr.client.modules.player;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class FastUse extends Module {
    public FastUse() { super("FastUse", "Removes item use delay (mixin)", 0, ModuleCategory.PLAYER); }

    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(FastUse.class);
        return m != null && m.isEnabled();
    }
}
