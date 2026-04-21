package com.azr.client.modules.combat;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;

public class Reach extends Module {
    public final Setting<Double> distance = register(new Setting<>("Distance", 4.0, 3.0, 6.0));

    public Reach() { super("Reach", "Extends attack reach (mixin-based)", 0, ModuleCategory.COMBAT); }

    public static double extra() {
        Reach r = (Reach) com.azr.client.module.ModuleManager.get().byClass(Reach.class);
        return (r != null && r.isEnabled()) ? r.distance.asDouble() - 3.0 : 0.0;
    }
}
