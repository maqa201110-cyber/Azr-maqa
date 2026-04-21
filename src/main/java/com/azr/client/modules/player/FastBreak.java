package com.azr.client.modules.player;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;

public class FastBreak extends Module {
    public final Setting<Double> multiplier = register(new Setting<>("Multiplier", 2.0, 1.0, 5.0));
    public FastBreak() { super("FastBreak", "Speeds up block breaking (mixin)", 0, ModuleCategory.PLAYER); }

    public static double mul() {
        var m = (FastBreak) com.azr.client.module.ModuleManager.get().byClass(FastBreak.class);
        return (m != null && m.isEnabled()) ? m.multiplier.asDouble() : 1.0;
    }
}
