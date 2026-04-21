package com.azr.client.modules.visuals;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class Glint extends Module {
    public Glint() { super("Glint", "Glint visual on items (render mixin)", 0, ModuleCategory.VISUALS); }
    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(Glint.class);
        return m != null && m.isEnabled();
    }
}
