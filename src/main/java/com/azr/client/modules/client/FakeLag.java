package com.azr.client.modules.client;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;

public class FakeLag extends Module {
    public final Setting<Double> ticks = register(new Setting<>("Ticks", 5.0, 1.0, 30.0));
    public FakeLag() { super("FakeLag", "Hold outgoing position packets briefly (mixin)", 0, ModuleCategory.CLIENT); }

    public static int delay() {
        var m = (FakeLag) com.azr.client.module.ModuleManager.get().byClass(FakeLag.class);
        return (m != null && m.isEnabled()) ? m.ticks.asInt() : 0;
    }
}
