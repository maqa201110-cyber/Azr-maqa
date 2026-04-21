package com.azr.client.modules.player;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class FastPlace extends Module {
    public FastPlace() { super("FastPlace", "Removes block placement delay (mixin)", 0, ModuleCategory.PLAYER); }

    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(FastPlace.class);
        return m != null && m.isEnabled();
    }
}
