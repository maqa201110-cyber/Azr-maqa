package com.azr.client.modules.combat;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;

public class HitboxExpand extends Module {
    public final Setting<Double> size = register(new Setting<>("Size", 0.3, 0.0, 1.5));

    public HitboxExpand() { super("HitboxExpand", "Expands player hitboxes (mixin)", 0, ModuleCategory.COMBAT); }

    public static double extra() {
        var m = (HitboxExpand) com.azr.client.module.ModuleManager.get().byClass(HitboxExpand.class);
        return (m != null && m.isEnabled()) ? m.size.asDouble() : 0.0;
    }
}
