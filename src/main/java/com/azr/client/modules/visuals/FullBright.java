package com.azr.client.modules.visuals;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class FullBright extends Module {
    private double original;

    public FullBright() { super("FullBright", "Maximum brightness", 0, ModuleCategory.VISUALS); }

    @Override
    public void onEnable() {
        original = mc.options.getGamma().getValue();
        mc.options.getGamma().setValue(15.0);
    }

    @Override
    public void onDisable() {
        mc.options.getGamma().setValue(original == 0 ? 1.0 : original);
    }
}
