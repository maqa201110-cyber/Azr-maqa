package com.azr.client.modules.movement;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class NoSlow extends Module {
    public NoSlow() { super("NoSlow", "Removes slowdown when using items (mixin)", 0, ModuleCategory.MOVEMENT); }

    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(NoSlow.class);
        return m != null && m.isEnabled();
    }
}
