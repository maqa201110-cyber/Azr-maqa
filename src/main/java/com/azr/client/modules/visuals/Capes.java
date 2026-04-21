package com.azr.client.modules.visuals;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class Capes extends Module {
    public Capes() { super("Capes", "Render custom capes (render mixin)", 0, ModuleCategory.VISUALS); }
    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(Capes.class);
        return m != null && m.isEnabled();
    }
}
