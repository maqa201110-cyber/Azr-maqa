package com.azr.client.modules.visuals;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;

public class NameTags extends Module {
    public final Setting<Double> scale = register(new Setting<>("Scale", 1.5, 0.5, 4.0));
    public NameTags() { super("NameTags", "Better player nametags (handled in render mixin)", 0, ModuleCategory.VISUALS); }

    public static double scale() {
        var m = (NameTags) com.azr.client.module.ModuleManager.get().byClass(NameTags.class);
        return (m != null && m.isEnabled()) ? m.scale.asDouble() : 1.0;
    }
    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(NameTags.class);
        return m != null && m.isEnabled();
    }
}
