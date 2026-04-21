package com.azr.client.modules.visuals;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class ChinaHat extends Module {
    public ChinaHat() { super("ChinaHat", "Cone hat over players (render mixin)", 0, ModuleCategory.VISUALS); }
    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(ChinaHat.class);
        return m != null && m.isEnabled();
    }
}
