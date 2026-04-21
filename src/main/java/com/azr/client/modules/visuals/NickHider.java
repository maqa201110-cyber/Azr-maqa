package com.azr.client.modules.visuals;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class NickHider extends Module {
    public NickHider() { super("NickHider", "Hide your real name (render mixin)", 0, ModuleCategory.VISUALS); }
    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(NickHider.class);
        return m != null && m.isEnabled();
    }
    public static String fakeName() { return "Player"; }
}
