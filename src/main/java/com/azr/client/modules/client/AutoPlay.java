package com.azr.client.modules.client;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class AutoPlay extends Module {
    public AutoPlay() { super("AutoPlay", "Auto-rejoin on disconnect", 0, ModuleCategory.CLIENT); }
    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(AutoPlay.class);
        return m != null && m.isEnabled();
    }
}
