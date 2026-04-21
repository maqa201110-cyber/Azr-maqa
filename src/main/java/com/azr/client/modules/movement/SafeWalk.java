package com.azr.client.modules.movement;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class SafeWalk extends Module {
    public SafeWalk() { super("SafeWalk", "Don't walk off ledges (mixin)", 0, ModuleCategory.MOVEMENT); }

    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(SafeWalk.class);
        return m != null && m.isEnabled();
    }
}
