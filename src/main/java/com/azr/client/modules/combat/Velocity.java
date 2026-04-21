package com.azr.client.modules.combat;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;

public class Velocity extends Module {

    public final Setting<Double> horizontal = register(new Setting<>("Horizontal", 0.0, 0.0, 100.0));
    public final Setting<Double> vertical   = register(new Setting<>("Vertical",   0.0, 0.0, 100.0));

    public Velocity() { super("Velocity", "Cancel/reduce knockback", 0, ModuleCategory.COMBAT); }

    public static Velocity instance() {
        return (Velocity) com.azr.client.module.ModuleManager.get().byClass(Velocity.class);
    }

    public static boolean isActive() {
        Velocity v = instance();
        return v != null && v.isEnabled();
    }

    public static double horizontalMul() {
        Velocity v = instance();
        return v == null ? 1.0 : v.horizontal.asDouble() / 100.0;
    }

    public static double verticalMul() {
        Velocity v = instance();
        return v == null ? 1.0 : v.vertical.asDouble() / 100.0;
    }
}
