package com.azr.client.modules.visuals;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class Trajectories extends Module {
    public Trajectories() { super("Trajectories", "Show projectile arc (renderer hook)", 0, ModuleCategory.VISUALS); }

    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(Trajectories.class);
        return m != null && m.isEnabled();
    }
}
