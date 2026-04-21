package com.azr.client.modules.client;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;

public class Timer extends Module {
    public final Setting<Double> speed = register(new Setting<>("Speed", 1.5, 0.1, 5.0));

    public Timer() { super("Timer", "Modify game tick speed (mixin)", 0, ModuleCategory.CLIENT); }

    public static float speed() {
        var m = (Timer) com.azr.client.module.ModuleManager.get().byClass(Timer.class);
        return (m != null && m.isEnabled()) ? m.speed.asFloat() : 1.0f;
    }
    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(Timer.class);
        return m != null && m.isEnabled();
    }
}
