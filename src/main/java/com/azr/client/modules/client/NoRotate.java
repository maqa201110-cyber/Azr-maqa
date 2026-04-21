package com.azr.client.modules.client;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class NoRotate extends Module {
    public NoRotate() { super("NoRotate", "Don't visually rotate to KillAura targets (mixin)", 0, ModuleCategory.CLIENT); }
    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(NoRotate.class);
        return m != null && m.isEnabled();
    }
}
