package com.azr.client.modules.visuals;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class ESP extends Module {
    public ESP() { super("ESP", "Outline players through walls (render mixin)", 0, ModuleCategory.VISUALS); }
    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(ESP.class);
        return m != null && m.isEnabled();
    }
}
