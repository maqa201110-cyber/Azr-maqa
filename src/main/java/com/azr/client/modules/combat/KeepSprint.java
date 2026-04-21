package com.azr.client.modules.combat;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class KeepSprint extends Module {
    public KeepSprint() { super("KeepSprint", "Don't lose sprint when attacking (mixin)", 0, ModuleCategory.COMBAT); }

    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(KeepSprint.class);
        return m != null && m.isEnabled();
    }
}
